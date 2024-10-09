package com.brscrt.brokerage.model.mapper;


import com.brscrt.brokerage.model.dto.OrderRequest;
import com.brscrt.brokerage.model.dto.OrderResponse;
import com.brscrt.brokerage.model.entity.Customer;
import com.brscrt.brokerage.model.entity.Order;

public interface OrderMapper {

    static OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .assetName(order.getAssetName())
                .orderSide(order.getOrderSide())
                .size(order.getSize())
                .price(order.getPrice())
                .status(order.getStatus())
                .createDate(order.getCreateDate())
                .customerId(order.getCustomer().getId())
                .build();
    }

    static Order mapToEntity(OrderRequest orderRequest) {
        Order order = new Order();
        order.setAssetName(orderRequest.assetName());
        Customer customer = new Customer();
        order.setCustomer(customer);
        order.setSize(orderRequest.size());
        order.setPrice(orderRequest.price());
        order.setOrderSide(orderRequest.orderSide());

        return order;
    }
}