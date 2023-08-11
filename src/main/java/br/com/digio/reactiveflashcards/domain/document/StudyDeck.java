package br.com.digio.reactiveflashcards.domain.document;

import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.util.Set;

public record StudyDeck(
        String deckId, // para saber de onde veio o deck
        Set<StudyCard> cards
    ) {

    @Builder(toBuilder = true)
    public StudyDeck {}
}
