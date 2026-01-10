package ma.lahjaily.inventoryservice.repository;


import ma.lahjaily.inventoryservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, String> {
	List<Product> findByOwnerId(String ownerId);
}
