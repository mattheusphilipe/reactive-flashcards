package br.com.digio.reactiveflashcards.api.mapper;

import br.com.digio.reactiveflashcards.domain.document.DeckDocument;
import br.com.digio.reactiveflashcards.domain.document.StudyDocument;
import br.com.digio.reactiveflashcards.domain.document.UserDocument;
import br.com.digio.reactiveflashcards.domain.dto.MailMessageDTO;
import br.com.digio.reactiveflashcards.domain.dto.StudyDTO;
import org.mapstruct.*;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@DecoratedWith(MailMapperDecorator.class)
public interface MailMapper {

    @Mapping(target = "username", source = "user.name")
    @Mapping(target = "destination", source = "user.email")
    @Mapping(target = "subject", constant = "Relatório de estudos")
    @Mapping(target =  "questions", source = "study.questions")
    MailMessageDTO toDTO(final StudyDTO study, final DeckDocument deck, final UserDocument user);


    @Mapping(target = "to", expression = "java(new String[]{mailMessageDTO.destination()})")
    @Mapping(target = "from", source= "sender")
    @Mapping(target = "subject", source= "mailMessageDTO.subject") // temos mais de um subject, tem que definir o soruce todo apra o map struct não se perder
    @Mapping(target = "fileTypeMap", ignore = true)
    @Mapping(target = "encodeFilenames", ignore = true)
    @Mapping(target = "validateAddresses", ignore = true)
    @Mapping(target = "replyTo", ignore = true)
    @Mapping(target = "cc", ignore = true)
    @Mapping(target = "bcc", ignore = true)
    @Mapping(target = "priority", ignore = true)
    @Mapping(target = "sentDate", ignore = true)
    @Mapping(target = "text", ignore = true)
    // o @MappingTarget, pois quero uma variaǘel que seráo alvo do nosso mpeamento
    // naõ queremos criar uma nova instância aqui, queremos passar uma instância
    // que o map struct vai poder usar para preencher usando nossa configuração
    MimeMessageHelper toMimeMessageHelper(@MappingTarget final MimeMessageHelper helper,
                                          final MailMessageDTO mailMessageDTO,
                                          final String sender,
                                          final String body
                                          ) throws MessagingException;
}
