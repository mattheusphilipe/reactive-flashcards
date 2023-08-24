package br.com.digio.reactiveflashcards.domain.exception;

public class EmailNotUniqueException extends ReactiveFlashcardsException {

    public EmailNotUniqueException(String message) {
        super(message);
    }
}
