package br.com.digio.reactiveflashcards.domain.exception;

public class NotFoundException extends ReactiveFlashcardsException {

    public NotFoundException(String message) {
        super(message);
    }
}
