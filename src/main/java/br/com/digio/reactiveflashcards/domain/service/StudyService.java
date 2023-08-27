package br.com.digio.reactiveflashcards.domain.service;

import br.com.digio.reactiveflashcards.domain.document.Card;
import br.com.digio.reactiveflashcards.domain.document.StudyDocument;
import br.com.digio.reactiveflashcards.domain.mapper.StudyDomainMapper;
import br.com.digio.reactiveflashcards.domain.repository.StudyRepository;
import br.com.digio.reactiveflashcards.domain.service.query.DeckQueryService;
import br.com.digio.reactiveflashcards.domain.service.query.UserQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class StudyService {

    private final UserQueryService userQueryService;
    private final DeckQueryService deckQueryService;
    private final StudyRepository studyRepository;
    private final StudyDomainMapper studyDomainMapper;

    // iniciar estudos
    public Mono<StudyDocument> start(final StudyDocument document) {
        var userExistBase = userQueryService.findById(document.id()); // só para não disparar uma exception caso não exista.

        return userExistBase
                .flatMap(user -> deckQueryService.findById(document.studyDeck().deckId())
                )//validar e pegar os cards do deck para jogar no noss estudo
                .doFirst(() -> log.info("builder().build(), {}", StudyDocument.builder().build()))
                .flatMap(deck -> getCards(document, deck.cards()))
                .map(study -> {
                    study.addQuestion(studyDomainMapper.generateRandomQuestion(study.studyDeck().cards()));
                    return study;
                })
                .doFirst(() -> log.info("==== Generating a primary random question"))
                .flatMap(study -> studyRepository.save(study))
                .doOnSuccess((study) -> log.info("==== A follow study {} was saved", study));
    }

    public Mono<StudyDocument> getCards(final StudyDocument document, final Set<Card> cards) {
        // mapeamento para gerar uma pergunta aleatória a partir do nosso deck de estudo

        return Flux.fromIterable(cards)
                .doFirst(() -> log.info("==== Copying cards to new study {} - {}", document, cards))
                .map(card -> studyDomainMapper.toStudyCard(card))
                .collectList()
                // uso o document para pegar os cards  ao invés de uma instância nova, pois eu não quero perder a referência, o deckId
                .map(studyCards -> document.studyDeck().toBuilder().cards(Set.copyOf(studyCards)).build())
                .map(studyDeck -> document.toBuilder().studyDeck(studyDeck).build());
    }

}
