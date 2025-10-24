package com.example.inventoryservice.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "inventory", types = Product.class)
public interface ProductProjectionInventory {
    String getId();
    String getName();
    int getQuantity();
}
