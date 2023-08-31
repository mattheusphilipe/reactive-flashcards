package br.com.digio.reactiveflashcards.api.exceptionhandler;

import br.com.digio.reactiveflashcards.domain.exception.DeckInStudyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.DECK_IN_STUDY_FOUND;

@Slf4j
public class DeckInStudyHandler extends AbstractHandlerException<DeckInStudyException> {

    public DeckInStudyHandler(final ObjectMapper mapper) {
        super(mapper);
    }
    @Override
    public Mono<Void> handlerException(final ServerWebExchange exchange, final DeckInStudyException ex) {
        return Mono.fromCallable(() -> {
            prepareExchange(exchange, HttpStatus.CONFLICT);
            return DECK_IN_STUDY_FOUND.getMessage();
         }
        ).map(message -> buildError(HttpStatus.CONFLICT, message))
        .doFirst(() -> log.error("==== Not Found Exception", ex))
        .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }
}
