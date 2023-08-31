package br.com.digio.reactiveflashcards.domain.service.query;

import br.com.digio.reactiveflashcards.domain.document.Question;
import br.com.digio.reactiveflashcards.domain.document.StudyDocument;
import br.com.digio.reactiveflashcards.domain.exception.NotFoundException;
import br.com.digio.reactiveflashcards.domain.repository.StudyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.*;

@Service
@Slf4j
@AllArgsConstructor
public class StudyQueryService {
    private final StudyRepository studyRepository;

    public Mono<StudyDocument> findPendingStudyByUserIdAndDeckId(final String userId, final String deckId) {
        return studyRepository
                .findByUserIdAndCompleteFalseAndStudyDeck_DeckId(userId, deckId)
                .doFirst(() -> log.info("==== Pending study with userId {} and deckId {}", userId, deckId))
                .filter(Objects::nonNull)
                .switchIfEmpty(
                        Mono.defer(() ->
                                Mono.error(new NotFoundException(STUDY_DECK_NOT_FOUND.params(userId, deckId).getMessage()))
                        )
                );
    }

    public Mono<StudyDocument> findById(final String id) {
        return studyRepository
                .findById(id)
                .doFirst(() -> log.info("==== Getting a study with id {}", id))
                .filter(Objects::nonNull)
                .switchIfEmpty(
                        Mono.defer(() ->
                                Mono.error(new NotFoundException(STUDY_NOT_FOUND.params(id).getMessage()))
                        )
                );
    }

    public Mono<Question> getLastPendingQuestion(final String id) {
        return findById(id)
                .filter(study -> BooleanUtils.isFalse(study.complete())) // verificar se tenho estudo em andamento
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new NotFoundException(STUDY_QUESTION_NOT_FOUND.params(id).getMessage()))
                ))
                .flatMapMany(study -> Flux.fromIterable(study.questions()))
                .filter(question -> question.isAnswered())
                .doFirst(() -> log.info("==== Getting a current pending question in study {}", id))
                .single();

        /*
        * flatMapMany
        *
        * Retorna o Flux de alguma coisa (das questions)a partir de um Mono.
        * */

        /*
        * single
        *
        * Retorna o Mono (um item de um Flux.
        * */

    }
}
