package br.com.digio.reactiveflashcards.domain.document;


import lombok.Builder;

public record StudyCard(String front, String back) { // pergunta e resposta

    @Builder(toBuilder = true) // Como é Record ele não deixa anotar na declaração da classe, apenas no construtor aqui
    public StudyCard {}
}
