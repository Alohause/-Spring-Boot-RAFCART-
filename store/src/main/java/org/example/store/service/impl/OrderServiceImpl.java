package org.example.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.store.dto.CreateOrderResponse;
import org.example.store.entity.OrderItem;
import org.example.store.entity.Orders;
import org.example.store.entity.UserAddress;
import org.example.store.mapper.OrderItemMapper;
import org.example.store.mapper.OrdersMapper;
import org.example.store.mapper.UserAddressMapper;
import org.example.store.service.CartFacade;
import org.example.store.service.OrderService;
import org.example.store.vo.OrderItemVO;
import org.example.store.vo.OrderVO;
import org.example.store.vo.TrackOrderVO;
import org.example.store.vo.TrackStepVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrdersMapper ordersMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserAddressMapper userAddressMapper;
    private final CartFacade cartFacade;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderServiceImpl(OrdersMapper ordersMapper,
                            OrderItemMapper orderItemMapper,
                            UserAddressMapper userAddressMapper,
                            CartFacade cartFacade) {
        this.ordersMapper = ordersMapper;
        this.orderItemMapper = orderItemMapper;
        this.userAddressMapper = userAddressMapper;
        this.cartFacade = cartFacade;
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrderFromCart(Long userId, Long addressId, String remark) {
        // 1) 校验地址
        UserAddress addr = userAddressMapper.selectOne(
                new LambdaQueryWrapper<UserAddress>()
                        .eq(UserAddress::getId, addressId)
                        .eq(UserAddress::getUserId, userId)
                        .eq(UserAddress::getIsDeleted, 0)
                        .last("LIMIT 1")
        );
        if (addr == null) {
            throw new RuntimeException("地址不存在或已删除");
        }

        // 获取购物车
        CartFacade.CartView cart = cartFacade.getCart(userId);
        if (cart == null || cart.items == null || cart.items.isEmpty()) {
            throw new RuntimeException("购物车为空，无法下单");
        }

        // 金额
        BigDecimal total = BigDecimal.ZERO;
        for (CartFacade.CartItem it : cart.items) {
            BigDecimal price = it.price == null ? BigDecimal.ZERO : it.price;
            int qty = it.quantity == null ? 0 : it.quantity;
            total = total.add(price.multiply(BigDecimal.valueOf(qty)));
        }

        // 地址快照
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("receiverName", addr.getReceiverName());
        snapshot.put("phone", addr.getPhone());
        snapshot.put("region", addr.getRegion());
        snapshot.put("detail", addr.getDetail());

        String snapshotJson;
        try {
            snapshotJson = objectMapper.writeValueAsString(snapshot);
        } catch (Exception e) {
            throw new RuntimeException("地址快照生成失败");
        }

        // 创建订单
        Orders order = new Orders();
        order.setOrderNo(genOrderNo());
        order.setUserId(userId);
        order.setStatus("CREATED");
        order.setTotalAmount(total);
        order.setPayAmount(total);
        order.setAddressId(addressId);
        order.setAddressSnapshot(snapshotJson);
        order.setRemark(remark);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        ordersMapper.insert(order);

        // 订单快照
        for (CartFacade.CartItem it : cart.items) {
            BigDecimal price = it.price == null ? BigDecimal.ZERO : it.price;
            int qty = it.quantity == null ? 0 : it.quantity;
            BigDecimal sub = price.multiply(BigDecimal.valueOf(qty));

            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId());
            oi.setSkuId(it.skuId);
            oi.setProductName(it.productName == null ? "" : it.productName);
            oi.setSkuSpec(it.skuSpec);
            oi.setUnitPrice(price);
            oi.setQuantity(qty);
            oi.setSubtotal(sub);
            oi.setCreatedAt(LocalDateTime.now());
            orderItemMapper.insert(oi);
        }

        // 清空购物车
        // cartFacade.clearCart(userId);
        CreateOrderResponse resp = new CreateOrderResponse();
        resp.setOrderId(order.getId());
        resp.setOrderNo(order.getOrderNo());
        resp.setPayAmount(order.getPayAmount());
        resp.setStatus(order.getStatus());
        return resp;
    }

    private String genOrderNo() {
        // 时间戳 + 6位随机
        String ts = String.valueOf(System.currentTimeMillis());
        int r = new Random().nextInt(900000) + 100000;
        return "OD" + ts + r;
    }

    @Override
    public IPage<OrderVO> myOrders(Long userId, Integer page, Integer size, String status) {
        Page<Orders> p = new Page<>(page == null ? 1 : page, size == null ? 10 : size);

        LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
        qw.eq(Orders::getUserId, userId);
        if (status != null && !status.trim().isEmpty()) {
            qw.eq(Orders::getStatus, status.trim());
        }
        qw.orderByDesc(Orders::getCreatedAt).orderByDesc(Orders::getId);

        IPage<Orders> orderPage = ordersMapper.selectPage(p, qw);
        List<Orders> orders = orderPage.getRecords();
        if (orders == null || orders.isEmpty()) {
            return new Page<OrderVO>(p.getCurrent(), p.getSize(), 0);
        }

        List<Long> orderIds = orders.stream().map(Orders::getId).collect(Collectors.toList());
        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().in(OrderItem::getOrderId, orderIds)
                        .orderByAsc(OrderItem::getId)
        );

        Map<Long, List<OrderItem>> itemMap = items.stream().collect(Collectors.groupingBy(OrderItem::getOrderId));

        List<OrderVO> voList = new ArrayList<>();
        for (Orders o : orders) {
            OrderVO vo = toVO(o);
            List<OrderItem> its = itemMap.getOrDefault(o.getId(), Collections.emptyList());
            vo.setItems(its.stream().map(this::toItemVO).collect(Collectors.toList()));
            voList.add(vo);
        }

        Page<OrderVO> out = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        out.setRecords(voList);
        return out;
    }

    @Override
    public OrderVO getOrderDetail(Long userId, Long orderId) {
        Orders o = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));
        if (o == null) throw new RuntimeException("订单不存在或无权限");

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId)
                        .orderByAsc(OrderItem::getId)
        );

        OrderVO vo = toVO(o);
        vo.setItems(items.stream().map(this::toItemVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long orderId) {
        Orders o = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));

        if (o == null) throw new RuntimeException("订单不存在或无权限");
        if ("COMPLETED".equals(o.getStatus())) {
            throw new RuntimeException("已完成订单不可取消");
        }
        if ("CANCELLED".equals(o.getStatus())) {
            throw new RuntimeException("订单已取消");
        }
        if ("RETURN_REQUESTED".equals(o.getStatus())) {
            throw new RuntimeException("退货处理中不可取消");
        }

        o.setStatus("CANCELLED");
        o.setUpdatedAt(LocalDateTime.now());
        ordersMapper.updateById(o);
    }

    @Override
    @Transactional
    public void requestReturn(Long userId, Long orderId, String reason) {
        Orders o = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getId, orderId)
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));
        if (o == null) throw new RuntimeException("订单不存在或无权限");
        if (!Arrays.asList("PAID", "SHIPPED", "COMPLETED").contains(o.getStatus())) {
            throw new RuntimeException("当前订单状态不允许申请退货");
        }

        String r = (reason == null ? "" : reason.trim());
        o.setRemark((o.getRemark() == null ? "" : o.getRemark()) + (r.isEmpty() ? "" : (" | 退货原因：" + r)));
        o.setStatus("RETURN_REQUESTED");
        o.setUpdatedAt(LocalDateTime.now());
        ordersMapper.updateById(o);
    }

    @Override
    public TrackOrderVO track(Long userId, String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) throw new RuntimeException("请输入订单号");

        Orders o = ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getOrderNo, orderNo.trim())
                .eq(Orders::getUserId, userId)
                .last("LIMIT 1"));
        if (o == null) throw new RuntimeException("订单不存在或无权限");

        TrackOrderVO vo = new TrackOrderVO();
        vo.setOrderNo(o.getOrderNo());
        vo.setStatus(o.getStatus());
        vo.setCreatedAt(o.getCreatedAt());
        vo.setPaidAt(o.getPaidAt());
        vo.setUpdatedAt(o.getUpdatedAt());

        List<TrackStepVO> steps = new ArrayList<>();
        steps.add(step("01. Order Placement", o.getCreatedAt(), true));

        boolean paid = Arrays.asList("PAID","SHIPPED","COMPLETED","RETURN_REQUESTED").contains(o.getStatus());
        steps.add(step("02. Processing (Paid)", o.getPaidAt(), paid));

        boolean shipped = Arrays.asList("SHIPPED","COMPLETED").contains(o.getStatus());
        steps.add(step("03. Shipping", shipped ? o.getUpdatedAt() : null, shipped));

        boolean done = Arrays.asList("COMPLETED").contains(o.getStatus());
        steps.add(step("04. Delivered", done ? o.getUpdatedAt() : null, done));

        vo.setSteps(steps);
        return vo;
    }

    private TrackStepVO step(String title, LocalDateTime time, boolean done) {
        TrackStepVO s = new TrackStepVO();
        s.setTitle(title);
        s.setTime(time);
        s.setStatus(done ? "DONE" : "PENDING");
        return s;
    }

    private OrderVO toVO(Orders o) {
        OrderVO vo = new OrderVO();
        vo.setId(o.getId());
        vo.setOrderNo(o.getOrderNo());
        vo.setStatus(o.getStatus());
        vo.setTotalAmount(o.getTotalAmount());
        vo.setPayAmount(o.getPayAmount());
        vo.setCreatedAt(o.getCreatedAt());
        vo.setPaidAt(o.getPaidAt());
        vo.setUpdatedAt(o.getUpdatedAt());
        return vo;
    }

    private OrderItemVO toItemVO(OrderItem it) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(it.getId());
        vo.setSkuId(it.getSkuId());
        vo.setProductName(it.getProductName());
        vo.setSkuSpec(it.getSkuSpec());
        vo.setUnitPrice(it.getUnitPrice());
        vo.setQuantity(it.getQuantity());
        vo.setSubtotal(it.getSubtotal());
        return vo;
    }

}
