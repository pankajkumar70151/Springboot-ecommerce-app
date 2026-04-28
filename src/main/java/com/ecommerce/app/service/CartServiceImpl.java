package com.ecommerce.app.service;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ResourceNotFoundException;
import com.ecommerce.app.modal.Cart;
import com.ecommerce.app.modal.CartItem;
import com.ecommerce.app.modal.Product;
import com.ecommerce.app.payload.CartDTO;
import com.ecommerce.app.payload.CartItemDTO;
import com.ecommerce.app.payload.ProductDTO;
import com.ecommerce.app.repositories.CartItemRepository;
import com.ecommerce.app.repositories.CartRepository;
import com.ecommerce.app.repositories.ProductRepository;
import com.ecommerce.app.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        // Get user's email

        String email = authUtil.loggedInEmail();

        // check if existing cart is available or create new one
        Cart existingCart = cartRepository.findCartByEmail(email);
        if(existingCart == null) {
            existingCart = new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggedInUser());
        } else {

            //clear all current items in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        // Process each item in the request to add to the cart
        double totalPrice = 0.00;
        for(CartItemDTO cartItemDTO : cartItems) {
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

        // Find the product by ID

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        // Directly update product stock and total price
            //roduct.setProductStock(product.getProductStock() - quantity);
            totalPrice += product.getProductDiscountedPrice() * quantity;
        // Create and save cart item
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getProductDiscountedPrice());
            cartItem.setDiscount(product.getProductDiscount());

            cartItemRepository.save(cartItem);
        }
        // Update the cart's total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "Cart created/updated with the new items successfully!";
    }

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        // FInd the existing cart or create a new one
        Cart cart = createCart();

        // Retrieve the product details from the database
        Product product = productRepository.findById(productId).orElseThrow(()->
                new ResourceNotFoundException("Product", "productId", productId));

        // perform validation like product availability or stocks

        CartItem cartItem = cartItemRepository.findCartItemByProductIdandCartId(cart.getCartId(), productId);
        if(cartItem != null) {
            throw new APIException("Product "+ product.getProductName() +" already exist in cart");
        }

        if(product.getProductStock() == 0)
        {
            throw new APIException("Product "+ product.getProductName() +" is out of stock");
        }

        if(product.getProductStock() < quantity){
            throw new APIException("Product "+ product.getProductName() +" stock is less than the quantity requested");
        }

        // Create a cart item

        CartItem newCartItem = new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getProductDiscount());
        newCartItem.setProductPrice(product.getProductDiscountedPrice());

        // Save the cart item to the database

        cartItemRepository.save(newCartItem);

        // Calculate the total price of the cart

        product.setProductStock(product.getProductStock());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getProductDiscountedPrice() * quantity));

        cartRepository.save(cart);

        // Convert cart class to cartDTO with the help of a model mapper
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        // Getting all the cart items and converting all the product to product DTO

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setProductStock(item.getQuantity());
                    return productDTO;
                });

        cartDTO.setProducts(productDTOStream.toList());
        // return updated cart details
        return cartDTO;
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());

        if(userCart != null)
        {
            return userCart;
        }

        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());
        return cartRepository.save(newCart);
    }

    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();

        if(carts.isEmpty())
        {
            throw new APIException("No carts exists");
        }

        List<CartDTO> cartDTOList = carts.stream().map(cart -> {

            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> productDTOList = cart.getCartItems().stream().
                    map(item -> {

                        ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                        productDTO.setProductStock(item.getQuantity());
                        return productDTO;
                    }).toList();

            cartDTO.setProducts(productDTOList);

            return cartDTO;
        }).toList();

        return cartDTOList;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {

        Cart cart = cartRepository.findCartByEmailIdAndCartId(emailId, cartId);

        if(cart == null)
        {
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(item -> item.getProduct().setProductStock(item.getQuantity()));
        List<ProductDTO> productDTOList = cart.getCartItems().stream().map(item ->
                modelMapper.map(item.getProduct(), ProductDTO.class)).toList();

        cartDTO.setProducts(productDTOList);

        return cartDTO;

    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if(product.getProductStock() == 0)
        {
            throw new APIException(product.getProductName() +" is out of stock");
        }

        if(quantity > 0 && product.getProductStock() < quantity){

            throw new APIException(product.getProductName() +" stock is less than the quantity requested");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdandCartId(cart.getCartId(), productId);
        if(cartItem == null)
        {
            throw new APIException(product.getProductName() +" not found in cart");
        }

        // Calculate the new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        if(newQuantity < 0)
        {
            throw new APIException("Quantity can't be less than zero");
        }

        if(newQuantity == 0){

            deleteProductFromCart(cart.getCartId(), product.getProductId());
        }
        else {

            cartItem.setProductPrice(product.getProductDiscountedPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getProductDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));

            cartRepository.save(cart);
        }

        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        if(updatedCartItem.getQuantity() == 0)
        {
           cartItemRepository.delete(updatedCartItem);
            return getCart(authUtil.loggedInEmail(), cartId);
        }

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
                    productDTO.setProductStock(item.getQuantity());
                    return productDTO;
                });

        cartDTO.setProducts(productDTOStream.toList());


        return cartDTO;


    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdandCartId(cart.getCartId(), productId);

        if(cartItem == null)
        {
            throw new ResourceNotFoundException("Product", "productId", productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product deleted from cart successfully!!";
    }

    @Override
    public void updateProductsInCarts(Long productId, Long cartId) {

        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdandCartId(cart.getCartId(), productId);

        if(cartItem == null)
        {
            throw new APIException("Product "+ product.getProductName() +" not found in cart");
        }

        double cartTotalPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getProductDiscountedPrice());
        cartItem.setDiscount(product.getProductDiscount());

        cart.setTotalPrice(cartTotalPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.save(cartItem);
    }


}
