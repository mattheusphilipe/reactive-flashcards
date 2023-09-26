package br.com.digio.reactiveflashcards.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("retry-config")
@ConstructorBinding
public record RetryConfig(Long maxRetries, Long minDuration) {
}
