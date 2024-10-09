package com.brscrt.brokerage.config.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToLocalDateTimeConverter stringToLocalDateTimeConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(stringToLocalDateTimeConverter);
    }
}