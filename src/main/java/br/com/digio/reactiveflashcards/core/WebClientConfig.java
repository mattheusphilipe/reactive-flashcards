package br.com.digio.reactiveflashcards.core;

import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.sql.Time;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    WebClient webClient(final WebClient.Builder builder, final ClientHttpConnector connector) {

        log.info("==== Creating a WebClient");

        return builder.clientConnector(connector).build();
    }

    ClientHttpConnector clientHttpConnector(final HttpClient httpClient) {
        return new ReactorClientHttpConnector(httpClient);
    }

    HttpClient httpClient(
            final Long responseTimeout,
            final Long readTimeout
    ) {
       return HttpClient.create()
               .responseTimeout(Duration.ofMillis(responseTimeout))
               .doOnConnected(connection ->
                       connection.addHandlerFirst(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)));
    }

}