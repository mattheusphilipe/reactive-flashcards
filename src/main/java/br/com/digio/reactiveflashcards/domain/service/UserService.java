package br.com.digio.reactiveflashcards.domain.service;

import br.com.digio.reactiveflashcards.domain.document.UserDocument;
import br.com.digio.reactiveflashcards.domain.repository.UserRepository;
import br.com.digio.reactiveflashcards.domain.service.query.UserQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j // logs no consle caso necessário
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;

    public Mono<UserDocument> save(final UserDocument document) {
        return userRepository
                .save(document)
                .doFirst(() -> log.info("=== tried to save a follow user {} ", document)); // operação realizada antes do save.
    }

    public Mono<UserDocument> update(final UserDocument document) {
        Mono<UserDocument> userFound = userQueryService.findById(document.id());

        return userFound
                .map(user -> document
                        .toBuilder()
                        .createdAt(user.createdAt()) //para manter as datas, pois se passar vazio o Mongo não gerencia
                        .updatedAt(user.updatedAt())
                        .build()
                )
                .flatMap(userRepository::save) /// operação não blocante
                .doFirst(() -> log.info("==== Trying to update a user with follow {}", document));
    }

    public Mono<Void> delete(final String id) {
        return userQueryService
                .findById(id)
                .flatMap(userDocument -> userRepository.delete(userDocument))
                .doFirst(() -> log.info("=== Trying to delete a user with follow id {}", id));
    }
}
