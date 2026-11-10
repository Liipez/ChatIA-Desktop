package br.com.chatia.service;

import java.io.IOException;

/** Erro retornado pela API da Groq, com o código HTTP para tratamento. */
public class GroqException extends IOException {

    private final int statusCode;

    public GroqException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
