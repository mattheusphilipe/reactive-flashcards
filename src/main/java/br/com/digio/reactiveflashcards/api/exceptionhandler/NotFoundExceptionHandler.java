package br.com.digio.reactiveflashcards.api.exceptionhandler;

import br.com.digio.reactiveflashcards.api.exceptionhandler.AbstractHandlerException;
import br.com.digio.reactiveflashcards.domain.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
public class NotFoundExceptionHandler extends AbstractHandlerException<NotFoundException> {

    public NotFoundExceptionHandler(ObjectMapper mapper) {
        super(mapper);
    }
    @Override
    public Mono<Void> handlerException(ServerWebExchange exchange, NotFoundException ex) {
        return Mono
                .fromCallable(() -> {
                    prepareExchange(exchange, NOT_FOUND); // só vai setar o nosso satusCode e o application json que temos que retornar no contentType
                    return ex.getMessage();
                })
                .map(message -> buildError(NOT_FOUND, message)) //montar nosso objeto de response
                .doFirst(() -> log.error("==== NotFoundException:", ex)) // apenas o log
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse)); // realizar o writeReponse que vai devolver o resposonse escrito no exchange.

    }
}
