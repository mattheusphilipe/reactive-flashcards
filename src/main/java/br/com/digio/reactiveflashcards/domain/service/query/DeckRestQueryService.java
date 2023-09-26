package br.com.digio.reactiveflashcards.domain.service.query;

import br.com.digio.reactiveflashcards.core.DeckApiConfig;
import br.com.digio.reactiveflashcards.domain.dto.AuthDTO;
import br.com.digio.reactiveflashcards.domain.dto.DeckDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class DeckRestQueryService {

    private final WebClient webClient;
    private final DeckApiConfig deckApiConfig;
    private final Mono<AuthDTO> authCache;

    public DeckRestQueryService(WebClient webClient, DeckApiConfig deckApiConfig) {
        this.webClient = webClient;
        this.deckApiConfig = deckApiConfig;
        authCache = Mono.
                from(getAuth())
                .cache(
                        (auth) -> Duration.ofSeconds(auth.expiresIn()),
                        throwable -> Duration.ZERO,
                        () -> Duration.ZERO
                );
    }

    public Flux<DeckDTO> getDecks() {

        return authCache.flatMapMany(auth -> doGetDecks(auth.token()));
    }

    private Flux<DeckDTO> doGetDecks(final String token) {
        return webClient
                .get()
                .uri(deckApiConfig.getDecksUri())
                .header("token", token)
                .retrieve()
//                .onStatus(status -> status.isError(), () -> Mono.) //tratamento de erro
                .bodyToFlux(DeckDTO.class);
    }

    private Mono<AuthDTO> getAuth() {
        return webClient
                .post()
                .uri(deckApiConfig.getAuthUri())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(AuthDTO.class);
    }

}
