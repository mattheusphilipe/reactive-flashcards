package br.com.digio.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
public class ResponseStatusExceptionHandler extends AbstractHandlerException<ResponseStatusException>{

    public ResponseStatusExceptionHandler(ObjectMapper mapper)
    {
        super(mapper);
    }
    @Override
    public Mono<Void> handlerException(ServerWebExchange exchange, ResponseStatusException ex) {
        return Mono
                .fromCallable(() -> {
                    prepareExchange(exchange, NOT_FOUND);
                    return GENERIC_NOT_FOUND.getMessage();
                })
                .map(message -> buildError(NOT_FOUND, message))
                .doFirst(() -> log.error("==== ResponseStatusException:", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
}
