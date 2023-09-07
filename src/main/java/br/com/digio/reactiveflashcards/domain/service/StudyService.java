package br.com.digio.reactiveflashcards.domain.service;

import br.com.digio.reactiveflashcards.domain.document.Card;
import br.com.digio.reactiveflashcards.domain.document.Question;
import br.com.digio.reactiveflashcards.domain.document.StudyCard;
import br.com.digio.reactiveflashcards.domain.document.StudyDocument;
import br.com.digio.reactiveflashcards.domain.dto.QuestionDTO;
import br.com.digio.reactiveflashcards.domain.dto.StudyDTO;
import br.com.digio.reactiveflashcards.domain.exception.DeckInStudyException;
import br.com.digio.reactiveflashcards.domain.exception.NotFoundException;
import br.com.digio.reactiveflashcards.domain.mapper.StudyDomainMapper;
import br.com.digio.reactiveflashcards.domain.repository.StudyRepository;
import br.com.digio.reactiveflashcards.domain.service.query.DeckQueryService;
import br.com.digio.reactiveflashcards.domain.service.query.StudyQueryService;
import br.com.digio.reactiveflashcards.domain.service.query.UserQueryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.DECK_IN_STUDY_FOUND;
import static br.com.digio.reactiveflashcards.domain.exception.BaseErrorMessage.STUDY_QUESTION_NOT_FOUND;

@Service
@AllArgsConstructor
@Slf4j
public class StudyService {

    private final UserQueryService userQueryService;
    private final DeckQueryService deckQueryService;
    private final StudyQueryService studyQueryService;
    private final StudyRepository studyRepository;
    private final StudyDomainMapper studyDomainMapper;

    // iniciar estudos
    public Mono<StudyDocument> start(final StudyDocument document) {
        var userExistBase = userQueryService.findById(document.userId()); // só para não disparar uma exception caso não exista.

       /*
       * Neste caso faço a verificação do estudo e volto para o meu fluxo normal com o Then...
       * */

//        return  verifyStudy(document).then(Mono.defer(() -> userExistBase))
        return  userExistBase
                .flatMap(user -> deckQueryService.findById(document.studyDeck().deckId()))//validar e pegar os cards do deck para jogar no noss estudo
                .doFirst(() -> log.info("builder().build(), {}", StudyDocument.builder().build()))
                .flatMap(deck -> fillDeckStudyCards(document, deck.cards()))
                .map(study -> {
                    study
                            .toBuilder()
                            .question(studyDomainMapper.
                                    generateRandomQuestion(study.studyDeck().cards())
                            ).build();
                    return study;
                })
                .doFirst(() -> log.info("==== Generating a primary random question"))
                .flatMap(study -> studyRepository.save(study))
                .doOnSuccess((study) -> log.info("==== A follow study {} was saved", study));
    }

    private Mono<StudyDocument> fillDeckStudyCards(final StudyDocument document, final Set<Card> cards) {
        // mapeamento para gerar uma pergunta aleatória a partir do nosso deck de estudo

        return Flux.fromIterable(cards)
                .doFirst(() -> log.info("==== Copying cards to new study {} - {}", document, cards))
                .map(card -> studyDomainMapper.toStudyCard(card))
                .collectList()
                // uso o document para pegar os cards  ao invés de uma instância nova, pois eu não quero perder a referência, o deckId
                .map(studyCards -> document.studyDeck().toBuilder().cards(Set.copyOf(studyCards)).build())
                .map(studyDeck -> document.toBuilder().studyDeck(studyDeck).build());
    }

    private Mono<Void> verifyStudy(final StudyDocument document) {

        return  studyQueryService
                .findPendingStudyByUserIdAndDeckId(document.userId(), document.studyDeck().deckId())
                .flatMap(study ->
                        Mono.defer(() ->
                                Mono.error(new DeckInStudyException(DECK_IN_STUDY_FOUND.params(document.userId(), document.studyDeck().deckId()).getMessage()))
                        )
                ).onErrorResume(NotFoundException.class, e -> Mono.empty())
                .then();

    }

    public Mono<StudyDocument> answer(final String id, final String answer) {
/*
    ficar atento a thenReturn quando tiver que propagar exception
  .flatMap(study -> studyQueryService.verifyIfFinished(study).thenReturn(study)) // se i thenReturn ficar aqui, ele vai ignorar uma possível excetion dentro do verifyIfFnished
*/


        // primeiro passo, responder a nossa questão
       return studyQueryService.findById(id) // procura o nosso estudo
                .flatMap(study -> studyQueryService.verifyIfFinished(study)) // Colocamos o thenReturn dentro do método, ou seja no final do fluxo, ainda sendo possível capturar a exceção
                .map(study -> studyDomainMapper.answer(study, answer))
                .zipWhen(this::getNextPossibilities) // método que junta o resultado desse fluxo com o fluxo de cima
                .map(tuple -> studyDomainMapper.toDTO(tuple.getT1(), tuple.getT2()))
        //zipWhen, retorna duas tuplas, primeiro valor com o fluxo do zip e o segundo com o valor do fluxo de cima
                .flatMap(study -> setNewQuestion(study))
                .map(studyDomainMapper::toDocument)
                .flatMap(studyRepository::save)
                .doFirst(() -> log.info("==== Saving answer and next question if have one"));

    }

    //Dicas/Tips: não se usar map após, Mono.empty, pois ele retorna Mono<Void> e o map não vai disparar exceção
    // correto é usar then ou thenReturn para retorna rvalor ou ThenMany pra mudar para flux

    public Mono<List<String>> getNextPossibilities(final StudyDocument document) {
        /*  Flux.fromIterable
         *  Não é uma operação null-safe, se a query estivesse vazia, teriamos problemas
         * Mas no caso desse método tenho garantia de que ela está preenchida.
         *Tenha certeza de tenha uma lista no fromIterable ou terá problemas para encontrar o erro
         * */
        return Flux.fromIterable(document.studyDeck().cards())// colocar nossos cards no fluxo
                .doFirst(() -> log.info("==== Getting question not used on question without correct  answers"))
                .map(StudyCard::front)  // pegamos apenas os cards que tem a pergunta
                // percorrer o fluxo somente se esse front não tiver nenhum correspondente com as perguntas já feita na nossa question
                .filter(asks -> document
                        .questions() // pegar as questões respondidas
                        .stream()
                        .filter(Question::isCorrect) // das questoões respondidas, quero somente as corretas
                        .map(Question::asked)// quero só as perguntas
                        .noneMatch(q -> q.equals(asks)) // Eliminando as perguntas já respondidas pelo usuário de forma correta
                ).collectList()
                .flatMap(asks -> removeLastAsk(asks, document.getLastAnsweredQuestion().asked()));
    }

    private Mono<? extends List<String>> removeLastAsk(List<String> asks, String asked) {
        // garantir que a próxima pergunta não seja repetida

        // pode acontecer da última pergunta do usuário responder que queremos desprezar,
        // ser a única na lista; Se isso acontecer, vai ter que deixar o usuário seguir em frente.
        return Mono.just(asks)
                .doFirst(() -> log.info("====  Removing last askqied if it's not a last pending question in study"))
                .filter(a -> a.size() == 1) // se só tem um item, segue em frente
                .switchIfEmpty(// caso tenha mais que dois itens...
                    Mono.defer( // remove da lsita a pergunta já perguntada
                            () ->
                                    Mono.just(
                                            asks
                                                    .stream().
                                                    filter(a -> !a.equals(asked))
                                                    .collect(Collectors.toList())
                                    )
                    )
                );

    }

    private Mono<StudyDTO> setNewQuestion(final StudyDTO dto) {
        return Mono.just(dto.hasAnyAnswered())
                .filter(BooleanUtils::isTrue) // não modifica, só verifica
                .switchIfEmpty( //método para disparar exceção
                    Mono.defer(
                            () -> Mono.error(
                                    new NotFoundException(STUDY_QUESTION_NOT_FOUND
                                            .params(dto.id())
                                            .getMessage()
                                    ))
                    )
                ).flatMap(hasAnyAnswer -> generateNextQuestion(dto))
                .map(question -> dto.toBuilder().question(question).build())// adiciona rminha nova pergunta as outras
                .onErrorResume(NotFoundException.class, e -> Mono.just(dto));
    }
    private Mono<QuestionDTO> generateNextQuestion(final StudyDTO dto) {
        /*
        * garantir que será igual a nossa ask
        * Temos que pegar a pergunta do nosso cars para ele poder gravar a resposta do uusário
        * Senão depois como vou conferir que o usuário respondeu de forma correta??
        * */
        return Mono
                .just(dto // pegar uma pergunta aleatória
                        .remainAsks()
                        .get(
                                new Random().nextInt(dto.remainAsks().size())
                        )
                ).doFirst(() -> log.info("==== Selecting next random question"))
                .map(ask -> dto
                        .studyDeck()
                        .cards()
                        .stream()
                        .filter(card -> card.front().equals(ask)) // achar o card que tem a pergunta ask
                        .map(studyDomainMapper::toQuestion)// setar data e hora que foi feita  pergunta e a resposta esperada
                        .findFirst()
                        .orElseThrow()
                );
    }
}
