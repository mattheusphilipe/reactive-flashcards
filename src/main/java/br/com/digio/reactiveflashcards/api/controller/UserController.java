package br.com.digio.reactiveflashcards.api.controller;

import br.com.digio.reactiveflashcards.api.controller.request.UserRequest;
import br.com.digio.reactiveflashcards.api.controller.response.UserResponse;
import br.com.digio.reactiveflashcards.api.mapper.UserMapper;
import br.com.digio.reactiveflashcards.core.validation.MongoId;
import br.com.digio.reactiveflashcards.domain.service.UserService;
import br.com.digio.reactiveflashcards.domain.service.query.UserQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN;

@Validated
@RestController
@RequestMapping("users")
@Slf4j
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserMapper userMapper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Mono<UserResponse> save(@Valid @RequestBody final UserRequest request) {
        return userService.save(userMapper.toDocument(request))
                .doFirst(() -> log.info("===== Saving a user follow data {}", request))
                .map(document -> userMapper.toResponse(document)); // basicamente faz o mapeamento de um tipo para o outro, no caso recebe o document do Save que retorna um Mono de user Document
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "{id}")
    @ResponseStatus(OK)
    public Mono<UserResponse> findById(@PathVariable @Valid @MongoId(message = "{userController.id}") final String id) {
        //@MongoId para validar se é realmente um id do mongo db
        return userQueryService
                .findById(id)
                .doFirst(() -> log.info("===== Searching a user with follow id {}", id))
                .map(document -> userMapper.toResponse(document));
    }
}
