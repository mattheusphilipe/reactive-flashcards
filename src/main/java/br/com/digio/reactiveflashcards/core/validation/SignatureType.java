package br.com.digio.reactiveflashcards.core.validation;

public enum SignatureType {
    NAO_ASSINADO("Não assinado"),
    DIGITAL("Digital"),
    ELETRONICA("Eletrônica");

    private final String value;
    SignatureType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
