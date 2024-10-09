package com.brscrt.brokerage.service;

import com.brscrt.brokerage.exception.checked.ApiException;
import com.brscrt.brokerage.exception.checked.InsufficientFundsException;
import com.brscrt.brokerage.exception.unchecked.DataSourceException;
import com.brscrt.brokerage.model.entity.Asset;
import com.brscrt.brokerage.model.entity.Order;
import com.brscrt.brokerage.model.entity.OrderStatus;
import com.brscrt.brokerage.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProcessingService {

    private final OrderRepository orderRepository;
    private final AssetService assetService;

    @Transactional
    public void processOrder(Order order) throws ApiException {
        log.info("Starting to process order ID {} for customer ID {}", order.getId(), order.getCustomer().getId());

        Asset tryAsset = assetService.getCustomerTryAsset(order.getCustomer().getId());

        double totalOrderValue = order.getSize() * order.getPrice();
        log.debug("Total order value: {} for order ID {}", totalOrderValue, order.getId());

        if (tryAsset.getUsableSize() < totalOrderValue) {
            throw new InsufficientFundsException("Insufficient TRY funds to match order for order ID " + order.getId());
        }

        tryAsset.setUsableSize(tryAsset.getUsableSize() - totalOrderValue);

        log.debug("Deducting {} from TRY asset for customer ID {}", totalOrderValue, order.getCustomer().getId());
        try {
            assetService.save(tryAsset);
            log.info("Successfully updated TRY asset for customer ID {}", order.getCustomer().getId());
        } catch (Exception e) {
            throw new DataSourceException("Error while saving TRY asset for customer ID " + order.getCustomer().getId(), e);
        }

        log.debug("Fetching bought asset {} for customer ID {}", order.getAssetName(), order.getCustomer().getId());
        Asset boughtAsset = assetService.getCustomerAsset(order.getCustomer().getId(), order.getAssetName());

        boughtAsset.setSize(boughtAsset.getSize() + order.getSize());
        boughtAsset.setUsableSize(boughtAsset.getUsableSize() + order.getSize());

        log.debug("Updating bought asset {} for customer ID {}", order.getAssetName(), order.getCustomer().getId());
        try {
            assetService.save(boughtAsset);
            log.info("Successfully updated bought asset {} for customer ID {}", order.getAssetName(), order.getCustomer().getId());
        } catch (Exception e) {
            throw new DataSourceException("Error while saving bought asset for customer ID " + order.getCustomer().getId(), e);
        }

        log.debug("Setting order ID {} status to MATCHED", order.getId());
        order.setStatus(OrderStatus.MATCHED);

        try {
            orderRepository.save(order);
            log.info("Order ID {} successfully matched", order.getId());
        } catch (Exception e) {
            throw new DataSourceException("Error while updating order status for order ID " + order.getId(), e);
        }
    }
}