package com.growthsheet.order_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.growthsheet.order_service.dto.request.CheckoutRequest;
import com.growthsheet.order_service.dto.response.OrderResponse;
import com.growthsheet.order_service.entity.Cart;
import com.growthsheet.order_service.entity.CartItem;
import com.growthsheet.order_service.entity.Order;
import com.growthsheet.order_service.entity.OrderItem;
import com.growthsheet.order_service.repository.CartRepository;
import com.growthsheet.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional

public class OrderService {
    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;

    public OrderService(
            OrderRepository orderRepo,
            CartRepository cartRepo) {
        this.orderRepo = orderRepo;
        this.cartRepo = cartRepo;
    }

    public Order checkout(UUID userId, CheckoutRequest req) {

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

        order.setItems(orderItems);
        order.setTotalPrice(orderTotal);

        Order savedOrder = orderRepo.save(order);

        // === ลบ item ที่จ่ายแล้วออกจาก cart ===
        cart.getItems().removeIf(i -> req.getCartItemIds().contains(i.getId()));

        // === คำนวณ total cart ใหม่ ===
        BigDecimal cartTotal = cart.getItems().stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(cartTotal);

        cartRepo.save(cart);

        return savedOrder;
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
            item.setSheetId(i.getSheetId());
            item.setSheetName(i.getSheetName());
            item.setSellerName(i.getSellerName());
            item.setPrice(i.getPrice());
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

        if (order.getStatus() == "PAID") {
            return; // กัน update ซ้ำ
        }

        order.setStatus("PAID");
        orderRepo.save(order);
    }

}
