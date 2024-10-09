package com.brscrt.brokerage.component;

import com.brscrt.brokerage.component.handler.exception.ErrorResponse;
import com.brscrt.brokerage.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain) throws IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        String requestURI = request.getRequestURI();
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwt = authorizationHeader.substring(7);
                username = jwtTokenUtil.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwt, userDetails))) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                    userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            chain.doFilter(request, response);
        } catch (SignatureException e) {
            handleException(response, "Invalid JWT signature", HttpServletResponse.SC_UNAUTHORIZED, requestURI);
        } catch (ExpiredJwtException e) {
            handleException(response, "JWT token has expired", HttpServletResponse.SC_UNAUTHORIZED, requestURI);
        } catch (JwtException e) {
            handleException(response, "JWT token is invalid", HttpServletResponse.SC_UNAUTHORIZED, requestURI);
        } catch (Exception e) {
            handleException(response, "An error occurred while processing the JWT token",
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, requestURI);
        }
    }

    private void handleException(HttpServletResponse response, String message, int statusCode, String requestURI)
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");

        ErrorResponse errorMessage = new ErrorResponse(
                LocalDateTime.now(),
                message,
                requestURI
        );
        String json = objectMapper.writeValueAsString(errorMessage);

        response.getWriter().write(json);
    }
}