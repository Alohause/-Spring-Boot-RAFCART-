package org.example.store.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.store.dto.CreateOrderResponse;
import org.example.store.vo.OrderVO;
import org.example.store.vo.TrackOrderVO;

public interface OrderService {
    CreateOrderResponse createOrderFromCart(Long userId, Long addressId, String remark);
    IPage<OrderVO> myOrders(Long userId, Integer page, Integer size, String status);
    OrderVO getOrderDetail(Long userId, Long orderId);
    void cancel(Long userId, Long orderId);
    void requestReturn(Long userId, Long orderId, String reason);
    TrackOrderVO track(Long userId, String orderNo);
}
