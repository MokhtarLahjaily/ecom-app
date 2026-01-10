package ma.lahjaily.customerservice;

import ma.lahjaily.customerservice.entities.Customer;
import ma.lahjaily.customerservice.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }

    // Seeding removed: customers are created dynamically from Keycloak users on first access.

}
