package br.com.digio.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.lang.Exception;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.GENERIC_EXCEPTION;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
public class GenericHandler extends AbstractHandlerException<Exception> {

    public GenericHandler(ObjectMapper mapper) {
        super(mapper);
    }
    @Override
    public Mono<Void> handlerException(ServerWebExchange exchange, Exception ex) {
        return Mono
                .fromCallable(() -> { /// formCalleble, diferente do fromSupplier propaga uma exception caso de algum erro neste ponto
                    prepareExchange(exchange, INTERNAL_SERVER_ERROR);
                    return GENERIC_EXCEPTION.getMessage();
                })
                .map(message -> buildError(INTERNAL_SERVER_ERROR, message))
                .doFirst(() -> log.error("=== Exception: ", ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
        ///com flatMap  trata nosso response nde forma não blocante


        /* tanto o map quanto o flatMap servem para pegar algo que está sendo propagado no seu fluxo de dados e trasnformar em alguma outra coisa.
         * A diferença que que o flatMap recebe uma função que vai retornar outro tipo e ele espera um Mono, ele realiza operações não blocantes dentro do flatMap
         * Já o Map não espera um Mono, ele trabalha co qualquer tipo, o map realiza operações blocantes.
         * No caso se seus métodos já estão preparados pra retornar Mono, já consegue tratar com o Map.
         * Nos tratamentos de dados tem que pensar se precisa trabalahr com operações não blocantes
         */
    }
}
