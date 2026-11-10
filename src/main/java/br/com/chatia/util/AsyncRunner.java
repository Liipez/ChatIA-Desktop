package br.com.chatia.util;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

/**
 * Executa operações demoradas (banco, rede, IA) fora da thread do JavaFX,
 * para a interface não travar. Os callbacks voltam na thread do JavaFX.
 */
public final class AsyncRunner {

    private AsyncRunner() {
    }

    public static <T> void run(Callable<T> action,
                               Consumer<T> onSuccess,
                               Consumer<String> onError) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return action.call();
            }
        };

        task.setOnSucceeded(e -> onSuccess.accept(task.getValue()));
        task.setOnFailed(e -> {
            Throwable error = task.getException();
            System.err.println("[HyvexIA] Erro: " + error);
            onError.accept(ErrorMessages.friendly(error));
        });

        Thread thread = new Thread(task, "chatia-worker");
        thread.setDaemon(true);
        thread.start();
    }
}
