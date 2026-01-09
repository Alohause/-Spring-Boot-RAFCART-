package org.example.store.service;

import org.example.store.dto.CreatePaymentResponse;

public interface PaymentService {
    CreatePaymentResponse createPayment(Long userId, Long orderId, String method);
    void pay(Long userId, Long paymentId);
}
