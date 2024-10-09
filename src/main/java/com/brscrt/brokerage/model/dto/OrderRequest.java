package com.brscrt.brokerage.model.dto;

import com.brscrt.brokerage.model.entity.OrderSide;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotBlank(message = "Asset name is mandatory")
        String assetName,

        @NotNull(message = "Order side is mandatory")
        OrderSide orderSide,

        int size,
        double price
) {
}