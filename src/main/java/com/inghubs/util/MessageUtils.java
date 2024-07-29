package com.inghubs.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class MessageUtils {

    private final MessageSource messageSource;

    public String getMessage(String code, Object... args) {
        String defaultMessage = "An unexpected error occurred";
        return messageSource.getMessage(code, args, defaultMessage, Locale.getDefault());
    }
}
