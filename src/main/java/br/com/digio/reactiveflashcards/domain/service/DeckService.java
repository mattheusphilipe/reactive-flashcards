package br.com.digio.reactiveflashcards.domain.service;

import br.com.digio.reactiveflashcards.domain.document.DeckDocument;
import br.com.digio.reactiveflashcards.domain.mapper.DeckDomainMapper;
import br.com.digio.reactiveflashcards.domain.repository.DeckRepository;
import br.com.digio.reactiveflashcards.domain.service.query.DeckQueryService;
import br.com.digio.reactiveflashcards.domain.service.query.DeckRestQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@AllArgsConstructor
public class DeckService {
    private final DeckRepository deckRepository;
    private final DeckRestQueryService deckRestQueryService;
    private DeckDomainMapper deckDomainMapper;
    private DeckQueryService deckQueryService;

    public Mono<DeckDocument> save(final DeckDocument document) {
        return deckRepository
                .save(document)
                .doFirst(() -> log.info("==== Trying to save a follow deck {}", document));
    }

    public Flux<DeckDocument> findAll() {
        return deckRepository.findAll().doFirst(() -> log.info("==== Trying to find all decks"));
    }

    public Mono<DeckDocument> update(final DeckDocument document) {
        return deckQueryService.findById(document.id())
                .map(deck -> document.toBuilder()
                        .createdAt(document.createdAt())
                        .updatedAt(document.updatedAt())
                        .build()
                ).flatMap(deckRepository::save);
    }

    public Mono<Void> delete(final String id) {
        return deckQueryService.findById(id)
                .flatMap(deckRepository::delete);
    }

    public Mono<Void> sync() {
        return deckRestQueryService
                .getDecks()
                .map(deckDomainMapper::toDocument)// mapeamento de cada atributo, trata item a item e não como uma lista
                .flatMap(deckRepository::save)
                .then();
    }

}