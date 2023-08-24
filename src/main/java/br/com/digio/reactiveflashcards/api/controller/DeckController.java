package br.com.digio.reactiveflashcards.api.controller;


import br.com.digio.reactiveflashcards.api.controller.request.DeckRequest;
import br.com.digio.reactiveflashcards.api.controller.response.DeckResponse;
import br.com.digio.reactiveflashcards.api.mapper.DeckMapper;
import br.com.digio.reactiveflashcards.core.validation.MongoId;
import br.com.digio.reactiveflashcards.domain.service.DeckService;
import br.com.digio.reactiveflashcards.domain.service.query.DeckQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Validated
@RestController
@RequestMapping("decks")
@Slf4j
@AllArgsConstructor
public class DeckController {

    public final DeckService deckService;
    public final DeckMapper deckMapper;
    public final DeckQueryService deckQueryService;
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DeckResponse> save(@Valid @RequestBody final DeckRequest request) {

        return deckService.save(deckMapper.toDocument(request))
                .doFirst(() -> log.info("==== Saving a deck with follow data {}", request))
                .map(document -> deckMapper.toResponse(document));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DeckResponse> findById(
            @Valid @PathVariable @MongoId(message = "{deckController.id}") final String id
    ) {
        return deckQueryService
                .findById(id)
                .map(deck -> deckMapper.toResponse(deck));
    }

}
