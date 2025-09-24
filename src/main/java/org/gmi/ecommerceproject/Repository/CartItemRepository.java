package org.gmi.ecommerceproject.Repository;

import org.gmi.ecommerceproject.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface
CartItemRepository extends JpaRepository<CartItem,Long> {
    @Query(value = "SELECT ci FROM CartItem ci where ci.cart.cartId=?1 AND ci.product.productId = ?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.cartId = ?1 AND ci.product.productId = ?2")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);


}
