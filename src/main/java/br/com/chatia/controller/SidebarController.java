package br.com.chatia.controller;

import br.com.chatia.model.Conversation;
import br.com.chatia.model.Message;
import br.com.chatia.service.AuthenticationService;
import br.com.chatia.service.ConversationService;
import br.com.chatia.util.AsyncRunner;
import br.com.chatia.util.SessionManager;

import java.util.List;
import java.util.function.Consumer;

/**
 * Ações da sidebar: listar, abrir, renomear e excluir conversas, e logout.
 * A verificação de que a conversa pertence ao usuário é feita no
 * ConversationService/Repository, não aqui.
 */
public class SidebarController {

    private ConversationService conversationService;

    private synchronized ConversationService service() {
        if (conversationService == null) {
            conversationService = new ConversationService();
        }
        return conversationService;
    }

    public void loadConversations(Consumer<List<Conversation>> onSuccess, Consumer<String> onError) {
        AsyncRunner.run(() -> service().getUserConversations(), onSuccess, onError);
    }

    public void loadMessages(String conversationId,
                             Consumer<List<Message>> onSuccess, Consumer<String> onError) {
        AsyncRunner.run(() -> service().getMessages(conversationId), onSuccess, onError);
    }

    public void rename(String conversationId, String newTitle,
                       Consumer<Conversation> onSuccess, Consumer<String> onError) {
        AsyncRunner.run(() -> service().renameConversation(conversationId, newTitle),
                onSuccess, onError);
    }

    public void delete(String conversationId,
                       Consumer<Boolean> onSuccess, Consumer<String> onError) {
        AsyncRunner.run(() -> service().deleteConversation(conversationId), onSuccess, onError);
    }

    public void logout() {
        SessionManager.logout();
    }
}
