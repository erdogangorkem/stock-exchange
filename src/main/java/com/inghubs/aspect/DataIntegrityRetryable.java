package com.inghubs.aspect;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retryable(
        value = {DataIntegrityViolationException.class},
        maxAttempts = 2,
        backoff = @Backoff(delay = 1000)
)
public @interface DataIntegrityRetryable {
}
