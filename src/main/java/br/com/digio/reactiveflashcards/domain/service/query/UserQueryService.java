package br.com.digio.reactiveflashcards.domain.service.query;

import br.com.digio.reactiveflashcards.domain.document.UserDocument;
import br.com.digio.reactiveflashcards.domain.exception.NotFoundException;
import br.com.digio.reactiveflashcards.domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.USER_EMAIL_NOT_UNIQUE;
import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.USER_NOT_FOUND;

@Service
@Slf4j
@AllArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    public Mono<UserDocument> findById(final String id) {
        return userRepository
                .findById(id)
                .doFirst(() -> log.info("==== try to find user with id {}", id))
                .filter(Objects::nonNull) // fazer um filtro nos resultos, retirar o que se quer.
                .switchIfEmpty(Mono.defer(
                        () -> Mono.error(new NotFoundException(USER_NOT_FOUND.params(id).getMessage()))
                )); /// Mono.defer, carregamento tardio de um dados === lazy loading
                    /// Apenas com o Mono.error iria dar certo, porém
                    /// Sem o Mono.defer, o erro seria disparado toda vez ao chamar o método finbById

    }

    public Mono<UserDocument> findByEmail(final String email) {
        return userRepository
                .findByEmail(email)
                .doFirst(() -> log.info("==== try to find user with e-mail {}", email))
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.defer(
                        () -> Mono.error(new NotFoundException(USER_EMAIL_NOT_UNIQUE.params(email).getMessage()))
                ));

    }
}
