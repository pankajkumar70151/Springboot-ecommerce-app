package com.ecommerce.app.service;

import com.ecommerce.app.payload.CartDTO;
import com.ecommerce.app.payload.CartItemDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CartService {

    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId, Long cartId);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductsInCarts(Long productId, Long cartId);

    String createOrUpdateCartWithItems(List<CartItemDTO> cartItems);
}
