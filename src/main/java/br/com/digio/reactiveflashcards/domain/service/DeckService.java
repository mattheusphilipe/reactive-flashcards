package br.com.digio.reactiveflashcards.domain.service;

import br.com.digio.reactiveflashcards.domain.document.DeckDocument;
import br.com.digio.reactiveflashcards.domain.repository.DeckRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class DeckService {
    private final DeckRepository deckRepository;

    public Mono<DeckDocument> save(final DeckDocument document) {
        return deckRepository
                .save(document)
                .doFirst(() -> log.info("==== Trying to save a follow deck {}", document));
    }
}