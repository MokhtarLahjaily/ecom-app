package ma.lahjaily.billingservice.repository;

import ma.lahjaily.billingservice.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
