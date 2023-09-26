package br.com.digio.reactiveflashcards.domain.exception;

public class ReactiveFlashcardsException extends RuntimeException {
    public ReactiveFlashcardsException(String message) {
        super(message);
    }
    public ReactiveFlashcardsException(String message, Throwable cause) {
        super(message, cause);
    }


}
