package org.example.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.store.dto.CreatePaymentResponse;
import org.example.store.entity.Orders;
import org.example.store.entity.Payment;
import org.example.store.mapper.OrdersMapper;
import org.example.store.mapper.PaymentMapper;
import org.example.store.service.CartFacade;
import org.example.store.service.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final OrdersMapper ordersMapper;
    private final CartFacade cartFacade;

    public PaymentServiceImpl(PaymentMapper paymentMapper, OrdersMapper ordersMapper, CartFacade cartFacade) {
        this.paymentMapper = paymentMapper;
        this.ordersMapper = ordersMapper;
        this.cartFacade = cartFacade;
    }

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(Long userId, Long orderId, String method) {
        Orders order = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));

        if (order == null) throw new RuntimeException("订单不存在");
        if (!"CREATED".equals(order.getStatus())) throw new RuntimeException("订单状态不允许支付");

        Payment p = new Payment();
        p.setOrderId(orderId);
        p.setMethod(method == null ? "UNKNOWN" : method);
        p.setStatus("INIT");
        p.setAmount(order.getPayAmount());
        p.setTradeNo(genTradeNo());
        p.setCreatedAt(LocalDateTime.now());
        paymentMapper.insert(p);

        CreatePaymentResponse resp = new CreatePaymentResponse();
        resp.setPaymentId(p.getId());
        resp.setOrderId(orderId);
        resp.setMethod(p.getMethod());
        resp.setStatus(p.getStatus());
        resp.setAmount(p.getAmount());
        resp.setTradeNo(p.getTradeNo());
        return resp;
    }

    @Override
    @Transactional
    public void pay(Long userId, Long paymentId) {
        Payment p = paymentMapper.selectById(paymentId);
        if (p == null) throw new RuntimeException("支付单不存在");

        Orders order = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, p.getOrderId())
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));
        if (order == null) throw new RuntimeException("订单不存在或无权限");

        if ("PAID".equals(p.getStatus())) return;

        p.setStatus("PAID");
        p.setPaidAt(LocalDateTime.now());
        paymentMapper.updateById(p);

        order.setStatus("PAID");
        order.setPaidAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        ordersMapper.updateById(order);

        cartFacade.clearCart(userId);
    }

    private String genTradeNo() {
        long ts = System.currentTimeMillis();
        int r = new Random().nextInt(900000) + 100000;
        return "TR" + ts + r;
    }
}
