package com.brscrt.brokerage.service;

import com.brscrt.brokerage.exception.unchecked.DataSourceException;
import com.brscrt.brokerage.model.dto.CustomerResponse;
import com.brscrt.brokerage.model.mapper.CustomerMapper;
import com.brscrt.brokerage.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Optional<CustomerResponse> getCustomerById(Long id) {
        log.info("Fetching customer with ID {}", id);

        try {
            return customerRepository.findById(id).map(CustomerMapper::mapToResponse);
        } catch (Exception e) {
            throw new DataSourceException("Error while fetching customer with ID " + id, e);
        }
    }
}