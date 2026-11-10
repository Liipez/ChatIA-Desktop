package br.com.chatia.controller;

import br.com.chatia.service.PasswordRecoveryService;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.net.ConnectException;
import java.util.function.Consumer;

/**
 * Recebe as ações da ForgotPasswordView e repassa ao PasswordRecoveryService.
 * As operações rodam fora da thread do JavaFX para não travar a tela.
 */
public class ForgotPasswordController {

    private PasswordRecoveryService service;

    private PasswordRecoveryService service() {
        if (service == null) {
            service = new PasswordRecoveryService();
        }
        return service;
    }

    public void requestCode(String email, Runnable onSuccess, Consumer<String> onError) {
        runAsync(() -> {
            service().requestReset(email);
            return null;
        }, onSuccess, onError);
    }

    public void resetPassword(String email, String code, String password, String confirm,
                              Runnable onSuccess, Consumer<String> onError) {
        runAsync(() -> {
            service().resetPassword(email, code, password, confirm);
            return null;
        }, onSuccess, onError);
    }

    private void runAsync(java.util.concurrent.Callable<Void> action,
                          Runnable onSuccess, Consumer<String> onError) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                return action.call();
            }
        };

        task.setOnSucceeded(e -> onSuccess.run());
        task.setOnFailed(e -> {
            Throwable error = task.getException();
            System.err.println("[Recuperação de senha] " + error);
            Platform.runLater(() -> onError.accept(friendlyMessage(error)));
        });

        Thread thread = new Thread(task, "password-recovery");
        thread.setDaemon(true);
        thread.start();
    }

    private String friendlyMessage(Throwable error) {
        if (error instanceof IllegalArgumentException
                || error instanceof IllegalStateException) {
            return error.getMessage();
        }
        if (error instanceof jakarta.mail.MessagingException) {
            return "Não foi possível enviar o e-mail. Verifique sua conexão e tente novamente.";
        }
        if (error instanceof ConnectException
                || error instanceof com.mongodb.MongoException) {
            return "Não foi possível acessar o banco de dados. Verifique sua conexão.";
        }
        return "Ocorreu um erro inesperado. Tente novamente.";
    }
}
