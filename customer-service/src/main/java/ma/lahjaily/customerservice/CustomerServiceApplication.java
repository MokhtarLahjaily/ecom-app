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

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            customerRepository.save(Customer.builder()
                            .name("Mohamed")
                            .email("mohamed@email.com")
                    .build());
            customerRepository.save(Customer.builder()
                    .name("Imane")
                    .email("imane@email.com")
                    .build());
            customerRepository.save(Customer.builder()
                    .name("Yassine")
                    .email("yassine@email.com")
                    .build());
            customerRepository.findAll().forEach(c -> {
                System.out.println("===========================");
                System.out.println(c.getId());
                System.out.println(c.getName());
                System.out.println(c.getEmail());
                System.out.println("===========================");
            });
        };
    }

}
