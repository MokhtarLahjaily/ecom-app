package ma.lahjaily.customerservice.web;

import ma.lahjaily.customerservice.entities.Customer;
import ma.lahjaily.customerservice.repository.CustomerRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class MeController {

    private final CustomerRepository customerRepository;

    public MeController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping("/search/current-user")
    public Customer getOrCreateForCurrentUser(JwtAuthenticationToken jwt) {
        String sub = jwt.getToken().getClaimAsString("sub");
        String preferredUsername = jwt.getToken().getClaimAsString("preferred_username");
        String email = jwt.getToken().getClaimAsString("email");
        return customerRepository.findByKeycloakId(sub)
                .orElseGet(() -> customerRepository.save(Customer.builder()
                        .keycloakId(sub)
                        .username(preferredUsername)
                        .name(preferredUsername != null ? preferredUsername : "user")
                        .email(email)
                        .build()));
    }
}
