package br.com.digio.reactiveflashcards.domain.document;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Document(collection = "studies")
public record StudyDocument(
        @Id String id,
        String userId, // referência para o usuário que está estudante, começou este estudo.
        StudyDeck studyDeck,// cópia da nossa entidade (todo deck, todo nosso estudo), o usuário vai começar o estudo dele, na hora que ele for pegar, caso haja alguma alteração eu não perder o histíco do que foi feito anteriormente.
        List<Question> questions,
        @CreatedDate
        @Field("created_at")
        OffsetDateTime createdAt,
        @LastModifiedDate
        @Field("updated_at")
        OffsetDateTime updatedAt
        ) {

        @Builder(toBuilder = true)
        public StudyDocument {  }

        public void addQuestion(final Question question) {

                questions.add(question);
        }

        public Question getLastQuestionPending() {
                return questions
                        .stream()
                        .filter(question -> Objects.isNull(question.answeredIn()))
                        .findFirst()
                        .orElseThrow(); // caso dê problema e se chega rasqui já foi concluído nosso estudo
        }
}
