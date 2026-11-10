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
            String uri = AppConfig.get("MONGODB_URI");

            if (uri == null) {
                throw new IllegalStateException(
                        "O banco de dados não está configurado. Defina MONGODB_URI "
                                + "(variável de ambiente ou " + AppConfig.configFile() + ")."
                );
            }

            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(uri))
                    .applyToClusterSettings(builder ->
                            builder.serverSelectionTimeout(5, TimeUnit.SECONDS))
                    .build();

            mongoClient = MongoClients.create(settings);
        }

        String nomeBanco = AppConfig.get("MONGODB_DATABASE", NOME_BANCO_PADRAO);

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