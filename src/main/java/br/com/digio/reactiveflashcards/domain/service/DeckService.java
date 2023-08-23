package br.com.digio.reactiveflashcards.domain.service;

import br.com.digio.reactiveflashcards.domain.repository.DeckRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class DeckService {
    private final DeckRepository deckRepository;
}