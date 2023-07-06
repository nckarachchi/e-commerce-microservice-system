package com.ecommerce.cartservice.controller;

import com.ecommerce.cartservice.dto.cartItem.CreateCartItemRequest;
import com.ecommerce.cartservice.dto.cartItem.UpdateCartItemRequest;
import com.ecommerce.cartservice.model.Cart;
import com.ecommerce.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addProductToCart(@RequestParam String customerId,
                                                        @RequestBody CreateCartItemRequest createCartItemRequest){

        cartService.save(customerId,createCartItemRequest);
        return new ResponseEntity<>("Product is added", HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<String> updateQuantity(@RequestParam String customerId,
                                                   @RequestBody UpdateCartItemRequest updateCartItemRequest){

        cartService.updateQuantity(customerId,updateCartItemRequest);
        return new ResponseEntity<>("Product is updated with id", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getCarts() {
        return ResponseEntity.ok(cartService.getCarts());
    }
}
