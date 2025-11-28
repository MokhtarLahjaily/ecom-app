package ma.lahjaily.billingservice.repository;

import ma.lahjaily.billingservice.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import java.util.List;

@RepositoryRestResource
public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByCustomerId(long customerId);
}
