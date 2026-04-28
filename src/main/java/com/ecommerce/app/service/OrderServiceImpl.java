package com.ecommerce.app.service;

import com.ecommerce.app.exceptions.APIException;
import com.ecommerce.app.exceptions.ResourceNotFoundException;
import com.ecommerce.app.modal.*;
import com.ecommerce.app.payload.OrderDTO;
import com.ecommerce.app.payload.OrderItemDTO;
import com.ecommerce.app.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartService cartService;

    @Autowired
    ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

        // Getting User Cart
        Cart cart = cartRepository.findCartByEmail(email);
        if(cart == null)
        {
            throw new ResourceNotFoundException("Cart","email", email);
        }

        Address address = addressRepository.findById(addressId).orElseThrow(() ->
                new ResourceNotFoundException("Address", "addressId", addressId));

        // Creates a new Order with payment details
        Order order = new Order();
        order.setEmail(email);
        order.setAddress(address);
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus("Order Accepted !");
        order.setTotalAmount(cart.getTotalPrice());


        Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
        payment.setOrder(order);
        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);

        Order savedOrder = orderRepository.save(order);
        // Get items from the cart into the order item

        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems.isEmpty())
        {
            throw new APIException("Cart is empty !");
        }

        List<OrderItem> orderItems = cart.getCartItems().stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setProductPrice(item.getProductPrice());
            orderItem.setDiscount(item.getDiscount());
            orderItem.setOrder(savedOrder);
            return orderItem;
        }).toList();

        orderItems = orderItemRepository.saveAll(orderItems);

        // update product stock
        cart.getCartItems().forEach(item -> {

            Product product = item.getProduct();
            product.setProductStock(product.getProductStock() - item.getQuantity());
            productRepository.save(product);

            // Clear Cart
            cartService.deleteProductFromCart(cart.getCartId(), product.getProductId());
        });
        // send back the order summary

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(orderItem ->
                orderDTO.getOrderItems().add(modelMapper.map(orderItem, OrderItemDTO.class))
        );
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }
}
