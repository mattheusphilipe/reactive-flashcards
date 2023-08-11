package br.com.digio.reactiveflashcards.api.exceptionhandler;

import br.com.digio.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import br.com.digio.reactiveflashcards.api.controller.response.ProblemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_BAD_REQUEST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;


@Slf4j
public class ConstraintViolationHandler extends AbstractHandlerException<ConstraintViolationException>{

    public ConstraintViolationHandler(ObjectMapper mapper) {
        super(mapper);
    }
    @Override
    public Mono<Void> handlerException(ServerWebExchange exchange, ConstraintViolationException ex) {
        return Mono.fromCallable(() -> {
            prepareExchange(exchange, BAD_REQUEST);
            return GENERIC_BAD_REQUEST.getMessage();
        })
        .map(message -> buildError(BAD_REQUEST, message))
        .flatMap(response -> buildParamErrorMessage(response, ex))
        .doFirst(() -> log.error("=== ConstraintViolationException", ex))
        .flatMap(response -> writeResponse(exchange, response));
    }

    private Mono<ProblemResponse> buildParamErrorMessage(final ProblemResponse response, final ConstraintViolationException ex) {
        return Flux
                .fromIterable(ex.getConstraintViolations())
                .map(constraintViolation ->
                        ErrorFieldResponse
                                .builder()
                                .name(((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().toString())
                                .message(constraintViolation.getMessage()).build()
                ).collectList()
                .map(problems -> response.toBuilder().fields(problems).build());


    }
}
