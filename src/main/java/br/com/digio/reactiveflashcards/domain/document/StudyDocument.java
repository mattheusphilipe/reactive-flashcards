package br.com.digio.reactiveflashcards.domain.document;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Document(collection = "studies")
public record StudyDocument(
        @Id String id,
        @Field("user_id")
        String userId, // referência para o usuário que está estudante, começou este estudo.
        Boolean complete,
        @Field("study_deck")
        StudyDeck studyDeck,// cópia da nossa entidade (todo deck, todo nosso estudo), o usuário vai começar o estudo dele, na hora que ele for pegar, caso haja alguma alteração eu não perder o histíco do que foi feito anteriormente.
        List<Question> questions,
        @CreatedDate
        @Field("created_at")
        OffsetDateTime createdAt,
        @LastModifiedDate
        @Field("updated_at")
        OffsetDateTime updatedAt
        ) {

        public static StudyDocumentBuilder builder() {
                return new StudyDocumentBuilder();
        }

        public StudyDocumentBuilder toBuilder() {
                return new StudyDocumentBuilder(id, userId, studyDeck, questions, createdAt, updatedAt);
        }

        public Question getLastPendingQuestion() {
                return questions
                        .stream()
                        .filter(question -> Objects.isNull(question.answeredIn()))
                        .findFirst()
                        .orElseThrow(); // caso dê problema e se chega rasqui já foi concluído nosso estudo
        }

        public Question getLastAnsweredQuestion() {
                return questions
                        .stream()
                        .filter(q -> Objects.nonNull(q.answeredIn()))
                        .max(Comparator.comparing(Question::askedIn))
                        .orElseThrow();
        }

        @NoArgsConstructor
        @AllArgsConstructor
        public static class StudyDocumentBuilder {
                private String id;
                private String userId; // referência para o usuário que está estudante, começou este estudo.
                private StudyDeck studyDeck;// cópia da nossa entidade (todo deck, todo nosso estudo), o usuário vai começar o estudo dele, na hora que ele for pegar, caso haja alguma alteração eu não perder o histíco do que foi feito anteriormente.
                private List<Question> questions = new ArrayList<>();
                private OffsetDateTime createdAt;
                private OffsetDateTime updatedAt;

                public StudyDocumentBuilder id(final String id) {
                        this.id = id;
                        return this;
                }
                public StudyDocumentBuilder userId(final String userId) {
                        this.userId = userId;
                        return this;
                }
                public StudyDocumentBuilder studyDeck(final StudyDeck studyDeck) {
                        this.studyDeck = studyDeck;
                        return this;
                }
                public StudyDocumentBuilder questions(final List<Question> questions) {
                        this.questions = questions;
                        return this;
                }
                public StudyDocumentBuilder question(final Question question) {
                        this.questions.add(question);
                        return this;
                }
                public StudyDocumentBuilder createdAt(final OffsetDateTime createdAt) {
                        this.createdAt = createdAt;
                        return this;
                }
                public StudyDocumentBuilder updatedAt(final OffsetDateTime updatedAt) {
                        this.updatedAt = updatedAt;
                        return this;
                }

                public StudyDocument build() {
                        List<Question> correctQuestions = questions
                                .stream()
                                .filter(Question::isCorrect)
                                .toList();

                        var complete = studyDeck != null && correctQuestions.size() == studyDeck.cards().size();
                        return new StudyDocument(id, userId, complete, studyDeck, questions, createdAt, updatedAt);
                }
        }
}
