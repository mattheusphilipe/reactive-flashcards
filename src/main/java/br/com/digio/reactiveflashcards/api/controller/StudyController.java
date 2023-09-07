package br.com.digio.reactiveflashcards.api.controller;

import br.com.digio.reactiveflashcards.api.controller.request.AnswerQuestionRequest;
import br.com.digio.reactiveflashcards.api.controller.request.StudyRequest;
import br.com.digio.reactiveflashcards.api.controller.response.AnswerQuestionResponse;
import br.com.digio.reactiveflashcards.api.controller.response.QuestionResponse;
import br.com.digio.reactiveflashcards.api.mapper.StudyMapper;
import br.com.digio.reactiveflashcards.core.validation.MongoId;
import br.com.digio.reactiveflashcards.domain.service.StudyService;
import br.com.digio.reactiveflashcards.domain.service.query.StudyQueryService;
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
    private final StudyQueryService studyQueryService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<QuestionResponse> start(@Valid @RequestBody final StudyRequest request) {
        return studyService
                .start(studyMapper.toDocument(request))
                .doFirst(() -> log.info("==== Trying to create a study with follow data request {}", request))
                .map(document -> studyMapper.toResponse(document.getLastPendingQuestion(), document.id()));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE, value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<QuestionResponse> getCurrentQuestion(@Valid @PathVariable @MongoId(message = "{studyController.id}") final String id) {
        return studyQueryService
                .getLastPendingQuestion(id)
                .doFirst(() -> log.info("==== Trying to get a next question in study {}", id))
                .map(question -> studyMapper.toResponse(question, id));
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE, value = "{id}/answer")
    @ResponseStatus(HttpStatus.OK)
    public Mono<AnswerQuestionResponse> answer(
            @Valid @PathVariable @MongoId(message = "{studyController.id}") final String id,
            @Valid @RequestBody final AnswerQuestionRequest request
            ) {

        var answer = request.answer();
        return studyService.answer(id, answer)
                .doFirst(() -> log.info("==== Trying to answer pending question in study {} with {}", id, answer))
                .map(
                        document -> studyMapper.toResponse(document.getLastAnsweredQuestion())
                );
    }

}
