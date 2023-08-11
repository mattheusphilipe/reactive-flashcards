package br.com.digio.reactiveflashcards;

import br.com.digio.reactiveflashcards.core.validation.SignatureType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

@SpringBootApplication
@EnableReactiveMongoAuditing(dateTimeProviderRef = "dateTimeProvider")
public class ReactiveFlashcardsApplication {

	public static void main(String[] args) {

		SignatureType signatureType = SignatureType.NAO_ASSINADO;
		SignatureType signatureType2 = SignatureType.DIGITAL;

		for(SignatureType s : SignatureType.values()) {
			System.out.println("Name: " + s.name());
			System.out.println("Value: " + s.getValue());
		}
		SpringApplication.run(ReactiveFlashcardsApplication.class, args);

	}

}
