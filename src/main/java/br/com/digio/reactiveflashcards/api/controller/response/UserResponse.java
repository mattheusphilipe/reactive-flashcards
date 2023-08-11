package br.com.digio.reactiveflashcards.api.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record UserResponse(
        @JsonProperty("id")
       String id,
        @JsonProperty("name")
       String email,
        @JsonProperty("email")
       String name
    ) {

    @Builder(toBuilder = true)
    public UserResponse {}
}
