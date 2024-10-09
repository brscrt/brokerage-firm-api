package com.brscrt.brokerage.service;

import com.brscrt.brokerage.exception.checked.ApiException;
import com.brscrt.brokerage.exception.checked.InsufficientFundsException;
import com.brscrt.brokerage.exception.checked.InvalidIbanException;
import com.brscrt.brokerage.exception.checked.NotFoundException;
import com.brscrt.brokerage.exception.unchecked.DataSourceException;
import com.brscrt.brokerage.model.dto.AssetResponse;
import com.brscrt.brokerage.model.entity.Asset;
import com.brscrt.brokerage.model.mapper.AssetMapper;
import com.brscrt.brokerage.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.brscrt.brokerage.model.mapper.AssetMapper.mapToResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {

    private static final Pattern IBAN_PATTERN = Pattern.compile("^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$");

    private final AssetRepository assetRepository;

    @Transactional
    public AssetResponse depositMoney(Long customerId, double amount) {
        log.info("Starting deposit of {} for customer ID {}", amount, customerId);
        Asset tryAsset = getTryAsset(customerId);

        tryAsset.setSize(tryAsset.getSize() + amount);
        tryAsset.setUsableSize(tryAsset.getUsableSize() + amount);

        try {
            assetRepository.save(tryAsset);
        } catch (Exception e) {
            throw new DataSourceException("Error while saving asset for deposit operation. Customer ID: "
                    + customerId, e);
        }

        log.info("Successfully deposited {} for customer ID {}", amount, customerId);

        return mapToResponse(tryAsset);
    }

    @Transactional
    public AssetResponse withdrawMoney(Long customerId, double amount, String iban) throws ApiException {
        log.info("Starting withdrawal of {} for customer ID {} with IBAN {}", amount, customerId, iban);

        if (!isValidIBAN(iban)) {
            log.error("Invalid IBAN provided: {}", iban);
            throw new InvalidIbanException("Invalid IBAN");
        }

        Asset tryAsset = getTryAsset(customerId);

        if (tryAsset.getUsableSize() < amount) {
            log.error("Insufficient funds for customer ID {}. Requested: {}, Available: {}",
                    customerId, amount, tryAsset.getUsableSize());
            throw new InsufficientFundsException("Insufficient funds in TRY asset");
        }

        tryAsset.setSize(tryAsset.getSize() - amount);
        tryAsset.setUsableSize(tryAsset.getUsableSize() - amount);

        try {
            assetRepository.save(tryAsset);
        } catch (Exception e) {
            throw new DataSourceException("Error while saving asset for withdrawal operation. Customer ID: "
                    + customerId, e);
        }

        log.info("Successfully withdrew {} for customer ID {}", amount, customerId);

        return mapToResponse(tryAsset);
    }

    @Transactional(readOnly = true)
    public List<AssetResponse> listAssets(Long customerId) {
        log.info("Listing assets for customer ID {}", customerId);
        try (Stream<Asset> assetStream = assetRepository.findByCustomerId(customerId)) {
            List<AssetResponse> assetResponseList = assetStream
                    .map(AssetMapper::mapToResponse)
                    .toList();
            log.info("Found {} assets for customer ID {}", assetResponseList.size(), customerId);

            return assetResponseList;
        } catch (Exception e) {
            throw new DataSourceException("Error while retrieving assets for customer ID: " + customerId, e);
        }
    }

    @Transactional(readOnly = true)
    public Asset getCustomerTryAsset(Long customerId) {
        return getTryAsset(customerId);
    }

    @Transactional(readOnly = true)
    public Asset getCustomerAsset(Long customerId, String assetName) {
        log.info("Fetching asset {} for customer ID {}", assetName, customerId);

        return getAsset(customerId, assetName);
    }

    @Transactional
    public void save(Asset asset) {
        log.debug("Saving asset: {}", asset.getId());

        try {
            assetRepository.save(asset);
        } catch (Exception e) {
            throw new DataSourceException("Error while saving asset " + asset.getId(), e);
        }

        log.debug("Asset has been saved: {}", asset.getId());
    }

    private Asset getTryAsset(Long customerId) {
        return getAsset(customerId, "TRY");
    }

    private Asset getAsset(Long customerId, String assetName) {
        try {
            return assetRepository.findByCustomerIdAndAssetName(customerId, assetName)
                    .orElseThrow(() -> new NotFoundException("Customer does not have a " + assetName + " account"));
        } catch (Exception e) {
            throw new DataSourceException("Error while fetching " + assetName + " asset for customer ID: "
                    + customerId, e);
        }
    }

    private static boolean isValidIBAN(String iban) {
        return StringUtils.isNotBlank(iban) && IBAN_PATTERN.matcher(iban).matches();
    }
}