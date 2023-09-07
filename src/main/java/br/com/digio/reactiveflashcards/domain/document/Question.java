package br.com.digio.reactiveflashcards.domain.document;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.Objects;

public record Question(
        @Field("asked_in")
        OffsetDateTime askedIn,
        String asked,
        @Field("answered_in")
        OffsetDateTime answeredIn,
        String answered,
        String expected // expected: correct answer
) {

    public Boolean isAnswered() {
        return Objects.nonNull(answeredIn);
    }

    public Boolean isNotAnswered() {
        return Objects.isNull(answeredIn);
    }

    public Boolean isCorrect() {
        // verificar se a pergunta foi respondida e se a resposta foi igual ao esperado.
        return isAnswered() && answered.equals(expected);
    }
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

        public QuestionBuilder asked(final String asked) {
            if (StringUtils.isNotBlank(asked)) {
                this.asked = asked;
                this.askedIn = OffsetDateTime.now();
            }
            return this;
        }
        public QuestionBuilder answered(final String answered) {
            if (StringUtils.isNotBlank(answered)) {
                this.answered = answered;
                this.answeredIn = OffsetDateTime.now();
            }
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
