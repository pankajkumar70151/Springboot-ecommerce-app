package com.ecommerce.app.service;

import com.ecommerce.app.payload.OrderDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {

    @Transactional
    OrderDTO placeOrder(String email, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
