package br.com.digio.reactiveflashcards.api.controller;

import br.com.digio.reactiveflashcards.api.controller.request.StudyRequest;
import br.com.digio.reactiveflashcards.api.controller.response.QuestionResponse;
import br.com.digio.reactiveflashcards.api.mapper.StudyMapper;
import br.com.digio.reactiveflashcards.domain.service.StudyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("studies")
@Slf4j
@AllArgsConstructor
@Validated
public class StudyController {

    private final StudyService studyService;
    private final StudyMapper studyMapper;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<QuestionResponse> start(@Valid @RequestBody final StudyRequest request) {
        return studyService
                .start(studyMapper.toDocument(request))
                .doFirst(() -> log.info("==== Trying to create a tudy with follow data request {}", request))
                .map(document -> studyMapper.toResponse(document.getLastQuestionPending()));
    }
}
