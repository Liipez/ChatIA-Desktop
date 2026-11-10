package br.com.chatia.service;

import br.com.chatia.model.Conversation;
import br.com.chatia.model.Message;
import br.com.chatia.model.User;
import br.com.chatia.repository.ConversationRepository;
import br.com.chatia.repository.MessageRepository;
import br.com.chatia.util.SessionManager;
import java.util.List;

public class ConversationService {
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationService() {
        this.conversationRepository = new ConversationRepository();
        this.messageRepository = new MessageRepository();
    }

    public Conversation createConversation(String title) {
        User user = requireLoggedUser();

        if (title == null || title.isBlank()) {
            title = "Nova Conversa";
        }

        Conversation conversation = new Conversation(
                user.getId(),
                title.trim()
        );

        return conversationRepository.save(conversation);
    }

    public List<Message> getMessages(String conversationId) {
        User user = requireLoggedUser();

        Conversation conversation =
                conversationRepository.findByIdAndUserId(
                        conversationId,
                        user.getId()
                );
        if (conversation == null) {
            throw new IllegalArgumentException(
                    "Conversa não encontrada para este usuário."
            );
        }
        messageRepository.assignOwnerToLegacyMessages(conversation.getId(), user.getId());

        return messageRepository.findByConversationIdAndUserId(
                conversation.getId(),
                user.getId()
        );
    }
    public List<Conversation> getUserConversations() {
        User user = requireLoggedUser();
        return conversationRepository.findByUserId(user.getId());
    }

    public Conversation renameConversation(String conversationId, String newTitle) {
        User user = requireLoggedUser();

        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException(
                    "O título da conversa não pode estar vazio."
            );
        }
        newTitle = newTitle.trim();

        if (newTitle.length() > 100) {
            throw new IllegalArgumentException(
                    "O título deve ter no máximo 100 caracteres."
            );
        }

        Conversation conversation =
                conversationRepository.findByIdAndUserId(
                        conversationId,
                        user.getId()
                );

        if (conversation == null) {
            throw new IllegalArgumentException(
                    "Conversa não encontrada para este usuário."
            );
        }

        conversationRepository.updateTitle(
                conversation.getId(),
                user.getId(),
                newTitle
        );

        conversation.setTitle(newTitle);

        return conversation;
    }

    public boolean deleteConversation(String conversationId) {
        User user = requireLoggedUser();

        Conversation conversation =
                conversationRepository.findByIdAndUserId(
                        conversationId,
                        user.getId()
                );

        if (conversation == null) {
            throw new IllegalArgumentException(
                    "Conversa não encontrada para este usuário."
            );
        }

        // Primeiro, exclui as mensagens da conversa.
        messageRepository.assignOwnerToLegacyMessages(conversation.getId(), user.getId());
        messageRepository.deleteByConversationIdAndUserId(conversation.getId(), user.getId());

        // Depois, exclui a conversa do usuário autenticado.
        return conversationRepository.deleteByIdAndUserId(
                conversation.getId(),
                user.getId()
        );
    }

    public Message saveMessage(
            String conversationId,
            String role,
            String content
    ) {
        User user = requireLoggedUser();

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "A mensagem não pode estar vazia."
            );
        }

        if (!"user".equals(role) && !"assistant".equals(role)) {
            throw new IllegalArgumentException(
                    "Tipo de mensagem inválido."
            );
        }

        Conversation conversation =
                conversationRepository.findByIdAndUserId(
                        conversationId,
                        user.getId()
                );
        if (conversation == null) {
            throw new IllegalArgumentException(
                    "Conversa não encontrada para este usuário"
            );
        }
        Message message = new Message(
                conversation.getId(),
                user.getId(),
                role,
                content.trim()
        );

        Message savedMessage = messageRepository.save(message);

        conversationRepository.updateTitle(
                conversation.getId(),
                user.getId(),
                conversation.getTitle()
        );
        return savedMessage;
    }

    private User requireLoggedUser(){
        User user = SessionManager.getCurrentUser();

        if (user == null || user.getId() == null){
            throw new IllegalArgumentException(
                    "É necessário estar autenticado para acessar as conversas"
            );
        }
        return user;
    }
    public String getPreviousConversationMemory(String currentConversationId) {
        User user = requireLoggedUser();

        List<Conversation> conversations =
                conversationRepository.findByUserId(user.getId());

        StringBuilder memory = new StringBuilder();
        int messagesAdded = 0;
        int maxMessages = 10;

        for (Conversation conversation : conversations) {
            if (conversation.getId().equals(currentConversationId)) {
                continue;
            }

            messageRepository.assignOwnerToLegacyMessages(conversation.getId(), user.getId());
            List<Message> messages =
                    messageRepository.findByConversationIdAndUserId(conversation.getId(), user.getId());

            for (Message message : messages) {
                if (!"user".equals(message.getRole())
                        && !"assistant".equals(message.getRole())) {
                    continue;
                }

                String content = message.getContent();

                if (content == null || content.isBlank()) {
                    continue;
                }

                if (messagesAdded >= maxMessages) {
                    return memory.toString();
                }

                if (content.length() > 500) {
                    content = content.substring(0, 500) + "...";
                }

                memory.append("[").append(message.getRole()).append("] ")
                        .append(content)
                        .append("\n");

                messagesAdded++;
            }
        }

        return memory.toString();
    }
}