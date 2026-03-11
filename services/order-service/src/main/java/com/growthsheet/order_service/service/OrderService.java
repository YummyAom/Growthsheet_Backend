package com.growthsheet.order_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.growthsheet.order_service.dto.PageResponse;
import com.growthsheet.order_service.dto.request.CheckoutRequest;
import com.growthsheet.order_service.dto.response.OrderResponse;
import com.growthsheet.order_service.entity.Cart;
import com.growthsheet.order_service.entity.CartItem;
import com.growthsheet.order_service.entity.Order;
import com.growthsheet.order_service.entity.OrderItem;
import com.growthsheet.order_service.repository.CartRepository;
import com.growthsheet.order_service.repository.OrderRepository;
import com.growthsheet.order_service.repository.OrderItemRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.transaction.Transactional;

@Service
@Transactional

public class OrderService {
    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final OrderItemRepository orderItemRepo;

    public OrderService(
            OrderRepository orderRepo,
            CartRepository cartRepo,
            OrderItemRepository orderItemRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
        this.orderItemRepo = orderItemRepo;
    }

    public boolean hasPurchased(UUID userId, UUID sheetId) {

        List<Order> paidOrders = orderRepo.findByUserIdAndStatus(userId, "PAID");

        for (Order order : paidOrders) {
            for (OrderItem item : order.getItems()) {
                // ถ้าเจอ sheetId ที่ตรงกัน และยังไม่ได้ถูก refund แปลว่ายังมีสิทธิ์อยู่
                if (item.getSheetId().equals(sheetId) &&
                        (item.getIsRefunded() == null || !item.getIsRefunded())) {
                    return true;
                }
            }
        }
        return false;
    }

    // แก้ไข Return Type เป็น OrderResponse
    public OrderResponse checkout(UUID userId, CheckoutRequest req) {

        Cart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart empty"));

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal orderTotal = BigDecimal.ZERO;

        // === เอาเฉพาะ item ที่เลือก ===
        for (CartItem c : cart.getItems()) {
            if (req.getCartItemIds().contains(c.getId())) {
                OrderItem oi = new OrderItem();
                oi.setSheetId(c.getSheetId());
                oi.setSheetName(c.getSheetName());
                oi.setSellerName(c.getSellerName());
                oi.setPrice(c.getPrice());
                oi.setOrder(order);

                orderItems.add(oi);
                orderTotal = orderTotal.add(c.getPrice());
            }
        }

        if (orderItems.isEmpty()) {
            throw new RuntimeException("No valid items found in cart for checkout");
        }

        order.setItems(orderItems);
        order.setTotalPrice(orderTotal);

        Order savedOrder = orderRepo.save(order);
        // === คำนวณ total cart ใหม่ (แบบปลอดภัย) ===
        BigDecimal cartTotal = cart.getItems().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(cartTotal);
        cartRepo.save(cart);
        System.out.println("cartItemIds received: " + req.getCartItemIds());
        System.out.println("cart items in DB: " + cart.getItems().stream()
                .map(i -> "id=" + i.getId() + " sheetId=" + i.getSheetId())
                .toList());
        // ✅ แมป Entity Order ให้เป็น OrderResponse DTO ก่อนส่งคืน
        return mapToResponse(savedOrder);
    }

    public PageResponse<OrderResponse> getPaidOrdersByUser(UUID userId, Pageable pageable) {
        Page<OrderResponse> page = orderRepo
                .findByUserIdAndStatus(userId, "PAID", pageable)
                .map(order -> {
                    OrderResponse res = mapToResponse(order);
                    // กรองเอาเฉพาะ item ที่ 'ยังไม่ถูก refund' ไปแสดงในคลัง
                    List<OrderResponse.Item> activeItems = res.getItems().stream()
                            .filter(item -> item.getIsRefunded() == null || !item.getIsRefunded())
                            .toList();
                    res.setItems(activeItems);
                    return res;
                });

        return new PageResponse<>(page);
    }

    public List<OrderResponse> getOrdersByUser(UUID userId) {
        return orderRepo.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<OrderResponse> getPendingOrdersByUser(UUID userId) {
        return orderRepo.findByUserIdAndStatus(userId, "PENDING")
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrderResponse getOrderByIdAndUser(UUID orderId, UUID userId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // กัน user ดู order คนอื่น
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        return mapToResponse(order);
    }

    private OrderResponse mapToResponse(Order order) {

        List<OrderResponse.Item> items = new ArrayList<>();

        for (OrderItem i : order.getItems()) {
            OrderResponse.Item item = new OrderResponse.Item();
            item.setOrderItemId(i.getId());
            item.setSheetId(i.getSheetId());
            item.setSheetName(i.getSheetName());
            item.setSellerName(i.getSellerName());
            item.setPrice(i.getPrice());
            item.setIsRefunded(i.getIsRefunded());
            items.add(item);
        }

        OrderResponse res = new OrderResponse();
        res.setOrderId(order.getId());
        res.setUserId(order.getUserId());
        res.setStatus(order.getStatus());
        res.setTotalPrice(order.getTotalPrice());
        res.setItems(items);

        return res;
    }

    @Transactional
    public void markAsPaid(UUID orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("PAID".equals(order.getStatus())) {
            return;
        }

        order.setStatus("PAID");
        orderRepo.save(order);

        // ✅ ย้ายมาลบตรงนี้แทน หลังจากจ่ายเงินจริงๆ แล้ว
        Cart cart = cartRepo.findByUserId(order.getUserId())
                .orElse(null);

        if (cart != null) {
            List<UUID> paidSheetIds = order.getItems().stream()
                    .map(OrderItem::getSheetId)
                    .toList();

            cart.getItems().removeIf(i -> paidSheetIds.contains(i.getSheetId()));

            BigDecimal cartTotal = cart.getItems().stream()
                    .map(CartItem::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            cart.setTotalPrice(cartTotal);
            cartRepo.save(cart);
        }
    }

    // ===== ยกเลิก Order (ยกเลิกได้เฉพาะ PENDING) =====
    @Transactional
    public void cancelOrder(UUID orderId, UUID userId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("ไม่พบ Order"));

        // เช็คว่าเป็น order ของ user คนนี้หรือไม่
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("ไม่มีสิทธิ์ยกเลิก Order นี้");
        }

        // เช็คว่า order ยังเป็น PENDING อยู่หรือไม่
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException(
                    "ไม่สามารถยกเลิก Order ที่มีสถานะ " + order.getStatus() + " ได้ (ยกเลิกได้เฉพาะ PENDING)");
        }

        order.setStatus("CANCELLED");
        orderRepo.save(order);
    }

    @Transactional
    public void revokeAccess(UUID orderItemId) {
        OrderItem item = orderItemRepo.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("OrderItem not found"));
        item.setIsRefunded(true);
        orderItemRepo.save(item);
    }
}
