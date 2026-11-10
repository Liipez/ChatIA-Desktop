package br.com.chatia.util;

import br.com.chatia.service.GroqException;
import com.mongodb.MongoException;
import com.mongodb.MongoSecurityException;
import com.mongodb.MongoTimeoutException;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpTimeoutException;

/**
 * Converte exceções técnicas em mensagens compreensíveis para o usuário.
 * O stack trace fica só no console; o usuário nunca o vê.
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static String friendly(Throwable error) {
        if (error == null) {
            return "Ocorreu um erro inesperado. Tente novamente.";
        }

        // Erros de validação/regra de negócio já têm mensagem amigável.
        if (error instanceof IllegalArgumentException
                || error instanceof IllegalStateException) {
            return error.getMessage();
        }

        if (error instanceof GroqException groq) {
            return switch (groq.getStatusCode()) {
                case 401, 403 -> "A chave da API Groq é inválida. Verifique a variável GROQ_API_KEY.";
                case 429 -> "Limite de uso da IA atingido. Aguarde alguns instantes e tente novamente.";
                default -> groq.getStatusCode() >= 500
                        ? "O serviço de IA está indisponível no momento. Tente novamente mais tarde."
                        : "A IA não conseguiu processar a solicitação.";
            };
        }

        if (error instanceof HttpConnectTimeoutException
                || error instanceof HttpTimeoutException) {
            return "A IA demorou demais para responder. Verifique sua internet e tente novamente.";
        }

        if (error instanceof MongoSecurityException) {
            return "Falha de autenticação no banco de dados. Verifique a variável MONGODB_URI.";
        }

        if (error instanceof MongoTimeoutException) {
            return "Não foi possível conectar ao banco de dados. Verifique sua internet.";
        }

        if (error instanceof MongoException) {
            return "Erro ao acessar o banco de dados. Tente novamente.";
        }

        if (error instanceof ConnectException
                || error instanceof UnknownHostException) {
            return "Sem conexão com a internet. Verifique sua rede e tente novamente.";
        }

        if (error instanceof jakarta.mail.MessagingException) {
            return "Não foi possível enviar o e-mail. Verifique sua conexão e tente novamente.";
        }

        if (error.getCause() != null && error.getCause() != error) {
            return friendly(error.getCause());
        }

        return "Ocorreu um erro inesperado. Tente novamente.";
    }
}
