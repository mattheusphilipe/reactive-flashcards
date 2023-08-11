package br.com.digio.reactiveflashcards.api.exceptionhandler;

import br.com.digio.reactiveflashcards.domain.exception.NotFoundException;
import br.com.digio.reactiveflashcards.domain.exception.ReactiveFlashcardsException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolationException;


@Component
@Order(-2) // dar prioridade ao componente
@Slf4j
@AllArgsConstructor
public class ApiExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper; // dependência que sera injetada via spring
    private final MessageSource messageSource; // para uma exceção específica

    // basicamente temos o nosso handler, de acordo com a nossa exception, vamos jogar no método para fazer o tratamento adequado
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        return Mono
                .error(ex)
                .onErrorResume(MethodNotAllowedException.class,
                        e -> new MethodNotAllowedHandler(objectMapper).handlerException(exchange, e))
                .onErrorResume(NotFoundException.class,
                        e -> new NotFoundExceptionHandler(objectMapper).handlerException(exchange, e))
                .onErrorResume(ResponseStatusException.class,
                        e-> new ResponseStatusExceptionHandler(objectMapper).handlerException(exchange, e))
                .onErrorResume(ConstraintViolationException.class,
                        e -> new ConstraintViolationHandler(objectMapper).handlerException(exchange, e))
                .onErrorResume(ReactiveFlashcardsException.class, e -> new ReactiveFlashcardsExceptionHandler(objectMapper).handlerException(exchange, e))
                .onErrorResume(WebExchangeBindException.class,
        e -> new WebExchangeBindHandler(objectMapper, messageSource).handlerException(exchange, e))
                .onErrorResume(Exception.class, e -> new GenericHandler(objectMapper).handlerException(exchange, e))
                .onErrorResume(JsonProcessingException.class, e -> new JsonProcessingExceptionHandler(objectMapper).handlerException(exchange, e)) // caso ocorra algum erro de serialização com o Jackson
                .then(); /// pois o then retorna justamente um Mono<Void>
    }

/*    private Mono<Void> handlerNotFoundException(final ServerWebExchange exchange, final NotFoundException ex) {
        return Mono
                .fromCallable(() -> {
                    prepareExchange(exchange, NOT_FOUND); // só vai setar o nosso satusCode e o application json que temos que retornar no contentType
                    return ex.getMessage();
                })
                .map(message -> buildError(NOT_FOUND, message)) //montar nosso objeto de response
                .doFirst(() -> log.error("==== NotFoundException:", ex)) // apenas o log
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse)); // realizar o writeReponse que vai devolver o resposonse escrito no exchange.
    }*/

/*    private Mono<Void> handlerException(final ServerWebExchange exchange, final Exception ex) {
        return Mono
                .fromCallable(() -> { /// formCalleble, diferente do fromSupplier propaga uma exception caso de algum erro neste ponto
            prepareExchange(exchange, INTERNAL_SERVER_ERROR);
            return GENERIC_EXCEPTION.getMessage();
        })
                .map(message -> buildError(INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("=== Exception: ", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
                ///com flatMap  trata nosso response nde forma não blocante


*//* tanto o map quanto o flatMap servem para pegar algo que está sendo propagado no seu fluxo de dados e trasnformar em alguma outra coisa.
        * A diferença que que o flatMap recebe uma função que vai retornar outro tipo e ele espera um Mono, ele realiza operações não blocantes dentro do flatMap
        * Já o Map não espera um Mono, ele trabalha co qualquer tipo, o map realiza operações blocantes.
        * No caso se seus métodos já estão preparados pra retornar Mono, já consegue tratar com o Map.
        * Nos tratamentos de dados tem que pensar se precisa trabalahr com operações não blocantes
        *//*
    }*/


}
