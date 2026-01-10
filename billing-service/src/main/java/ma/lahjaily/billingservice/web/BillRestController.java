package ma.lahjaily.billingservice.web;


import ma.lahjaily.billingservice.entities.Bill;
import ma.lahjaily.billingservice.feign.CustomerRestClient;
import ma.lahjaily.billingservice.feign.ProductRestClient;
import ma.lahjaily.billingservice.repository.BillRepository;
import ma.lahjaily.billingservice.repository.ProductItemRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
