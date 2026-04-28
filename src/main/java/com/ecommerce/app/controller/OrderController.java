package com.ecommerce.app.controller;

import com.ecommerce.app.payload.OrderDTO;
import com.ecommerce.app.payload.OrderRequestDTO;
import com.ecommerce.app.payload.StripePaymentDTO;
import com.ecommerce.app.service.OrderService;
import com.ecommerce.app.service.StripService;
import com.ecommerce.app.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class OrderController {

    @Autowired
    AuthUtil authUtil;

    @Autowired
    OrderService orderService;

    @Autowired
    StripService stripService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO) {

        String email = authUtil.loggedInEmail();
        OrderDTO order = orderService.placeOrder(
                email,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret(@RequestBody StripePaymentDTO stripePaymentDTO) throws StripeException {
        PaymentIntent paymentIntent = stripService.paymentIntent(stripePaymentDTO);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);

    }

}
