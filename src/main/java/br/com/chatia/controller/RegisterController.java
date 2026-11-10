package br.com.chatia.controller;

import br.com.chatia.model.User;
import br.com.chatia.repository.UserRepository;
import br.com.chatia.service.UserService;
import br.com.chatia.util.AsyncRunner;

import java.util.function.Consumer;

/** Recebe as ações da RegisterView e repassa ao UserService. */
public class RegisterController {

    public void register(String name, String email, String password, String confirmPassword,
                         Consumer<User> onSuccess, Consumer<String> onError) {
        AsyncRunner.run(
                () -> new UserService(new UserRepository())
                        .register(name, email, password, confirmPassword),
                onSuccess,
                onError
        );
    }
}
