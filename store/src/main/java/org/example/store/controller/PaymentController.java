package org.example.store.controller;

import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.dto.CreatePaymentRequest;
import org.example.store.dto.CreatePaymentResponse;
import org.example.store.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 创建支付单
    @PostMapping
    public Result<CreatePaymentResponse> create(@RequestBody CreatePaymentRequest req) {
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");
        if (req == null || req.getOrderId() == null) return Result.error("缺少订单ID");

        try {
            return Result.success(paymentService.createPayment(uid, req.getOrderId(), req.getMethod()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 模拟支付（成功）
    @PostMapping("/{id}/pay")
    public Result<String> pay(@PathVariable Long id) {
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");

        try {
            paymentService.pay(uid, id);
            return Result.success("支付成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
