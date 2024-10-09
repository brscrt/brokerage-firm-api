package com.brscrt.brokerage.repository;

import com.brscrt.brokerage.model.entity.Order;
import com.brscrt.brokerage.model.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Stream<Order> findByStatus(OrderStatus status);

    Stream<Order> findByCustomerIdAndCreateDateBetween(Long customerId, LocalDateTime start, LocalDateTime end);
}