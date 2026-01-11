package ma.lahjaily.billingservice.feign;



import ma.lahjaily.billingservice.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "customer-service")
public interface CustomerRestClient {
    @GetMapping("/api/customers/{id}")
    Customer getCustomerById(@PathVariable Long id, @RequestHeader("Authorization") String authorization);

    @GetMapping("/api/customers")
    PagedModel<Customer> getAllCustomers(@RequestHeader("Authorization") String authorization);

    @GetMapping("/api/customers/search/current-user")
    Customer getCurrentCustomer(@RequestHeader("Authorization") String authorization);

}
