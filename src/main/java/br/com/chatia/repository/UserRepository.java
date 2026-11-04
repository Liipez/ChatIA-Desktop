package br.com.chatia.repository;

import br.com.chatia.config.MongoConfig;
import br.com.chatia.model.User;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.ZoneId;
import java.util.Date;

public class UserRepository {

    private final MongoCollection<Document> collection;

    public UserRepository() {
        this.collection = MongoConfig.getDatabase().getCollection("users");
        // Garante no banco que não existem dois usuários com o mesmo e-mail
        this.collection.createIndex(
                Indexes.ascending("email"),
                new IndexOptions().unique(true)
        );
    }

    public long countUsers() {
        return collection.countDocuments();
    }

    public User save(User user) {
        Document doc = toDocument(user);
        try {
            collection.insertOne(doc);
        } catch (MongoWriteException e) {
            if (e.getError().getCategory() == ErrorCategory.DUPLICATE_KEY) {
                throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
            }
            throw e;
        }
        user.setId(doc.getObjectId("_id").toHexString());
        return user;
    }

    public User findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return doc == null ? null : fromDocument(doc);
    }

    public boolean existsByEmail(String email) {
        return collection.countDocuments(Filters.eq("email", email)) > 0;
    }

    public User findById(String id) {
        if (!ObjectId.isValid(id)) {
            return null;
        }
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return doc == null ? null : fromDocument(doc);
    }

    public boolean updatePasswordHash(String userId, String newHash) {
        if (!ObjectId.isValid(userId)) {
            return false;
        }
        return collection.updateOne(
                Filters.eq("_id", new ObjectId(userId)),
                Updates.set("passwordHash", newHash)
        ).getModifiedCount() > 0;
    }

    private Document toDocument(User user) {
        return new Document("name", user.getName())
                .append("email", user.getEmail())
                .append("passwordHash", user.getPasswordHash())
                .append("createdAt", Date.from(
                        user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant()));
    }

    private User fromDocument(Document doc) {
        User user = new User();
        user.setId(doc.getObjectId("_id").toHexString());
        user.setName(doc.getString("name"));
        user.setEmail(doc.getString("email"));
        user.setPasswordHash(doc.getString("passwordHash"));
        user.setCreatedAt(doc.getDate("createdAt").toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
        return user;
    }
}