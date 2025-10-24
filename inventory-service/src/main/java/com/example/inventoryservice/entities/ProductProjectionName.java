package com.example.inventoryservice.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "name", types = Product.class)
public interface ProductProjectionName {
    String getId();
    String getName();
}
