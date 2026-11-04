
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
    }

    public Message save(Message message) {
        Document doc = toDocument(message);

        collection.insertOne(doc);

        message.setId(doc.getObjectId("_id").toHexString());

        return message;
    }

    public List<Message> findByConversationId(String conversationId) {
        List<Message> messages = new ArrayList<>();

        if (!ObjectId.isValid(conversationId)) {
            return messages;
        }

        collection.find(
                Filters.eq("conversationId", conversationId)
        ).sort(
                Sorts.ascending("createdAt")
        ).forEach(doc -> messages.add(fromDocument(doc)));

        return messages;
    }

    public long countByConversationId(String conversationId) {
        if (!ObjectId.isValid(conversationId)) {
            return 0;
        }

        return collection.countDocuments(
                Filters.eq("conversationId", conversationId)
        );
    }

    private Document toDocument(Message message) {
        ZoneId zone = ZoneId.systemDefault();

        return new Document("conversationId", message.getConversationId())
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
        message.setRole(doc.getString("role"));
        message.setContent(doc.getString("content"));

        message.setCreatedAt(
                doc.getDate("createdAt").toInstant()
                        .atZone(zone).toLocalDateTime()
        );

        return message;
    }
    public long deleteByConversationId(String conversationId) {
        if (conversationId == null || !ObjectId.isValid(conversationId)) {
            return 0;
        }

        return collection.deleteMany(
                Filters.eq("conversationId", conversationId)
        ).getDeletedCount();
    }
}
