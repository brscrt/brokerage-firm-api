package com.brscrt.brokerage.model.dto;

import com.brscrt.brokerage.model.entity.OrderSide;
import com.brscrt.brokerage.model.entity.OrderStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record OrderResponse(
        Long id,
        String assetName,
        OrderSide orderSide,
        int size,
        double price,
        OrderStatus status,
        LocalDateTime createDate,
        Long customerId
) {
}