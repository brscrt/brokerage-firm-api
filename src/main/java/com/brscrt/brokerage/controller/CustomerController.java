package com.brscrt.brokerage.controller;

import com.brscrt.brokerage.exception.checked.ApiException;
import com.brscrt.brokerage.exception.checked.NotFoundException;
import com.brscrt.brokerage.model.dto.CustomerResponse;
import com.brscrt.brokerage.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.brscrt.brokerage.component.JwtTokenUtil.getCurrentCustomerId;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #customerId == authentication.principal.customerId)")
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long customerId) throws ApiException {
        return handleGetCustomer(customerId);
    }

    @GetMapping
    public ResponseEntity<CustomerResponse> getCustomer() throws NotFoundException {
        return handleGetCustomer(getCurrentCustomerId());
    }

    private ResponseEntity<CustomerResponse> handleGetCustomer(Long customerId) throws NotFoundException {
        log.info("Fetching customer with ID {}", customerId);
        CustomerResponse customerResponse = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        log.info("Successfully fetched customer with ID {}", customerId);

        return ResponseEntity.ok(customerResponse);
    }
}