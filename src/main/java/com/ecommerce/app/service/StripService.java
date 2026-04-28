package com.ecommerce.app.service;

import com.ecommerce.app.payload.StripePaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripService {

    PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException;
}
