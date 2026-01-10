package ma.lahjaily.inventoryservice.web;

import ma.lahjaily.inventoryservice.entities.Product;
import ma.lahjaily.inventoryservice.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductMeController {

    private final ProductRepository productRepository;

    public ProductMeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/me")
    public List<Product> myProducts(JwtAuthenticationToken jwt) {
        String sub = jwt.getToken().getClaimAsString("sub");
        return productRepository.findByOwnerId(sub);
    }

    // Admin: Create a new product (global catalog)
    @PostMapping
    public Product createProduct(@RequestBody Product product, JwtAuthenticationToken jwt) {
        String sub = jwt.getToken().getClaimAsString("sub");
        product.setOwnerId(sub); // Track who created it
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(UUID.randomUUID().toString());
        }
        return productRepository.save(product);
    }

    // Admin: Update a product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        return productRepository.findById(id)
            .map(existing -> {
                existing.setName(product.getName());
                existing.setPrice(product.getPrice());
                existing.setQuantity(product.getQuantity());
                return ResponseEntity.ok(productRepository.save(existing));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // Admin: Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
