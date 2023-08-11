package br.com.digio.reactiveflashcards.core.validation;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class MonoIdValidator implements ConstraintValidator<MongoId, String> { // MongoId (nossa notação que estamos usando na classe), String (o tipo  que vai validar)

    @Override
    public void initialize(MongoId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        log.info("==== checking if {} is a valid mongoDbID");
        return StringUtils.isNotBlank(value) && ObjectId.isValid(value); // fazer uma validação
    }
}