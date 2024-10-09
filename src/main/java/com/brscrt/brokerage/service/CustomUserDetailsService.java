package com.brscrt.brokerage.service;

import com.brscrt.brokerage.exception.unchecked.DataSourceException;
import com.brscrt.brokerage.model.CustomUserDetail;
import com.brscrt.brokerage.model.entity.Customer;
import com.brscrt.brokerage.model.entity.CustomerRole;
import com.brscrt.brokerage.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        Customer customer;
        try {
            customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        } catch (Exception e) {
            throw new DataSourceException("Error while fetching user with username: " + username, e);
        }

        log.debug("Successfully loaded user with username: {}", username);

        List<SimpleGrantedAuthority> authorities = customer.getRole().equals(CustomerRole.ADMIN)
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new CustomUserDetail(
                customer.getUsername(),
                customer.getPassword(),
                customer.getId(),
                authorities
        );
    }
}