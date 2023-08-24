package br.com.digio.reactiveflashcards.api.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
public class EmailNotUniqueExceptionHandler extends AbstractHandlerException {

    public EmailNotUniqueExceptionHandler(final ObjectMapper mapper) { super(mapper); }
    @Override
    public Mono<Void> handlerException(ServerWebExchange exchange, Exception ex) {
        return Mono
                .fromCallable(() -> {
                    prepareExchange(exchange, BAD_REQUEST); // só vai setar o nosso satusCode e o application json que temos que retornar no contentType
                    return ex.getMessage();
                })
                .map(message -> buildError(BAD_REQUEST, message)) //montar nosso objeto de response
                .doFirst(() -> log.error("==== NotFoundException:", ex)) // apenas o log
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse)); // realizar o writeReponse que vai devolver o resposonse escrito no exchange.


    }
}
