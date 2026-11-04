package br.com.chatia.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

public class MongoConfig {

    private static final String NOME_BANCO_PADRAO = "chatia";

    private static MongoClient mongoClient;

    private MongoConfig() {
    }

    public static synchronized MongoDatabase getDatabase() {
        if (mongoClient == null) {
            String uri = System.getenv("MONGODB_URI");

            if (uri == null || uri.isBlank()) {
                throw new IllegalStateException(
                        "A variável MONGODB_URI não foi configurada."
                );
            }

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(5, TimeUnit.SECONDS))
                    .build();

            mongoClient = MongoClients.create(settings);
        }

        String nomeBanco = System.getenv("MONGODB_DATABASE");
        if (nomeBanco == null || nomeBanco.isBlank()) {
            nomeBanco = NOME_BANCO_PADRAO;
        }

        return mongoClient.getDatabase(nomeBanco);
    }

    public static boolean testarConexao() {
        try {
            getDatabase().runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static synchronized void closeConnection() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}