package br.com.chatia.integration;

import br.com.chatia.model.User;
import br.com.chatia.repository.UserRepository;
import br.com.chatia.service.AuthenticationService;
import br.com.chatia.service.UserService;
import br.com.chatia.util.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static br.com.chatia.integration.IntegrationTestSupport.requireEnv;
import static br.com.chatia.integration.IntegrationTestSupport.uniqueEmail;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class AuthenticationIntegrationTest {

    private static final String SENHA = "senhaSegura123";

    private UserRepository repository;
    private UserService userService;
    private AuthenticationService auth;
    private User user;

    @BeforeEach
    void setUp() {
        requireEnv("MONGODB_URI");
        repository = new UserRepository();
        userService = new UserService(repository);
        auth = new AuthenticationService(repository);
        user = userService.register("Usuário Teste", uniqueEmail("auth"), SENHA, SENHA);
    }

    @AfterEach
    void tearDown() {
        SessionManager.logout();
        if (user != null) {
            repository.deleteById(user.getId());
        }
    }

    @Test
    void cadastroValidoSalvaSenhaComHash() {
        User salvo = repository.findById(user.getId());
        assertNotNull(salvo);
        assertNotEquals(SENHA, salvo.getPasswordHash());
        assertTrue(salvo.getPasswordHash().startsWith("$2a$"), "senha deve estar em BCrypt");
    }

    @Test
    void cadastroDuplicadoEhRecusado() {
        assertThrows(IllegalArgumentException.class,
                () -> userService.register("Outro", user.getEmail(), SENHA, SENHA));
    }

    @Test
    void loginValidoAbreSessao() {
        auth.login(user.getEmail().toUpperCase(), SENHA);
        assertTrue(SessionManager.isLoggedIn());
        assertEquals(user.getId(), SessionManager.getCurrentUser().getId());
    }

    @Test
    void senhaIncorretaEhRecusada() {
        assertThrows(IllegalArgumentException.class, () -> auth.login(user.getEmail(), "senhaErrada"));
        assertFalse(SessionManager.isLoggedIn());
    }

    @Test
    void usuarioInexistenteEhRecusadoComAMesmaMensagem() {
        IllegalArgumentException inexistente = assertThrows(IllegalArgumentException.class,
                () -> auth.login(uniqueEmail("naoexiste"), SENHA));
        IllegalArgumentException senhaErrada = assertThrows(IllegalArgumentException.class,
                () -> auth.login(user.getEmail(), "senhaErrada"));
        assertEquals(senhaErrada.getMessage(), inexistente.getMessage());
    }

    @Test
    void logoutEncerraSessao() {
        auth.login(user.getEmail(), SENHA);
        auth.logout();
        assertFalse(SessionManager.isLoggedIn());
    }
}
