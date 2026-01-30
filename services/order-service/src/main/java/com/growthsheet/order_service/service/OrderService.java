package com.growthsheet.order_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.growthsheet.order_service.dto.request.CreateOrderRequest;
import com.growthsheet.order_service.dto.response.OrderResponse;
import com.growthsheet.order_service.entity.Order;
import com.growthsheet.order_service.entity.OrderItem;
import com.growthsheet.order_service.repository.OrderRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(
            OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(UUID userId, CreateOrderRequest req) {

        Order order = new Order();
        order.setUserId(userId);
        order.setStatus("PENDING");

        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CreateOrderRequest.Item i : req.getItems()) {

            OrderItem item = new OrderItem();
            item.setSheetId(i.getSheetId());
            item.setPrice(i.getPrice());

            item.setOrder(order);
            orderItems.add(item);
            total = total.add(i.getPrice());
        }

        order.setItems(orderItems);
        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);

        OrderResponse res = new OrderResponse();
        res.setOrderId(savedOrder.getId());
        res.setUserId(savedOrder.getUserId());
        res.setStatus(savedOrder.getStatus());
        res.setTotalPrice(savedOrder.getTotalPrice());

        return res;
    }
}
