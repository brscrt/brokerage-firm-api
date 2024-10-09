package com.brscrt.brokerage.repository;

import com.brscrt.brokerage.model.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByCustomerIdAndAssetName(Long customerId, String assetName);

    Stream<Asset> findByCustomerId(Long customerId);
}