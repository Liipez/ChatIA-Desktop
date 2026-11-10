package br.com.chatia.integration;

import br.com.chatia.model.Conversation;
import br.com.chatia.model.Message;
import br.com.chatia.model.User;
import br.com.chatia.repository.UserRepository;
import br.com.chatia.service.ConversationService;
import br.com.chatia.service.UserService;
import br.com.chatia.util.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.chatia.integration.IntegrationTestSupport.requireEnv;
import static br.com.chatia.integration.IntegrationTestSupport.uniqueEmail;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprova que um usuário não consegue ver, renomear, ler ou excluir
 * conversas de outro usuário, mesmo conhecendo o ID da conversa.
 */
@Tag("integration")
class ConversationIsolationIntegrationTest {

    private UserRepository userRepository;
    private ConversationService conversations;
    private User usuario1;
    private User usuario2;
    private Conversation conversaDoUsuario1;

    @BeforeEach
    void setUp() {
        requireEnv("MONGODB_URI");
        userRepository = new UserRepository();
        UserService userService = new UserService(userRepository);
        conversations = new ConversationService();

        usuario1 = userService.register("Usuario 1", uniqueEmail("usuario1"), "senha12345", "senha12345");
        usuario2 = userService.register("Usuario 2", uniqueEmail("usuario2"), "senha12345", "senha12345");

        SessionManager.login(usuario1);
        conversaDoUsuario1 = conversations.createConversation("Java");
        conversations.saveMessage(conversaDoUsuario1.getId(), "user", "O que é encapsulamento?");
    }

    @AfterEach
    void tearDown() {
        if (usuario1 == null || conversaDoUsuario1 == null) {
            return; // teste ignorado (sem MONGODB_URI)
        }
        SessionManager.login(usuario1);
        try {
            conversations.deleteConversation(conversaDoUsuario1.getId());
        } catch (IllegalArgumentException ignored) {
            // já excluída pelo teste
        }
        SessionManager.logout();
        userRepository.deleteById(usuario1.getId());
        if (usuario2 != null) {
            userRepository.deleteById(usuario2.getId());
        }
    }

    @Test
    void cadaUsuarioVeApenasSuasConversas() {
        SessionManager.login(usuario2);
        Conversation conversaDoUsuario2 = conversations.createConversation("Angular");

        List<Conversation> doUsuario2 = conversations.getUserConversations();
        assertEquals(1, doUsuario2.size());
        assertEquals("Angular", doUsuario2.get(0).getTitle());

        SessionManager.login(usuario1);
        List<Conversation> doUsuario1 = conversations.getUserConversations();
        assertEquals(1, doUsuario1.size());
        assertEquals("Java", doUsuario1.get(0).getTitle());

        SessionManager.login(usuario2);
        conversations.deleteConversation(conversaDoUsuario2.getId());
    }

    @Test
    void outroUsuarioNaoLeMensagens() {
        SessionManager.login(usuario2);
        assertThrows(IllegalArgumentException.class,
                () -> conversations.getMessages(conversaDoUsuario1.getId()));
    }

    @Test
    void outroUsuarioNaoRenomeia() {
        SessionManager.login(usuario2);
        assertThrows(IllegalArgumentException.class,
                () -> conversations.renameConversation(conversaDoUsuario1.getId(), "Invadido"));

        SessionManager.login(usuario1);
        assertEquals("Java", conversations.getUserConversations().get(0).getTitle());
    }

    @Test
    void outroUsuarioNaoExclui() {
        SessionManager.login(usuario2);
        assertThrows(IllegalArgumentException.class,
                () -> conversations.deleteConversation(conversaDoUsuario1.getId()));

        SessionManager.login(usuario1);
        assertEquals(1, conversations.getUserConversations().size());
    }

    @Test
    void outroUsuarioNaoEnviaMensagemNaConversaAlheia() {
        SessionManager.login(usuario2);
        assertThrows(IllegalArgumentException.class,
                () -> conversations.saveMessage(conversaDoUsuario1.getId(), "user", "invasão"));
    }

    @Test
    void historicoPersisteComDonoDaMensagem() {
        List<Message> mensagens = conversations.getMessages(conversaDoUsuario1.getId());
        assertEquals(1, mensagens.size());
        assertEquals(usuario1.getId(), mensagens.get(0).getUserId());
    }

    @Test
    void renomearPersisteNoBanco() {
        conversations.renameConversation(conversaDoUsuario1.getId(), "Estudando JavaFX");
        assertEquals("Estudando JavaFX", conversations.getUserConversations().get(0).getTitle());
    }
}
