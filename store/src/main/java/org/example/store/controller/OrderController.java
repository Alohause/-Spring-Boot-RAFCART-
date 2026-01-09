package org.example.store.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.store.common.result.Result;
import org.example.store.common.util.UserContext;
import org.example.store.dto.CreateOrderRequest;
import org.example.store.dto.CreateOrderResponse;
import org.example.store.service.OrderService;
import org.example.store.vo.OrderVO;
import org.example.store.vo.TrackOrderVO;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<CreateOrderResponse> create(@RequestBody CreateOrderRequest req) {
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");
        if (req == null || req.getAddressId() == null) return Result.error("请选择收货地址");

        try {
            CreateOrderResponse resp = orderService.createOrderFromCart(uid, req.getAddressId(), req.getRemark());
            return Result.success(resp);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<IPage<OrderVO>> my (@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestParam(required = false) String status){
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");
        return Result.success(orderService.myOrders(uid, page, size, status));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail (@PathVariable Long id){
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");
        try {
            return Result.success(orderService.getOrderDetail(uid, id));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/cancel")
    public Result<String> cancel (@PathVariable Long id){
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");
        try {
            orderService.cancel(uid, id);
            return Result.success("已取消");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/return-request")
    public Result<String> returnReq (@PathVariable Long id, @RequestBody Map< String, Object > body){
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");
        try {
            String reason = body == null ? null : String.valueOf(body.getOrDefault("reason", "")).trim();
            orderService.requestReturn(uid, id, reason);
            return Result.success("已提交退货申请");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/track")
    public Result<TrackOrderVO> track (@RequestParam String orderNo){
        Long uid = UserContext.getUserId();
        if (uid == null) return Result.error("请先登录");
        try {
            return Result.success(orderService.track(uid, orderNo));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
