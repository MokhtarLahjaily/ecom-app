package ma.lahjaily.inventoryservice.repository;


import ma.lahjaily.inventoryservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import java.util.List;

@RepositoryRestResource
public interface ProductRepository extends JpaRepository<Product, String> {
	List<Product> findByOwnerId(String ownerId);

	// Disable Spring Data REST write operations - handled by ProductMeController
	@Override
	@RestResource(exported = false)
	<S extends Product> S save(S entity);

	@Override
	@RestResource(exported = false)
	void deleteById(String id);

	@Override
	@RestResource(exported = false)
	void delete(Product entity);
}
