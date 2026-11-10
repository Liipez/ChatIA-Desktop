package br.com.chatia.service;

import br.com.chatia.model.Conversation;
import br.com.chatia.model.Message;

import java.util.List;

/**
 * Regra de negócio do envio de mensagens:
 * ChatController -> ChatService -> RAGService -> GroqService -> Groq API
 */
public class ChatService {

    private static final int TAMANHO_TITULO_AUTOMATICO = 35;

    private final ConversationService conversationService;
    private final RAGService ragService;
    private final GroqService groqService;

    public ChatService(ConversationService conversationService) {
        this(conversationService, new RAGService(), new GroqService());
    }

    public ChatService(ConversationService conversationService,
                       RAGService ragService,
                       GroqService groqService) {
        this.conversationService = conversationService;
        this.ragService = ragService;
        this.groqService = groqService;
    }

    /** Resultado do envio: a conversa (pode ter sido criada agora) e a resposta da IA. */
    public record ChatResult(Conversation conversation, Message answer, boolean newConversation) {
    }

    /**
     * Envia a pergunta do usuário.
     * Se não houver conversa aberta, cria uma com o início da pergunta como título.
     */
    public ChatResult send(Conversation conversation, String question) throws Exception {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("Digite uma mensagem antes de enviar.");
        }
        question = question.trim();

        // Valida a chave antes de salvar qualquer coisa.
        if (!br.com.chatia.config.GroqConfig.isConfigured()) {
            throw new IllegalStateException(
                    "A chave da IA não está configurada. Defina a variável GROQ_API_KEY.");
        }

        boolean nova = conversation == null;
        if (nova) {
            String titulo = question.length() > TAMANHO_TITULO_AUTOMATICO
                    ? question.substring(0, TAMANHO_TITULO_AUTOMATICO) + "..."
                    : question;
            conversation = conversationService.createConversation(titulo);
        }

        // Histórico ANTES de salvar a pergunta atual (a pergunta vai separada).
        List<GroqService.MessageHistory> historico = conversationService
                .getMessages(conversation.getId())
                .stream()
                .map(m -> new GroqService.MessageHistory(m.getRole(), m.getContent()))
                .toList();

        conversationService.saveMessage(conversation.getId(), "user", question);

        // RAG: contexto da base de conhecimento.
        String context = ragService.buildContext(question);

        // Memória de outras conversas do mesmo usuário.
        String memoria = conversationService.getPreviousConversationMemory(conversation.getId());
        if (!memoria.isBlank()) {
            context += "\n\nMEMÓRIA DE OUTRAS CONVERSAS DO USUÁRIO:\n" + memoria;
        }

        String resposta = groqService.chat(question, context, historico);

        Message answer = conversationService.saveMessage(
                conversation.getId(), "assistant", resposta);

        return new ChatResult(conversation, answer, nova);
    }
}
