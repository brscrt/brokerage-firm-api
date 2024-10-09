package com.brscrt.brokerage.model.dto;

import lombok.Builder;

@Builder
public record AssetResponse(Long id, String assetName, double size, double usableSize, Long customerId) {
}