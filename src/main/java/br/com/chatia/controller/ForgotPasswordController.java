package br.com.chatia.controller;

import br.com.chatia.service.PasswordRecoveryService;
import br.com.chatia.util.AsyncRunner;

import java.util.function.Consumer;

/** Recebe as ações da ForgotPasswordView e repassa ao PasswordRecoveryService. */
public class ForgotPasswordController {

    private PasswordRecoveryService service;

    private synchronized PasswordRecoveryService service() {
        if (service == null) {
            service = new PasswordRecoveryService();
        }
        return service;
    }

    public void requestCode(String email, Runnable onSuccess, Consumer<String> onError) {
        AsyncRunner.run(() -> {
            service().requestReset(email);
            return null;
        }, ignored -> onSuccess.run(), onError);
    }

    public void resetPassword(String email, String code, String password, String confirm,
                              Runnable onSuccess, Consumer<String> onError) {
        AsyncRunner.run(() -> {
            service().resetPassword(email, code, password, confirm);
            return null;
        }, ignored -> onSuccess.run(), onError);
    }
}
