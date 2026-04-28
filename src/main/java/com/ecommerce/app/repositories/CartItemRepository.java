package com.ecommerce.app.repositories;

import com.ecommerce.app.modal.Cart;
import com.ecommerce.app.modal.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT c FROM CartItem c WHERE c.cart.id = ?1 AND c.product.id = ?2")
    CartItem findCartItemByProductIdandCartId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cart.id = ?1 AND c.product.id = ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cart.id = ?1")
    void deleteAllByCartId(Long cartId);
}
