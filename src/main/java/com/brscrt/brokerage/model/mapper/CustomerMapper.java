package com.brscrt.brokerage.model.mapper;


import com.brscrt.brokerage.model.dto.CustomerResponse;
import com.brscrt.brokerage.model.entity.Customer;

public interface CustomerMapper {

    static CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .role(customer.getRole())
                .build();
    }
}