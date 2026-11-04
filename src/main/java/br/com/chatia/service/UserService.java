package br.com.chatia.service;

import br.com.chatia.model.User;
import br.com.chatia.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {

    private static final int TAMANHO_MINIMO_SENHA = 8;
    private static final String REGEX_EMAIL = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String name, String email, String password, String confirmPassword) {
        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isEmpty()
                || confirmPassword == null || confirmPassword.isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos.");
        }

        String emailNormalizado = email.trim().toLowerCase();

        if (!emailNormalizado.matches(REGEX_EMAIL)) {
            throw new IllegalArgumentException("Informe um e-mail válido.");
        }
        if (password.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("As senhas não coincidem.");
        }
        if (userRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }

        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        User user = new User(name.trim(), emailNormalizado, hash);
        return userRepository.save(user);
    }
}
