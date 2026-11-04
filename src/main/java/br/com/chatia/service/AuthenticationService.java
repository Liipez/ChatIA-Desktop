package br.com.chatia.service;

import br.com.chatia.model.User;
import br.com.chatia.repository.UserRepository;
import br.com.chatia.util.SessionManager;
import org.mindrot.jbcrypt.BCrypt;

public class AuthenticationService {

    private static final String MENSAGEM_INVALIDO = "E-mail ou senha inválidos.";

    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Informe o e-mail e a senha.");
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase());

        // Mesma mensagem para usuário inexistente e senha errada,
        // para não revelar quais e-mails estão cadastrados.
        if (user == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException(MENSAGEM_INVALIDO);
        }

        SessionManager.login(user);
        return user;
    }

    public void logout() {
        SessionManager.logout();
    }
}
