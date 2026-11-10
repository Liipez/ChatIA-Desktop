package br.com.chatia.repository;

import br.com.chatia.config.MongoConfig;
import br.com.chatia.model.KnowledgeDocument;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.mongodb.client.model.Filters.eq;



public class KnowledgeRepository {

    private final MongoCollection<Document> collection;

    public KnowledgeRepository() {
        this.collection = MongoConfig.getDatabase()
                .getCollection("documents");

        collection.createIndex(Indexes.ascending("title"));
    }

    public void save(KnowledgeDocument knowledge) {

        if (knowledge.getId() == null) {

            String tituloNovo = knowledge.getTitle() == null
                    ? ""
                    : knowledge.getTitle().trim();

            String conteudoNovo = knowledge.getContent() == null
                    ? ""
                    : knowledge.getContent().trim();

            boolean duplicado = false;

            for (Document existente : collection.find()) {

                String tituloExistente = existente.getString("title");
                String conteudoExistente = existente.getString("content");

                tituloExistente = tituloExistente == null
                        ? ""
                        : tituloExistente.trim();

                conteudoExistente = conteudoExistente == null
                        ? ""
                        : conteudoExistente.trim();

                if (tituloNovo.equalsIgnoreCase(tituloExistente)
                        && conteudoNovo.equalsIgnoreCase(conteudoExistente)) {

                    knowledge.setId(
                            existente.getObjectId("_id").toHexString()
                    );

                    duplicado = true;
                    break;
                }
            }

            if (duplicado) {
                System.out.println("Documento já existe. Não foi salvo novamente.");
                return;
            }
        }

        Document document = new Document()
                .append("title", knowledge.getTitle())
                .append("content", knowledge.getContent())
                .append("keywords", knowledge.getKeywords())
                .append("createdAt", toDate(knowledge.getCreatedAt()))
                .append("updatedAt", toDate(knowledge.getUpdatedAt()));

        if (knowledge.getId() == null) {

            collection.insertOne(document);

            knowledge.setId(
                    document.getObjectId("_id").toHexString()
            );

        } else {

            ObjectId id = new ObjectId(knowledge.getId());

            document.put("_id", id);

            collection.replaceOne(
                    eq("_id", id),
                    document
            );
        }
    }

    public List<KnowledgeDocument> findAll() {

        List<KnowledgeDocument> documents = new ArrayList<>();

        for (Document document : collection.find()) {
            documents.add(toKnowledgeDocument(document));
        }

        return documents;
    }

    private KnowledgeDocument toKnowledgeDocument(Document document) {

        KnowledgeDocument knowledge = new KnowledgeDocument();

        ObjectId id = document.getObjectId("_id");

        if (id != null) {
            knowledge.setId(id.toHexString());
        }

        knowledge.setTitle(document.getString("title"));
        knowledge.setContent(document.getString("content"));

        List<String> keywords = document.getList(
                "keywords",
                String.class,
                new ArrayList<>()
        );

        knowledge.setKeywords(keywords);

        Date createdAt = document.getDate("createdAt");
        Date updatedAt = document.getDate("updatedAt");

        if (createdAt != null) {
            knowledge.setCreatedAt(
                    createdAt.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );
        }

        if (updatedAt != null) {
            knowledge.setUpdatedAt(
                    updatedAt.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
            );
        }

        return knowledge;
    }

    private Date toDate(LocalDateTime dateTime) {

        if (dateTime == null) {
            return null;
        }

        return Date.from(
                dateTime.atZone(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    public boolean deleteById(String id) {

        if (id == null || !ObjectId.isValid(id)) {
            return false;
        }

        ObjectId objectId = new ObjectId(id);

        return collection.deleteOne(
                eq("_id", objectId)
        ).getDeletedCount() > 0;
    }
}