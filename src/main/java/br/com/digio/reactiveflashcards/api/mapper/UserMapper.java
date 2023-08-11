package br.com.digio.reactiveflashcards.api.mapper;

import br.com.digio.reactiveflashcards.api.controller.request.UserRequest;
import br.com.digio.reactiveflashcards.api.controller.response.UserResponse;
import br.com.digio.reactiveflashcards.domain.document.UserDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/*
* Map Struct. Essa classe para nos poupar de ficar escrevendo código de mapeamento de entidade, focarmos no que interessa: o webflux
* */
@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserDocument toDocument(final UserRequest request);
    UserResponse toResponse(final UserDocument document);
}
