package br.com.digio.reactiveflashcards.domain.service;

import br.com.digio.reactiveflashcards.domain.document.UserDocument;
import br.com.digio.reactiveflashcards.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j // logs no consle caso necessário
@Service
public class UserService {

    private final UserRepository userRepository;

    public Mono<UserDocument> save(final UserDocument document) {
        return userRepository
                .save(document)
                .doFirst(() -> log.info("=== tried to save a follow document {} ", document)); // operação realizada antes do save.
    }
}
