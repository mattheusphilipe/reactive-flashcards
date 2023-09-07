package br.com.digio.reactiveflashcards.domain.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.*;

@Document(collection = "studies")
public record StudyDTO(
        String id,
        String userId, // referência para o usuário que está estudante, começou este estudo.
        Boolean complete,
        StudyDeckDTO studyDeck,// cópia da nossa entidade (todo deck, todo nosso estudo), o usuário vai começar o estudo dele, na hora que ele for pegar, caso haja alguma alteração eu não perder o histíco do que foi feito anteriormente.
        List<QuestionDTO> questions,
        List<String> remainAsks,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
        ) {

        public static StudyDocumentBuilder builder() {
                return new StudyDocumentBuilder();
        }

        public StudyDocumentBuilder toBuilder() {
                return new StudyDocumentBuilder(id, userId, studyDeck, questions, remainAsks, createdAt, updatedAt);
        }

        public Boolean hasAnyAnswered() {
                return !CollectionUtils.isEmpty(remainAsks);
        }

        @NoArgsConstructor
        @AllArgsConstructor
        public static class StudyDocumentBuilder {
                private String id;
                private String userId; // referência para o usuário que está estudante, começou este estudo.
                private StudyDeckDTO studyDeck;// cópia da nossa entidade (todo deck, todo nosso estudo), o usuário vai começar o estudo dele, na hora que ele for pegar, caso haja alguma alteração eu não perder o histíco do que foi feito anteriormente.
                private List<QuestionDTO> questions = new ArrayList<>();
                List<String> remainAsks = new ArrayList<>();
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
                public StudyDocumentBuilder studyDeck(final StudyDeckDTO studyDeck) {
                        this.studyDeck = studyDeck;
                        return this;
                }
                public StudyDocumentBuilder questions(final List<QuestionDTO> questions) {
                        this.questions = questions;
                        return this;
                }
                public StudyDocumentBuilder question(final QuestionDTO question) {
                        this.questions.add(question);
                        return this;
                }
                public StudyDocumentBuilder remainAsks(final List<String> remainAsks) {
                        this.remainAsks = remainAsks;
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

                public StudyDTO build() {
                        List<QuestionDTO> correctQuestions = questions.stream().filter(QuestionDTO::isCorrect).toList();
                        var complete = correctQuestions.size() == studyDeck.cards().size();

                        return new StudyDTO(id, userId, complete, studyDeck, questions, remainAsks,createdAt, updatedAt);
                }
        }
}
