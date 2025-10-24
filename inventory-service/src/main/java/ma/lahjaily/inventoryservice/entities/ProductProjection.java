package ma.lahjaily.inventoryservice.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "all", types = Product.class)
public interface ProductProjection {
    String getId();
    String getName();
    double getPrice();
    int getQuantity();
}
