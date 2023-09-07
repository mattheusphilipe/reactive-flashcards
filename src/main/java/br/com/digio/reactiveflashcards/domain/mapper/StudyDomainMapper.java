package br.com.digio.reactiveflashcards.domain.mapper;

import br.com.digio.reactiveflashcards.domain.document.Card;
import br.com.digio.reactiveflashcards.domain.document.Question;
import br.com.digio.reactiveflashcards.domain.document.StudyCard;
import br.com.digio.reactiveflashcards.domain.document.StudyDocument;
import br.com.digio.reactiveflashcards.domain.dto.StudyCardDTO;
import br.com.digio.reactiveflashcards.domain.dto.QuestionDTO;
import br.com.digio.reactiveflashcards.domain.dto.StudyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface StudyDomainMapper {

    StudyCard toStudyCard(final Card cards);
    default Question generateRandomQuestion(final Set<StudyCard> cards) {
        var values = new ArrayList<>(cards);
        var random = new Random();
        var position = random.nextInt(values.size());
        return toQuestion(values.get(position));
    }

    @Mapping(target = "asked", source = "front")
//    @Mapping(target = "askedIn", expression = "java(java.time.OffsetDateTime.now())")
    @Mapping(target = "answered", ignore = true)
    @Mapping(target = "expected", source = "back")
    Question toQuestion(final StudyCard card);

     @Mapping(target = "asked", source = "front")
     @Mapping(target = "answered", ignore = true)
     @Mapping(target = "expected", source = "back")
     QuestionDTO toQuestion(final StudyCardDTO  card);

    default StudyDocument answer(final StudyDocument document, final String answer) {
        Question currentQuestion = document.getLastPendingQuestion();
        var questions = document.questions();
        var curIndexQuestion = questions.indexOf(currentQuestion);

        currentQuestion = currentQuestion.toBuilder().answered(answer).build();

        questions.set(curIndexQuestion, currentQuestion);

        return document.toBuilder().questions(questions).build();
    }

    @Mapping(target = "question", ignore = true)
    StudyDTO toDTO(final StudyDocument document, final List<String> remainAsks);

    StudyDocument toDocument(final StudyDTO dto);
}
