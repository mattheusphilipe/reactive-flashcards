package br.com.digio.reactiveflashcards.domain.document;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

public record Question(
        @Field("asked_in")
        OffsetDateTime askedIn,
        String asked,
        @Field("answered_in")
        OffsetDateTime answeredIn,
        String answered,
        String expected // expected: correct answer
) {

    public static QuestionBuilder builder() {
        return new QuestionBuilder();
    }

    public QuestionBuilder toBuilder() {
        return new QuestionBuilder(askedIn, asked, answeredIn, answered, expected);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionBuilder {
        private OffsetDateTime askedIn;
        private String asked;
        private OffsetDateTime answeredIn;
        private String answered;
        private String expected;

        public QuestionBuilder askedIn(final OffsetDateTime askedIn) {
            this.askedIn = askedIn;
            return this;
        }
        public QuestionBuilder answeredIn(final OffsetDateTime answeredIn) {
            this.answeredIn = answeredIn;
            return this;
        }
        public QuestionBuilder asked(final String asked) {
            this.asked = asked;
            return this;
        }
        public QuestionBuilder answered(final String answered) {
            this.answered = answered;
            return this;
        }
        public QuestionBuilder expected(final String expected) {
            this.expected = expected;
            return this;
        }

        public Question build() {
            return new Question(askedIn, asked, answeredIn, answered, expected);
        }
    }

}
