package com.brscrt.brokerage.controller;

import com.brscrt.brokerage.exception.checked.ApiException;
import com.brscrt.brokerage.model.dto.OrderRequest;
import com.brscrt.brokerage.model.dto.OrderResponse;
import com.brscrt.brokerage.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static com.brscrt.brokerage.component.JwtTokenUtil.getCurrentCustomerId;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/matchOrders")
    public ResponseEntity<Void> matchOrders() {
        log.info("Matching pending orders.");
        orderService.matchPendingOrders();
        log.info("Pending orders matched successfully.");

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #customerId == authentication.principal.customerId)")
    @PostMapping("/create/{customerId}")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest,
                                                     @PathVariable Long customerId) throws ApiException {
        return handleCreateOrder(orderRequest, customerId);
    }

    @PostMapping("/create")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest)
            throws ApiException {
        return handleCreateOrder(orderRequest, getCurrentCustomerId());
    }

    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) throws ApiException {
        Long currentCustomerId = getCurrentCustomerId();
        log.info("Canceling order ID {} for customer ID {}", orderId, currentCustomerId);
        orderService.cancelOrder(orderId, currentCustomerId);
        log.info("Order ID {} canceled for customer ID {}", orderId, currentCustomerId);

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #customerId == authentication.principal.customerId)")
    @GetMapping("/list/{customerId}")
    public ResponseEntity<List<OrderResponse>> listOrders(@PathVariable Long customerId,
                                                          @RequestParam(required = false) LocalDateTime start,
                                                          @RequestParam(required = false) LocalDateTime end) {
        return handleListOrders(customerId, start, end);
    }

    @GetMapping("/list")
    public ResponseEntity<List<OrderResponse>> listOrders(@RequestParam(required = false) LocalDateTime start,
                                                          @RequestParam(required = false) LocalDateTime end) {
        return handleListOrders(getCurrentCustomerId(), start, end);
    }

    private ResponseEntity<OrderResponse> handleCreateOrder(OrderRequest orderRequest, Long customerId)
            throws ApiException {
        log.info("Creating order for customer ID {}", customerId);
        OrderResponse createdOrder = orderService.createOrder(orderRequest, customerId);
        log.info("Order created successfully for customer ID {}", customerId);

        return ResponseEntity.ok(createdOrder);
    }

    private ResponseEntity<List<OrderResponse>> handleListOrders(Long customerId,
                                                                 LocalDateTime start, LocalDateTime end) {
        log.info("Listing orders for customer ID {}", customerId);
        List<OrderResponse> orders = orderService.listOrders(customerId, start, end);
        log.info("Successfully listed orders for customer ID {}", customerId);

        return ResponseEntity.ok(orders);
    }
}