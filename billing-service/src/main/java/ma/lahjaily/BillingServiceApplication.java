package ma.lahjaily;


import ma.lahjaily.billingservice.entities.Bill;
import ma.lahjaily.billingservice.entities.ProductItem;
import ma.lahjaily.billingservice.feign.CustomerRestClient;
import ma.lahjaily.billingservice.feign.ProductRestClient;
import ma.lahjaily.billingservice.model.Customer;
import ma.lahjaily.billingservice.model.Product;
import ma.lahjaily.billingservice.repository.BillRepository;
import ma.lahjaily.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BillingServiceApplication.class, args);
    }

}
