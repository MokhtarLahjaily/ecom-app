package ma.lahjaily.inventoryservice;

import ma.lahjaily.inventoryservice.entities.Product;
import ma.lahjaily.inventoryservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRepository repository) {
		return args -> {
			// Seeding removed: products should be created per user (ownerId) via API.
		};
	}

}
