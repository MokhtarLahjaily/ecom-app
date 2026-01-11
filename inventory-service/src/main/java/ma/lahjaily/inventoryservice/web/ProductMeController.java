package ma.lahjaily.inventoryservice.web;

import ma.lahjaily.inventoryservice.entities.PageEvent;
import ma.lahjaily.inventoryservice.entities.Product;
import ma.lahjaily.inventoryservice.repository.ProductRepository;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductMeController {

    private final ProductRepository productRepository;
    private final StreamBridge streamBridge;

    public ProductMeController(ProductRepository productRepository, StreamBridge streamBridge) {
        this.productRepository = productRepository;
        this.streamBridge = streamBridge;
    }

    // Public: Get all products (paginated)
    @GetMapping
    public Page<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable);
    }

    // Public: Get a single product by ID
    // Publie un événement PageEvent sur visite-topic lors de la consultation
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        
        return productRepository.findById(id)
            .map(product -> {
                // Extraire l'utilisateur du token JWT si présent
                String currentUser = "anonymous";
                if (authorization != null && authorization.startsWith("Bearer ")) {
                    // L'utilisateur sera identifié côté analytics
                    currentUser = "authenticated-user";
                }
                
                // Créer et publier l'événement de visite sur visite-topic
                PageEvent pageEvent = new PageEvent(
                        "product-view",           // Nom de la page/action
                        currentUser,              // Utilisateur (ou anonymous)
                        new Date(),               // Timestamp
                        new Random().nextInt(5000) + 100  // Durée simulée
                );
                
                streamBridge.send("visite-topic", pageEvent);
                System.out.println("📤 [KAFKA] Événement publié sur visite-topic: " + pageEvent);
                
                return ResponseEntity.ok(product);
            })
            .orElse(ResponseEntity.notFound().build());
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
