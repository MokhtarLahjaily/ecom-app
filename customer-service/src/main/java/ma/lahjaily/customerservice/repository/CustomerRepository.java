package ma.lahjaily.customerservice.repository;

import ma.lahjaily.customerservice.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.Optional;

@RepositoryRestResource
public interface CustomerRepository extends JpaRepository<Customer,Long> {
	Optional<Customer> findByKeycloakId(String keycloakId);
}
