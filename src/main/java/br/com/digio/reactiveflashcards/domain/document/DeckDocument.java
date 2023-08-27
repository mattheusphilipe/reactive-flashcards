package br.com.digio.reactiveflashcards.domain.document;

import lombok.Builder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;
import java.util.Set;

/*
* Se fosse banco relacional iriamos definir uma relacionamento de N:N
* Mas como não é Apenas colocamos uma lista de cards em cada deck, Um NESTED PROP dentro do nosso deck.
* */
@Document(collection = "decks")
public record DeckDocument(
        @Id String id,
        String name,
        String description,
        Set<Card> cards, // não quero cards repetidos e  a ordenção não é relevante
        @CreatedDate
       @Field("created_at")
       OffsetDateTime createdAt,
        @LastModifiedDate
       @Field("updated_at")
       OffsetDateTime updatedAt
    ) {
    @Builder(toBuilder = true)
    public DeckDocument {}
}
