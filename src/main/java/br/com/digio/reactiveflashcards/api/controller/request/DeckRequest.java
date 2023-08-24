package br.com.digio.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public record DeckRequest(
        @JsonProperty("name")
        @NotBlank
        @Size(min = 1, max = 255)
        String name,
        @JsonProperty("description")
        @NotBlank
        @Size(min = 1, max = 255)
        String description,
        @Valid // como é um nestedObject, tem que anotar com @Valid para as validações funcionarem
        @Size(min = 3)
        @NotNull
        @JsonProperty("cards")
        Set<CardRequest> cards
      ) {

    @Builder(toBuilder = true)
    public DeckRequest {}
}
