package br.com.digio.reactiveflashcards.api.mapper;

import br.com.digio.reactiveflashcards.domain.dto.MailMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;

//@Component
public abstract class MailMapperDecorator implements MailMapper{


    @Qualifier("delegate")
//    @Autowired
    private MailMapper mailMapper;
    @Override
    public MimeMessageHelper toMimeMessageHelper(final MimeMessageHelper helper, final MailMessageDTO mailMessageDTO, final String sender, final String body) throws MessagingException {

        mailMapper.toMimeMessageHelper(helper, mailMessageDTO, sender, body);
        helper.setText(body, true);
        return helper;
    }


}
