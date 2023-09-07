package br.com.digio.reactiveflashcards.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

public record StudyCardDTO(String front, String back) { // pergunta e resposta

    @Builder(toBuilder = true)
    public StudyCardDTO {}
}
