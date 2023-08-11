package br.com.digio.reactiveflashcards.domain.document;

import lombok.Builder;

public record Question(String asked, String answered, String expected) {  // expected: correct answer

    @Builder(toBuilder = true)
    public Question {}
}
