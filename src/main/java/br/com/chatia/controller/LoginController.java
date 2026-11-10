package br.com.chatia.controller;

import br.com.chatia.model.User;
import br.com.chatia.repository.UserRepository;
import br.com.chatia.service.AuthenticationService;
import br.com.chatia.util.AsyncRunner;

import java.util.function.Consumer;

/** Recebe as ações da LoginView e repassa ao AuthenticationService. */
public class LoginController {

    public void login(String email, String password,
                      Consumer<User> onSuccess, Consumer<String> onError) {
        if (email == null || email.isBlank() || password == null || password.isEmpty()) {
            onError.accept("Preencha o e-mail e a senha para continuar.");
            return;
        }

        AsyncRunner.run(
                () -> new AuthenticationService(new UserRepository()).login(email, password),
                onSuccess,
                onError
        );
    }
}
