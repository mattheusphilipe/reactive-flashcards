package br.com.digio.reactiveflashcards;

import br.com.digio.reactiveflashcards.core.validation.SignatureType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
public class ReactiveFlashcardsApplication {

	public static void main(String[] args) {

		SignatureType signatureTypeNotSigned = SignatureType.NAO_ASSINADO;
		SignatureType signatureTypeDigital = SignatureType.DIGITAL;

		for(SignatureType sig : SignatureType.values()) {
			System.out.println("Name: " + sig.name());
			System.out.println("Value: " + sig.getValue());
		}
		SpringApplication.run(ReactiveFlashcardsApplication.class, args);

	}

}
