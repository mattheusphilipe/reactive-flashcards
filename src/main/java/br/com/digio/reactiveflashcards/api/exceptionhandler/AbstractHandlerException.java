package br.com.digio.reactiveflashcards.api.exceptionhandler;

import br.com.digio.reactiveflashcards.api.controller.response.ProblemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static lombok.AccessLevel.PROTECTED;

@RequiredArgsConstructor(access = PROTECTED)
public abstract class AbstractHandlerException<T extends Exception> {

    private ObjectMapper objectMapper;

    public AbstractHandlerException(ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    public abstract Mono<Void> handlerException(final ServerWebExchange exchange, T ex);

    protected Mono<Void> writeResponse(ServerWebExchange exchange, ProblemResponse problemResponse) {
        return exchange.getResponse()
                .writeWith(Mono
                        .fromCallable(() ->
                                new DefaultDataBufferFactory()
                                        .wrap(objectMapper.writeValueAsBytes(problemResponse))
                        )
                );
    }

    protected void prepareExchange(final ServerWebExchange exchange, final HttpStatus statusCode) {
        exchange.getResponse().setStatusCode(statusCode);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    }

    protected ProblemResponse buildError(final HttpStatus status, final String errorDescription) {
        return ProblemResponse
                .builder()
                .status(status.value())
                .errorDescription(errorDescription)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
