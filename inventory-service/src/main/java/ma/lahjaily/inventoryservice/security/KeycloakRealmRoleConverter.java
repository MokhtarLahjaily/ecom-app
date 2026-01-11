package ma.lahjaily.inventoryservice.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts Keycloak JWT roles to Spring Security GrantedAuthorities.
 * Extracts roles from both:
 * - realm_access.roles (realm-level roles)
 * - resource_access.<client-id>.roles (client-level roles)
 */
public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    
    private static final Logger logger = LoggerFactory.getLogger(KeycloakRealmRoleConverter.class);
    
    // Optional: Set this to your Keycloak client ID to extract client-specific roles
    private static final String CLIENT_ID = "ecomm-app"; // Change this to match your Keycloak client ID
    
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Log JWT subject for identification
        logger.debug("Converting JWT for subject: {}", jwt.getSubject());
        logger.debug("JWT claims: {}", jwt.getClaims().keySet());
        
        // Extract realm-level roles from realm_access.roles
        Collection<GrantedAuthority> realmRoles = extractRealmRoles(jwt);
        authorities.addAll(realmRoles);
        
        // Extract client-level roles from resource_access.<client-id>.roles
        Collection<GrantedAuthority> clientRoles = extractClientRoles(jwt, CLIENT_ID);
        authorities.addAll(clientRoles);
        
        // Log all extracted authorities
        logger.info("=== JWT Role Extraction Debug ===");
        logger.info("Subject (user): {}", jwt.getSubject());
        logger.info("Preferred username: {}", jwt.getClaimAsString("preferred_username"));
        logger.info("Realm roles extracted: {}", realmRoles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        logger.info("Client roles extracted ({}): {}", CLIENT_ID, clientRoles.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        logger.info("Total authorities: {}", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        logger.info("=================================");
        
        return authorities;
    }
    
    /**
     * Extracts roles from realm_access.roles claim
     */
    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        
        if (realmAccess == null || realmAccess.isEmpty()) {
            logger.warn("No realm_access claim found in JWT");
            return Collections.emptyList();
        }
        
        Object roles = realmAccess.get("roles");
        logger.debug("Raw realm_access.roles: {}", roles);
        
        if (!(roles instanceof List<?> list)) {
            logger.warn("realm_access.roles is not a List: {}", roles != null ? roles.getClass() : "null");
            return Collections.emptyList();
        }
        
        return list.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * Extracts roles from resource_access.<clientId>.roles claim
     */
    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractClientRoles(Jwt jwt, String clientId) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        
        if (resourceAccess == null || resourceAccess.isEmpty()) {
            logger.debug("No resource_access claim found in JWT");
            return Collections.emptyList();
        }
        
        logger.debug("Available clients in resource_access: {}", resourceAccess.keySet());
        
        Object clientAccess = resourceAccess.get(clientId);
        if (clientAccess == null) {
            logger.debug("No resource_access entry for client: {}", clientId);
            return Collections.emptyList();
        }
        
        if (!(clientAccess instanceof Map)) {
            logger.warn("resource_access.{} is not a Map", clientId);
            return Collections.emptyList();
        }
        
        Map<String, Object> clientAccessMap = (Map<String, Object>) clientAccess;
        Object roles = clientAccessMap.get("roles");
        logger.debug("Raw resource_access.{}.roles: {}", clientId, roles);
        
        if (!(roles instanceof List<?> list)) {
            logger.warn("resource_access.{}.roles is not a List", clientId);
            return Collections.emptyList();
        }
        
        return list.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}
