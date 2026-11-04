package br.com.chatia.repository;

import br.com.chatia.config.MongoConfig;
import br.com.chatia.model.Conversation;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationRepository {

    private final MongoCollection<Document> collection;

    public ConversationRepository() {
        this.collection = MongoConfig.getDatabase()
                .getCollection("conversations");

        // Índice para localizar as conversas de cada usuário.
        this.collection.createIndex(
                new Document("userId", 1)
                        .append("updatedAt", -1)
        );
    }

    public Conversation save(Conversation conversation) {
        Document doc = toDocument(conversation);

        collection.insertOne(doc);

        conversation.setId(doc.getObjectId("_id").toHexString());

        return conversation;
    }

    public List<Conversation> findByUserId(String userId) {
        List<Conversation> conversations = new ArrayList<>();

        collection.find(Filters.eq("userId", userId))
                .sort(Sorts.descending("updatedAt"))
                .forEach(doc -> conversations.add(fromDocument(doc)));

        return conversations;
    }

    public Conversation findByIdAndUserId(
            String conversationId,
            String userId
    ) {
        if (!ObjectId.isValid(conversationId)) {
            return null;
        }

        Document doc = collection.find(
                Filters.and(
                        Filters.eq("_id", new ObjectId(conversationId)),
                        Filters.eq("userId", userId)
                )
        ).first();

        return doc == null ? null : fromDocument(doc);
    }

    public boolean updateTitle(
            String conversationId,
            String userId,
            String newTitle
    ) {
        if (!ObjectId.isValid(conversationId)) {
            return false;
        }

        return collection.updateOne(
                Filters.and(
                        Filters.eq("_id", new ObjectId(conversationId)),
                        Filters.eq("userId", userId)
                ),
                Updates.combine(
                        Updates.set("title", newTitle),
                        Updates.set("updatedAt", new Date())
                )
        ).getMatchedCount() > 0;
    }

    public boolean deleteByIdAndUserId(
            String conversationId,
            String userId
    ) {
        if (!ObjectId.isValid(conversationId)) {
            return false;
        }

        return collection.deleteOne(
                Filters.and(
                        Filters.eq("_id", new ObjectId(conversationId)),
                        Filters.eq("userId", userId)
                )
        ).getDeletedCount() > 0;
    }

    private Document toDocument(Conversation conversation) {
        ZoneId zone = ZoneId.systemDefault();

        return new Document("userId", conversation.getUserId())
                .append("title", conversation.getTitle())
                .append("createdAt", Date.from(
                        conversation.getCreatedAt()
                                .atZone(zone).toInstant()
                ))
                .append("updatedAt", Date.from(
                        conversation.getUpdatedAt()
                                .atZone(zone).toInstant()
                ));
    }

    private Conversation fromDocument(Document doc) {
        ZoneId zone = ZoneId.systemDefault();

        Conversation conversation = new Conversation();

        conversation.setId(doc.getObjectId("_id").toHexString());
        conversation.setUserId(doc.getString("userId"));
        conversation.setTitle(doc.getString("title"));

        conversation.setCreatedAt(
                doc.getDate("createdAt").toInstant()
                        .atZone(zone).toLocalDateTime()
        );

        conversation.setUpdatedAt(
                doc.getDate("updatedAt").toInstant()
                        .atZone(zone).toLocalDateTime()
        );

        return conversation;
    }
}

