package com.brscrt.brokerage.config.web;

import com.brscrt.brokerage.util.DateTimeUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(@NonNull String source) {
        return DateTimeUtils.getDatetime(source);
    }
}