package ma.lahjaily.billingservice.web;


import ma.lahjaily.billingservice.dto.CreateBillRequest;
import ma.lahjaily.billingservice.entities.Bill;
import ma.lahjaily.billingservice.entities.ProductItem;
import ma.lahjaily.billingservice.feign.CustomerRestClient;
import ma.lahjaily.billingservice.feign.ProductRestClient;
import ma.lahjaily.billingservice.repository.BillRepository;
import ma.lahjaily.billingservice.repository.ProductItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class BillRestController {

    private BillRepository billRepository;

    private ProductItemRepository productItemRepository;

    private CustomerRestClient customerRestClient;

    private ProductRestClient productRestClient;

    public BillRestController(BillRepository billRepository, ProductItemRepository productItemRepository,CustomerRestClient customerRestClient, ProductRestClient productRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productRestClient = productRestClient;
    }
    @GetMapping(path = "/bills/{id}")
    public Bill getBill(@PathVariable Long id, JwtAuthenticationToken jwt){
        String authorization = "Bearer " + jwt.getToken().getTokenValue();
        Bill bill = billRepository.findById(id).get();
        bill.setCustomer(customerRestClient.getCustomerById(bill.getCustomerId(), authorization));
        bill.getProductItems().forEach(productItem -> {
            productItem.setProduct(productRestClient.getProductById(productItem.getProductId(), authorization));
        });
        return bill;
    }

    @GetMapping(path = "/bills/me")
    public java.util.List<Bill> myBills(JwtAuthenticationToken jwt) {
        String authorization = "Bearer " + jwt.getToken().getTokenValue();
        Long customerId = customerRestClient.getCurrentCustomer(authorization).getId();
        return billRepository.findByCustomerId(customerId);
    }

    /**
     * Crée une nouvelle facture pour le client authentifié.
     * L'ID du client est récupéré automatiquement via le token JWT.
     */
    @PostMapping(path = "/bills")
    @ResponseStatus(HttpStatus.CREATED)
    public Bill createBill(@RequestBody CreateBillRequest request, JwtAuthenticationToken jwt) {
        String authorization = "Bearer " + jwt.getToken().getTokenValue();
        
        // Récupérer l'ID du client depuis le token JWT
        Long customerId = customerRestClient.getCurrentCustomer(authorization).getId();
        
        // Créer la facture avec la date actuelle
        Bill bill = Bill.builder()
                .customerId(customerId)
                .billingDate(new Date())
                .build();
        
        // Sauvegarder d'abord la facture pour obtenir son ID
        bill = billRepository.save(bill);
        
        // Créer et sauvegarder les items de produit
        final Bill savedBill = bill;
        if (request.getProductItems() != null) {
            request.getProductItems().forEach(item -> {
                ProductItem productItem = ProductItem.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .bill(savedBill)
                        .build();
                productItemRepository.save(productItem);
            });
        }
        
        // Recharger la facture avec ses items
        return billRepository.findById(bill.getId()).orElse(bill);
    }
}
