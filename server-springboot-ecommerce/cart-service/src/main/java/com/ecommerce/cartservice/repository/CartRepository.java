package com.ecommerce.cartservice.repository;

import com.ecommerce.cartservice.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends MongoRepository<Cart, String > {
    Optional<Cart> findCartByCustomerId(String customerId);
    //Optional<Cart> findCartByCartItemsProductId(UUID productId);
}
