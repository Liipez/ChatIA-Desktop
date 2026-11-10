package br.com.chatia.service;

import br.com.chatia.model.KnowledgeDocument;
import br.com.chatia.repository.KnowledgeRepository;

import java.util.List;

/**
 * Cadastro da base de conhecimento (CRUD da coleção "documents").
 * A busca usada pelo chat fica no RAGService.
 */
public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;

    public KnowledgeService() {
        this.knowledgeRepository = new KnowledgeRepository();
    }

    public void saveDocument(String title, String content, List<String> keywords) {
        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            throw new IllegalArgumentException("Informe o título e o conteúdo do documento.");
        }

        KnowledgeDocument document = new KnowledgeDocument(title.trim(), content.trim());
        document.setKeywords(keywords);

        knowledgeRepository.save(document);
    }

    public List<KnowledgeDocument> getAllDocuments() {
        return knowledgeRepository.findAll();
    }

    public boolean deleteDocument(String id) {
        return knowledgeRepository.deleteById(id);
    }
}
