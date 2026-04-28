package com.ecommerce.app.controller;


import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ResourceNotFoundException;
import com.ecommerce.app.modal.Cart;
import com.ecommerce.app.payload.CartDTO;
import com.ecommerce.app.payload.CartItemDTO;
import com.ecommerce.app.repositories.CartRepository;
import com.ecommerce.app.service.CartService;
import com.ecommerce.app.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @PostMapping("/cart/create")
    public ResponseEntity<String> createOrUpdateCart(@RequestBody List<CartItemDTO> cartItems) {

        String response = cartService.createOrUpdateCartWithItems(cartItems);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @PostMapping("/cart/product/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {

            CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
            return new ResponseEntity<>(cartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>>getCarts() {

        List<CartDTO> cartDTOList = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOList, HttpStatus.OK);
    }

    @GetMapping("/carts/user/cart")
    public ResponseEntity<CartDTO> getCartByUser() {

        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart == null)
        {
            throw new APIException("Cart not found");
        }                                   
        Long cartId = cart.getCartId();
        CartDTO cartDTO = cartService.getCart(emailId, cartId);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);

    }

    @PutMapping("/cart/product/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateProductQuantity(@PathVariable Long productId, @PathVariable String operation) {

        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/cart/{cartId}/product/{productId}")
    public String deleteProductFromCart(@PathVariable Long cartId, @PathVariable Long productId) {

        String response = cartService.deleteProductFromCart(cartId, productId);

        return response;
    }

}
