package ma.lahjaily.billingservice.repository;

import ma.lahjaily.billingservice.entities.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem, String> {
}
