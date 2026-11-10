package br.com.chatia.controller;

import br.com.chatia.model.KnowledgeDocument;
import br.com.chatia.service.KnowledgeService;

import java.util.List;

/** Recebe as ações da KnowledgeView e repassa ao KnowledgeService. */
public class KnowledgeController {

    private KnowledgeService knowledgeService;

    private KnowledgeService service() {
        if (knowledgeService == null) {
            knowledgeService = new KnowledgeService();
        }
        return knowledgeService;
    }

    public void save(String title, String content, List<String> keywords) {
        service().saveDocument(title, content, keywords);
    }

    public List<KnowledgeDocument> findAll() {
        return service().getAllDocuments();
    }

    public boolean delete(String id) {
        return service().deleteDocument(id);
    }
}
