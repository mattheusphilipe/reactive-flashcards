package br.com.digio.reactiveflashcards.api.exceptionhandler;

import br.com.digio.reactiveflashcards.api.controller.response.ErrorFieldResponse;
import br.com.digio.reactiveflashcards.api.controller.response.ProblemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_BAD_REQUEST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
public class WebExchangeBindHandler extends AbstractHandlerException<WebExchangeBindException>{

    private final MessageSource messageSource;
    public WebExchangeBindHandler(ObjectMapper mapper, MessageSource messageSource) {
        super(mapper);
        this.messageSource = messageSource;
    }
    @Override
    public Mono<Void> handlerException(ServerWebExchange exchange, WebExchangeBindException ex) {
        return Mono.fromCallable(() ->  {
                    prepareExchange(exchange, BAD_REQUEST);
                    return GENERIC_BAD_REQUEST.getMessage();
                })
                .map(message -> buildError(BAD_REQUEST, message))
                .flatMap(response -> buildParamErrorMessage(response, ex))
                .doFirst(() -> log.error("==== WebExchangeBindException", ex))
                .flatMap(response -> writeResponse(exchange, response));
    }

    private Mono<ProblemResponse> buildParamErrorMessage(final ProblemResponse response, final WebExchangeBindException ex) {
        return Flux
                .fromIterable(ex.getAllErrors())
                .map(objectError ->
                        ErrorFieldResponse
                                .builder()
                                .name((objectError instanceof FieldError fieldError) ?  fieldError.getField() : objectError.getObjectName())
                                .message(messageSource.getMessage(objectError, LocaleContextHolder.getLocale())).build()
                ).collectList()
                .map(problems -> response.toBuilder().fields(problems).build());

    }
}
