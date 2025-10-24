package com.example.inventoryservice.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "basic", types = Product.class)
public interface ProductProjectionBasic {
    String getId();
    String getName();
    double getPrice();
}
