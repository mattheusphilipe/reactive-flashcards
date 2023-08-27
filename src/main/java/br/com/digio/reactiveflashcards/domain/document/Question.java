package br.com.digio.reactiveflashcards.domain.document;

import lombok.Builder;

import java.time.OffsetDateTime;

public record Question(
        OffsetDateTime askedIn,
        String asked,
        OffsetDateTime answeredIn,
        String answered,
        String expected
) {  // expected: correct answer

    @Builder(toBuilder = true)
    public Question {}
}
