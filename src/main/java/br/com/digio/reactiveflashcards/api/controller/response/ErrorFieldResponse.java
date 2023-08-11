package br.com.digio.reactiveflashcards.api.controller.response;

import lombok.Builder;

public record ErrorFieldResponse(
        String name, // nome do campo que deu erro
        String message
) {
    @Builder(toBuilder = true)
    public ErrorFieldResponse {}
}
