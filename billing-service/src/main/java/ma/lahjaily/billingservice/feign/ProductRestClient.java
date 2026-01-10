package ma.lahjaily.billingservice.feign;


import ma.lahjaily.billingservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "inventory-service")
public interface ProductRestClient {
    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable String id, @RequestHeader("Authorization") String authorization);
    @GetMapping("/api/products")
    PagedModel<Product> getAllProducts(@RequestHeader("Authorization") String authorization);
}
