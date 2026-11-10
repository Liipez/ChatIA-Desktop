package br.com.chatia.repository;

import br.com.chatia.config.MongoConfig;
import br.com.chatia.model.PasswordResetToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PasswordResetRepository {

    private final MongoCollection<Document> collection;

    public PasswordResetRepository() {
        this.collection = MongoConfig.getDatabase()
                .getCollection("password_reset_tokens");

        collection.createIndex(Indexes.ascending("userId"));

        // TTL: o próprio MongoDB apaga o token depois que ele expira.
        collection.createIndex(
                Indexes.ascending("expiresAt"),
                new IndexOptions().expireAfter(0L, TimeUnit.SECONDS)
        );
    }

    public PasswordResetToken save(PasswordResetToken token) {
        Document doc = new Document("userId", token.getUserId())
                .append("tokenHash", token.getTokenHash())
                .append("createdAt", toDate(token.getCreatedAt()))
                .append("expiresAt", toDate(token.getExpiresAt()))
                .append("attempts", token.getAttempts())
                .append("used", token.isUsed());

        collection.insertOne(doc);
        token.setId(doc.getObjectId("_id").toHexString());
        return token;
    }

    /** Último token ainda não utilizado do usuário. */
    public PasswordResetToken findLatestActiveByUserId(String userId) {
        Document doc = collection.find(
                Filters.and(
                        Filters.eq("userId", userId),
                        Filters.eq("used", false)
                )
        ).sort(Sorts.descending("createdAt")).first();

        return doc == null ? null : fromDocument(doc);
    }

    public void incrementAttempts(String tokenId) {
        collection.updateOne(
                Filters.eq("_id", new ObjectId(tokenId)),
                Updates.inc("attempts", 1)
        );
    }

    public void markAsUsed(String tokenId) {
        collection.updateOne(
                Filters.eq("_id", new ObjectId(tokenId)),
                Updates.set("used", true)
        );
    }

    /** Invalida todos os tokens pendentes (ao gerar um novo ou após redefinir). */
    public void invalidateAllByUserId(String userId) {
        collection.updateMany(
                Filters.and(
                        Filters.eq("userId", userId),
                        Filters.eq("used", false)
                ),
                Updates.set("used", true)
        );
    }

    private PasswordResetToken fromDocument(Document doc) {
        PasswordResetToken token = new PasswordResetToken();
        token.setId(doc.getObjectId("_id").toHexString());
        token.setUserId(doc.getString("userId"));
        token.setTokenHash(doc.getString("tokenHash"));
        token.setCreatedAt(toLocal(doc.getDate("createdAt")));
        token.setExpiresAt(toLocal(doc.getDate("expiresAt")));
        token.setAttempts(doc.getInteger("attempts", 0));
        token.setUsed(doc.getBoolean("used", false));
        return token;
    }

    private Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocal(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
