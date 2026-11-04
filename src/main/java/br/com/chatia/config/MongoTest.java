package br.com.chatia.config;

import org.bson.Document;

public class MongoTest {

    public static void main(String[] args) {
        try {
            MongoConfig.getDatabase().runCommand(new Document("ping", 1));
            System.out.println("Conectado ao MongoDB Atlas!");
        } catch (Exception e) {
            System.out.println("Não foi possível conectar ao MongoDB.");
            System.out.println("Erro: " + e.getMessage());
        } finally {
            MongoConfig.closeConnection();
        }
    }
}