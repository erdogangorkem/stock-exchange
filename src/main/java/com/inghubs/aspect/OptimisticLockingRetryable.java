package com.inghubs.aspect;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Retryable(
        value = {OptimisticLockingFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000)
)
public @interface OptimisticLockingRetryable {
}