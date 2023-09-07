package br.com.digio.reactiveflashcards.api.mapper;

import br.com.digio.reactiveflashcards.api.controller.request.StudyRequest;
import br.com.digio.reactiveflashcards.api.controller.response.AnswerQuestionResponse;
import br.com.digio.reactiveflashcards.api.controller.response.QuestionResponse;
import br.com.digio.reactiveflashcards.domain.document.Question;
import br.com.digio.reactiveflashcards.domain.document.StudyDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StudyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studyDeck.deckId", source = "deckId") // diz que tenho que pegar esse valor dentro do parâmetro request
    // se fosse mais deum parâmetro na assinatura do método então teria que dizer qual o nome do parâemtro: ex request.deckId
    @Mapping(target = "studyDeck.cards", ignore = true)
    @Mapping(target = "questions", ignore = true)
    @Mapping(target = "question", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    StudyDocument toDocument(final StudyRequest request);

    QuestionResponse toResponse(final Question question, final String id);

    AnswerQuestionResponse toResponse(final Question question);
}
