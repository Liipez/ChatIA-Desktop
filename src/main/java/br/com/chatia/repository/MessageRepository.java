
package br.com.chatia.repository;

import br.com.chatia.config.MongoConfig;
import br.com.chatia.model.Message;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageRepository {

    private final MongoCollection<Document> collection;

    public MessageRepository() {
        this.collection = MongoConfig.getDatabase()
                .getCollection("messages");

        this.collection.createIndex(
                new Document("conversationId", 1)
                        .append("createdAt", 1)
        );
        this.collection.createIndex(new Document("userId", 1));
    }

    public Message save(Message message) {
        Document doc = toDocument(message);

        collection.insertOne(doc);

        message.setId(doc.getObjectId("_id").toHexString());

        return message;
    }

    /** Mensagens de uma conversa, filtrando também pelo dono (defesa em profundidade). */
    public List<Message> findByConversationIdAndUserId(String conversationId, String userId) {
        List<Message> messages = new ArrayList<>();

        if (!ObjectId.isValid(conversationId) || userId == null) {
            return messages;
        }

        collection.find(
                Filters.and(
                        Filters.eq("conversationId", conversationId),
                        Filters.eq("userId", userId)
                )
        ).sort(
                Sorts.ascending("createdAt")
        ).forEach(doc -> messages.add(fromDocument(doc)));

        return messages;
    }

    /**
     * Migração: mensagens antigas foram salvas sem "userId".
     * Deve ser chamada SOMENTE depois de confirmar que a conversa pertence ao usuário.
     */
    public void assignOwnerToLegacyMessages(String conversationId, String userId) {
        collection.updateMany(
                Filters.and(
                        Filters.eq("conversationId", conversationId),
                        Filters.exists("userId", false)
                ),
                com.mongodb.client.model.Updates.set("userId", userId)
        );
    }

    private Document toDocument(Message message) {
        ZoneId zone = ZoneId.systemDefault();

        return new Document("conversationId", message.getConversationId())
                .append("userId", message.getUserId())
                .append("role", message.getRole())
                .append("content", message.getContent())
                .append("createdAt", Date.from(
                        message.getCreatedAt()
                                .atZone(zone).toInstant()
                ));
    }

    private Message fromDocument(Document doc) {
        ZoneId zone = ZoneId.systemDefault();

        Message message = new Message();

        message.setId(doc.getObjectId("_id").toHexString());
        message.setConversationId(doc.getString("conversationId"));
        message.setUserId(doc.getString("userId"));
        message.setRole(doc.getString("role"));
        message.setContent(doc.getString("content"));

        message.setCreatedAt(
                doc.getDate("createdAt").toInstant()
                        .atZone(zone).toLocalDateTime()
        );

        return message;
    }
    public long deleteByConversationIdAndUserId(String conversationId, String userId) {
        if (conversationId == null || !ObjectId.isValid(conversationId) || userId == null) {
            return 0;
        }

        return collection.deleteMany(
                Filters.and(
                        Filters.eq("conversationId", conversationId),
                        Filters.eq("userId", userId)
                )
        ).getDeletedCount();
    }
}
