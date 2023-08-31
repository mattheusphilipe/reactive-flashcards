package br.com.digio.reactiveflashcards.api.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record AnswerQuestionRequest(
        @JsonProperty("answer")
        @NotBlank
        @Size(min = 1, max = 255)
        String answer) {

    @Builder(toBuilder = true)
    public AnswerQuestionRequest {}

}