package br.com.digio.reactiveflashcards.core.mongo.provider;

import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

@Component("dateTimeProvider")
public class OffsetDateTImeProvider implements DateTimeProvider {

    // classe provedora das datas
    @Override
    public Optional<TemporalAccessor> getNow() {

        return Optional.of(OffsetDateTime.now(UTC));
    }
}
