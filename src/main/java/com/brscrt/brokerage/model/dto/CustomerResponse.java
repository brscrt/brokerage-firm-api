package com.brscrt.brokerage.model.dto;

import com.brscrt.brokerage.model.entity.CustomerRole;
import lombok.Builder;

@Builder
public record CustomerResponse(Long id, String username, CustomerRole role) {
}