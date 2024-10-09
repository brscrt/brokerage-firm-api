package com.brscrt.brokerage.controller;

import com.brscrt.brokerage.exception.checked.ApiException;
import com.brscrt.brokerage.model.dto.AssetResponse;
import com.brscrt.brokerage.service.AssetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.brscrt.brokerage.component.JwtTokenUtil.getCurrentCustomerId;

@Slf4j
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    @PostMapping("/deposit")
    public ResponseEntity<AssetResponse> depositMoney(@RequestParam double amount) {
        return handleDeposit(getCurrentCustomerId(), amount);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #customerId == authentication.principal.customerId)")
    @PostMapping("/deposit/{customerId}")
    public ResponseEntity<AssetResponse> depositMoney(@RequestParam double amount, @PathVariable Long customerId) {
        return handleDeposit(customerId, amount);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AssetResponse> withdrawMoney(@RequestParam double amount,
                                                       @RequestParam String iban) throws ApiException {
        return handleWithdraw(getCurrentCustomerId(), amount, iban);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #customerId == authentication.principal.customerId)")
    @PostMapping("/withdraw/{customerId}")
    public ResponseEntity<AssetResponse> withdrawMoney(@RequestParam double amount, @PathVariable Long customerId,
                                                       @RequestParam String iban) throws ApiException {
        return handleWithdraw(customerId, amount, iban);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #customerId == authentication.principal.customerId)")
    @GetMapping("/{customerId}")
    public ResponseEntity<List<AssetResponse>> getAssets(@PathVariable Long customerId) {
        return handleGetAssets(customerId);
    }

    @GetMapping
    public ResponseEntity<List<AssetResponse>> getAssets() {
        return handleGetAssets(getCurrentCustomerId());
    }

    private ResponseEntity<AssetResponse> handleDeposit(Long customerId, double amount) {
        log.info("Starting deposit for customer ID {} with amount: {}", customerId, amount);
        AssetResponse assetResponse = assetService.depositMoney(customerId, amount);
        log.info("Deposit completed for customer ID {} with amount: {}", customerId, amount);

        return ResponseEntity.ok(assetResponse);
    }

    private ResponseEntity<AssetResponse> handleWithdraw(Long customerId, double amount, String iban)
            throws ApiException {
        log.info("Starting withdrawal for customer ID {} with amount: {} and IBAN: {}", customerId, amount, iban);
        AssetResponse assetResponse = assetService.withdrawMoney(customerId, amount, iban);
        log.info("Withdrawal completed for customer ID {} with amount: {} and IBAN: {}", customerId, amount, iban);

        return ResponseEntity.ok(assetResponse);
    }

    private ResponseEntity<List<AssetResponse>> handleGetAssets(Long customerId) {
        log.info("Fetching assets for customer ID {}", customerId);
        List<AssetResponse> assets = assetService.listAssets(customerId);
        log.info("Assets fetched for customer ID {}", customerId);

        return ResponseEntity.ok(assets);
    }
}