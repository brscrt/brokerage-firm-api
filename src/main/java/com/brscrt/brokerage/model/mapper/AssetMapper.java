package com.brscrt.brokerage.model.mapper;


import com.brscrt.brokerage.model.dto.AssetResponse;
import com.brscrt.brokerage.model.entity.Asset;

public interface AssetMapper {

    static AssetResponse mapToResponse(Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .assetName(asset.getAssetName())
                .size(asset.getSize())
                .usableSize(asset.getUsableSize())
                .customerId(asset.getCustomer().getId())
                .build();
    }
}