package com.brscrt.brokerage.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Getter
public class CustomUserDetail extends User {

    private final Long customerId;

    public CustomUserDetail(String username, String password, Long customerId,
                            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.customerId = customerId;
    }
}
