package br.com.digio.reactiveflashcards.core.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;
import java.util.Date;

public class DateToOffsetDateTimeConverter implements Converter<OffsetDateTime, Date> {

    /// converter do java para o mongo
    @Override
    public Date convert(OffsetDateTime source) {
        return Date.from(source.toInstant()); // conversão de data para o Mongo que só dá suporte o Date do Java
    }


}