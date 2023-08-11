package br.com.digio.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record UserRequest(
        @NotBlank
        @Size(min = 1, max = 255)
        @JsonProperty("name")
        String email,
        @NotBlank
        @Size(min = 1, max = 255)
        @Email
        @JsonProperty("email")
        String name
    ) {

    @Builder(toBuilder = true)
    public UserRequest {}
}
