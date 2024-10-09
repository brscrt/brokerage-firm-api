package com.brscrt.brokerage.service;

import com.brscrt.brokerage.exception.checked.*;
import com.brscrt.brokerage.exception.unchecked.DataSourceException;
import com.brscrt.brokerage.exception.unchecked.UnauthorizedException;
import com.brscrt.brokerage.model.dto.OrderRequest;
import com.brscrt.brokerage.model.dto.OrderResponse;
import com.brscrt.brokerage.model.entity.Asset;
import com.brscrt.brokerage.model.entity.Order;
import com.brscrt.brokerage.model.entity.OrderSide;
import com.brscrt.brokerage.model.entity.OrderStatus;
import com.brscrt.brokerage.model.mapper.OrderMapper;
import com.brscrt.brokerage.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static com.brscrt.brokerage.component.JwtTokenUtil.isAdmin;
import static com.brscrt.brokerage.util.DateTimeUtils.getOrDefaultEndTime;
import static com.brscrt.brokerage.util.DateTimeUtils.getOrDefaultStartTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AssetService assetService;
    private final OrderProcessingService orderProcessingService;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest, Long customerId) throws ApiException {
        log.info("Starting order creation for customer ID {} with asset {}", customerId, orderRequest.assetName());

        Asset tryAsset = assetService.getCustomerTryAsset(customerId);
        log.debug("Customer ID {} has TRY asset with usable size: {}", customerId, tryAsset.getUsableSize());

        double totalCost = orderRequest.size() * orderRequest.price();

        if (orderRequest.orderSide() == OrderSide.BUY) {
            if (tryAsset.getUsableSize() < totalCost) {
                throw new InsufficientFundsException("Insufficient funds in TRY asset for customer ID " + customerId);
            }

            log.debug("Deducting {} from customer ID {}'s TRY asset", totalCost, customerId);
            tryAsset.setUsableSize(tryAsset.getUsableSize() - totalCost);
            assetService.save(tryAsset);

        } else if (orderRequest.orderSide() == OrderSide.SELL) {
            Asset assetToSell = assetService.getCustomerAsset(customerId, orderRequest.assetName());
            if (assetToSell.getUsableSize() < orderRequest.size()) {
                throw new InsufficientAssetException("Insufficient asset size to sell for customer ID " + customerId);
            }

            log.debug("Deducting {} from asset {} for customer ID {}", orderRequest.size(), orderRequest.assetName(), customerId);
            assetToSell.setUsableSize(assetToSell.getUsableSize() - orderRequest.size());
            assetService.save(assetToSell);

            log.debug("Crediting {} to customer ID {}'s TRY asset", totalCost, customerId);
            tryAsset.setUsableSize(tryAsset.getUsableSize() + totalCost);
            assetService.save(tryAsset);
        }

        Order order = OrderMapper.mapToEntity(orderRequest);
        order.setStatus(OrderStatus.PENDING);
        order.getCustomer().setId(customerId);

        try {
            Order savedOrder = orderRepository.save(order);
            log.info("Order ID {} created successfully for customer ID {}", savedOrder.getId(), customerId);

            return OrderMapper.mapToResponse(savedOrder);
        } catch (Exception e) {
            throw new DataSourceException("Error while saving order for customer ID " + customerId, e);
        }
    }

    @Transactional
    public void cancelOrder(Long orderId, Long customerId) throws ApiException {
        log.debug("Canceling order ID {} for customer ID {}", orderId, customerId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (isAdmin() || customerId.equals(order.getCustomer().getId())) {
            cancelOrder(orderId);
        } else {
            throw new UnauthorizedException("You are not authorized to cancel this order");
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(Long customerId, LocalDateTime start, LocalDateTime end) {
        start = getOrDefaultStartTime(start);
        end = getOrDefaultEndTime(end);

        log.info("Listing orders for customer ID {} between {} and {}", customerId, start, end);
        try (Stream<Order> ordersStream = orderRepository.findByCustomerIdAndCreateDateBetween(customerId,
                start, end)) {

            List<OrderResponse> orders = ordersStream
                    .map(OrderMapper::mapToResponse)
                    .toList();

            log.info("Found {} orders for customer ID {}", orders.size(), customerId);

            return orders;
        } catch (Exception e) {
            throw new DataSourceException("Error while listing orders for customer ID " + customerId, e);
        }
    }

    @Transactional
    public void matchPendingOrders() {
        log.info("Starting matching pending orders.");

        AtomicInteger successCount = new AtomicInteger(0);

        try (Stream<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING)) {
            pendingOrders.forEach(order -> {
                try {
                    orderProcessingService.processOrder(order);
                    successCount.incrementAndGet();
                } catch (InsufficientFundsException e) {
                    log.warn("Insufficient funds for order ID {}: {}", order.getId(), e.getMessage());
                } catch (Exception e) {
                    log.error("Error processing order ID {}: {}", order.getId(), e.getMessage());
                }
            });
        } catch (Exception e) {
            throw new DataSourceException("Error while matching pending orders", e);
        }

        log.info("Finished matching pending orders. Successfully processed {} orders.", successCount.get());
    }

    private void cancelOrder(Long orderId) throws ApiException {
        log.info("Canceling order ID {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (OrderStatus.PENDING != order.getStatus()) {
            throw new InvalidOrderStatusException("Only pending orders can be canceled");
        }

        Asset tryAsset = assetService.getCustomerTryAsset(order.getCustomer().getId());
        double totalOrderValue = order.getSize() * order.getPrice();
        tryAsset.setUsableSize(tryAsset.getUsableSize() + totalOrderValue);
        assetService.save(tryAsset);

        order.setStatus(OrderStatus.CANCELED);
        try {
            orderRepository.save(order);
            log.info("Order ID {} canceled successfully", orderId);
        } catch (Exception e) {
            throw new DataSourceException("Error while saving canceled order for order ID " + orderId, e);
        }
    }
}