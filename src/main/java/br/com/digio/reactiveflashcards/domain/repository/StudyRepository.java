package br.com.digio.reactiveflashcards.domain.repository;

import br.com.digio.reactiveflashcards.domain.document.StudyDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;


public interface StudyRepository extends ReactiveMongoRepository<StudyDocument, String> {

    Mono<StudyDocument> findByStudyDeckDeckId(final String deckId);

}
