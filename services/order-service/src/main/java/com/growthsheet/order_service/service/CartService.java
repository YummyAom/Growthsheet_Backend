package com.growthsheet.order_service.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.growthsheet.order_service.config.client.ProductClient;
import com.growthsheet.order_service.dto.request.AddToCartRequest;
import com.growthsheet.order_service.dto.response.CartResponse;
import com.growthsheet.order_service.entity.Cart;
import com.growthsheet.order_service.entity.CartItem;
import com.growthsheet.order_service.repository.CartRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CartService {
    private final CartRepository cartRepository;

    private final ProductClient productClient;

    public CartService(
            CartRepository cartRepository,
            ProductClient productClient) {
        this.cartRepository = cartRepository;
        this.productClient = productClient;
    }
    // public CartResponse addToCart(UUID userId, AddToCartRequest req) {

    // Cart cart = cartRepository.findByUserId(userId)
    // .orElseGet(() -> {
    // Cart c = new Cart();
    // c.setUserId(userId);
    // return c;
    // });

    // CartItem item = new CartItem();
    // item.setSheetId(req.getSheetId());
    // item.setSheetName(req.getSheetName());
    // item.setSellerName(req.getSellerName());
    // item.setPrice(req.getPrice());
    // item.setCart(cart);

    // cart.getItems().add(item);
    // cart.setTotalPrice(
    // cart.getTotalPrice().add(req.getPrice()));

    // return map(cartRepository.save(cart));
    // }

    public CartResponse addToCart(UUID userId, AddToCartRequest sheetId) {
        var product = productClient.getSheetById(sheetId.getSheetId());

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setTotalPrice(BigDecimal.ZERO);
                    c.setItems(new ArrayList<>());
                    return c;
                });

        CartItem item = new CartItem();
        item.setSheetId(product.id());
        item.setSheetName(product.title());
        item.setSellerName(product.seller().name());
        item.setPrice(product.price());
        item.setCart(cart);

        cart.getItems().add(item);

        BigDecimal currentTotal = cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO;
        cart.setTotalPrice(currentTotal.add(product.price()));

        return map(cartRepository.save(cart));
    }

    public CartResponse getCart(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));
        return map(cart);
    }

    private CartResponse map(Cart cart) {
        CartResponse res = new CartResponse();
        res.setCartId(cart.getId());
        res.setUserId(cart.getUserId());
        res.setTotalPrice(cart.getTotalPrice());

        List<CartResponse.Item> items = cart.getItems().stream().map(i -> {
            CartResponse.Item it = new CartResponse.Item();
            it.setId(i.getId());
            it.setSheetId(i.getSheetId());
            it.setSheetName(i.getSheetName());
            it.setSellerName(i.getSellerName());
            it.setPrice(i.getPrice());
            return it;
        }).toList();

        res.setItems(items);
        return res;
    }

    @Transactional
    public void removeItems(UUID userId, List<UUID> cartItemIds) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        // หา item ที่ต้องลบจริง ๆ
        List<CartItem> itemsToRemove = cart.getItems().stream()
                .filter(item -> cartItemIds.contains(item.getId()))
                .toList();

        if (itemsToRemove.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No cart items found to remove");
        }

        // ปรับราคา
        itemsToRemove.forEach(item -> cart.setTotalPrice(cart.getTotalPrice().subtract(item.getPrice())));

        // ลบออก
        cart.getItems().removeAll(itemsToRemove);

        cartRepository.save(cart);
    }

}
