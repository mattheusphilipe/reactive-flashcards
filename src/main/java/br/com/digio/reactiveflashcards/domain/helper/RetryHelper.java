package br.com.digio.reactiveflashcards.domain.helper;

import br.com.digio.reactiveflashcards.core.RetryConfig;
import br.com.digio.reactiveflashcards.domain.exception.RetryException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Predicate;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_MAX_RETRIES;

@AllArgsConstructor
@Component
@Slf4j
public class RetryHelper {

    private final RetryConfig retryConfig;
    public Retry proccessRetry(final String retryIdentifier, final Predicate<? super Throwable> errorFilter) {

        // método que "desiste caos não consiga"
        return Retry.backoff(0L, Duration.ofSeconds(retryConfig.minDuration()))
                .filter(errorFilter)
                .doBeforeRetry(retrySignal -> log.warn("==== Retrying {} - {} time(s)", retryIdentifier,
                        retrySignal.totalRetries()))
        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> new RetryException(
                GENERIC_MAX_RETRIES.getMessage(), retrySignal.failure()));


    }
}
