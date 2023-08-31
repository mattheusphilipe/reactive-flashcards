package br.com.digio.reactiveflashcards.domain.service;

import br.com.digio.reactiveflashcards.domain.document.UserDocument;
import br.com.digio.reactiveflashcards.domain.exception.EmailNotUniqueException;
import br.com.digio.reactiveflashcards.domain.exception.NotFoundException;
import br.com.digio.reactiveflashcards.domain.repository.UserRepository;
import br.com.digio.reactiveflashcards.domain.service.query.UserQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.USER_EMAIL_NOT_UNIQUE;

@AllArgsConstructor
@Slf4j // logs no consle caso necessário
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;

    public Mono<UserDocument> save(final UserDocument document) {

        /*
        * Antes de salvar o registor do usuário se ele encontrar pelo e-mail,
        * filtro o valor que é null e forço ele disparar uma exceção no switchifEmpty,
        * pois se encontra o registro filtrando nulo que eu sei que será nulo vai disparar a exceção.
        *Se não achar o usuário ele vai cair no onErrorResume.
        * */

        return userQueryService.findByEmail(document.email())
                .doFirst(() -> log.info("=== tried to save a follow user {} ", document)) // operação realizada antes do save.
                .filter(value -> Objects.isNull(value))
                .switchIfEmpty(
                        Mono.defer(
                                () -> Mono.error(
                                        new EmailNotUniqueException(USER_EMAIL_NOT_UNIQUE.params(document.email()).getMessage())
                                )
                        )
                )
                .onErrorResume(NotFoundException.class, e -> userRepository.save(document)); // caso não ache o e-mail, salvar o registro
    }

    /*
    * Validação para garantir que o e-mail esteja sendo utilzado apenas por um usuário
    * */
    private Mono<Void> verifyEmail(final UserDocument document) {
//        OBS IMPORTANT: sempre colocar as tratativas para o cenários de sucesso e no final os de insucesso
       return userQueryService.findByEmail(document.email())
               .filter(storedDocument -> storedDocument.id().equals(document.id()))
               .switchIfEmpty(
                            Mono.defer(
                            () -> Mono.error(
                                new EmailNotUniqueException(USER_EMAIL_NOT_UNIQUE.params(document.email()).getMessage())
                            )
                        )
               )
               .onErrorResume(NotFoundException.class, e -> Mono.empty())
               .then();
    }

    public Mono<UserDocument> update(final UserDocument document) {

        Mono<UserDocument> userFound = userQueryService.findById(document.id());

//        thenReturn para o caso de queremos retornar um valor, e finalizar a execução

        /*
        * Then...
        * toda vez que queriamos tranformar nosso fluxo em outro tipo de dados, chamavamos o flatMap
        * Só que aqui nós queremos voltar um Mono<Void>, se chamassemos o flatMap ele não ia funcionar
        * flatMap não dispara excecão, ele iria seguir o fluxo, só que não iria conseguir, porque não teria o que
        * transformar. O idel é chamar o Then, toda vez que temos um fluxo que retorna o Mono<Void> e queremos
        * continuar o fluxo, chamamos o then
        *
        * QUANDO QUEREMOS TAMBÉM FORÇAR UM RETORNO MONO<VOID>
        * */
        return verifyEmail(document).then(userFound
                .map(user -> document
                        .toBuilder()
                        .createdAt(user.createdAt()) //para manter as datas, pois se passar vazio o Mongo não gerencia
                        .updatedAt(user.updatedAt())
                        .build()
                )
                .flatMap(userRepository::save) /// operação não blocante
                .doFirst(() -> log.info("==== Trying to update a user with follow {}", document))
        );

    }

    public Mono<Void> delete(final String id) {
        return userQueryService
                .findById(id)
                .flatMap(userDocument -> userRepository.delete(userDocument))
                .doFirst(() -> log.info("=== Trying to delete a user with follow id {}", id));
    }

    public Flux<UserDocument> findAll() {
        return userRepository.findAll();
    }
}
