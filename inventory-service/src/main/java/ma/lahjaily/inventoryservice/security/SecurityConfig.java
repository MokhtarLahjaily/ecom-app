package ma.lahjaily.inventoryservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Public endpoints - NO JWT validation at all for GET products
    @Bean
    @Order(1)
    public SecurityFilterChain publicSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(request -> {
                String path = request.getRequestURI();
                String method = request.getMethod();
                // Only match GET requests to products (public catalog)
                return "GET".equals(method) && 
                       (path.startsWith("/api/products") && !path.equals("/api/products/me"));
            })
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            // No oauth2ResourceServer - truly anonymous
        return http.build();
    }

    // Protected endpoints - require JWT
    @Bean
    @Order(2)
    public SecurityFilterChain protectedSecurityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        http
            .securityMatcher("/**")
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/eureka/**", "/actuator/**").permitAll()
                // /api/products/me requires authentication
                .requestMatchers("/api/products/me").authenticated()
                // Write operations require ADMIN role
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter)));
        return http.build();
    }
}
