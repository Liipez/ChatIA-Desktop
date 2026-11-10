package br.com.chatia.controller;

import br.com.chatia.model.Conversation;
import br.com.chatia.service.ChatService;
import br.com.chatia.service.ConversationService;
import br.com.chatia.util.AsyncRunner;

import java.util.function.Consumer;

/**
 * Recebe a mensagem digitada na ChatView e repassa ao ChatService.
 * View -> ChatController -> ChatService -> RAGService -> GroqService -> Groq API
 */
public class ChatController {

    private ChatService chatService;
    private boolean sending = false;

    private synchronized ChatService service() {
        if (chatService == null) {
            chatService = new ChatService(new ConversationService());
        }
        return chatService;
    }

    public boolean isSending() {
        return sending;
    }

    /**
     * Envia a pergunta em segundo plano. Enquanto a IA responde,
     * novos envios são ignorados (evita perguntas duplicadas).
     */
    public void send(Conversation conversation, String text,
                     Consumer<ChatService.ChatResult> onSuccess, Consumer<String> onError) {
        if (sending) {
            return;
        }
        if (text == null || text.isBlank()) {
            onError.accept("Digite uma mensagem antes de enviar.");
            return;
        }

        sending = true;
        AsyncRunner.run(
                () -> service().send(conversation, text),
                result -> {
                    sending = false;
                    onSuccess.accept(result);
                },
                error -> {
                    sending = false;
                    onError.accept(error);
                }
        );
    }
}
