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
			// Add sample products if database is empty
			if (repository.count() == 0) {
				repository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("MacBook Pro 16\"")
					.price(2499.99)
					.quantity(10)
					.ownerId("admin")
					.build());
				repository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("iPhone 15 Pro")
					.price(1199.99)
					.quantity(25)
					.ownerId("admin")
					.build());
				repository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("iPad Air")
					.price(599.99)
					.quantity(15)
					.ownerId("admin")
					.build());
				repository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("AirPods Pro")
					.price(249.99)
					.quantity(50)
					.ownerId("admin")
					.build());
				repository.save(Product.builder()
					.id(UUID.randomUUID().toString())
					.name("Apple Watch Ultra")
					.price(799.99)
					.quantity(20)
					.ownerId("admin")
					.build());
				System.out.println("✅ Sample products created!");
			}
		};
	}

}
