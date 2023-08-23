package br.com.digio.reactiveflashcards.api.controller.response;

import br.com.digio.reactiveflashcards.core.validation.MongoId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record CardResponse(
        @JsonProperty("front")
        String front,
        @JsonProperty("back")
        String back
    ) {

    @Builder(toBuilder = true)
    public CardResponse {}
}
