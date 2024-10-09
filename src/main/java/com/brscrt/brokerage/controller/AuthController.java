package com.brscrt.brokerage.controller;

import com.brscrt.brokerage.component.JwtTokenUtil;
import com.brscrt.brokerage.model.CustomUserDetail;
import com.brscrt.brokerage.model.dto.AuthRequest;
import com.brscrt.brokerage.model.dto.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Starting login process for user: {}", authRequest.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

        CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        log.info("Login process completed successfully for user: {}", authRequest.username());

        return ResponseEntity.ok(new AuthResponse(token));
    }
}