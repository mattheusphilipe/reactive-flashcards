package br.com.digio.reactiveflashcards.api.controller.request;

import br.com.digio.reactiveflashcards.core.validation.MongoId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record StudyRequest(
        @MongoId
        @JsonProperty("userId") // para garantir a serialização
        String userId,
        @JsonProperty("deckId")
        @MongoId
        String deckId
) {

    @Builder(toBuilder = true)
    public StudyRequest {}
}
