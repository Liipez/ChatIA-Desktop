package br.com.chatia.service;

import br.com.chatia.model.KnowledgeDocument;
import br.com.chatia.repository.KnowledgeRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recuperação de informações (RAG) baseada em palavras-chave.
 *
 * Pergunta -> normalização -> tokenização -> remoção de stop words
 * -> palavras relevantes -> busca na coleção "documents"
 * -> ranking por relevância -> contexto enviado à Groq.
 */
public class RAGService {

    private static final int MAX_DOCUMENTOS = 5;

    private final KnowledgeRepository knowledgeRepository;
    private final TextProcessor textProcessor;

    public RAGService() {
        this(new KnowledgeRepository(), new TextProcessor());
    }

    public RAGService(KnowledgeRepository knowledgeRepository, TextProcessor textProcessor) {
        this.knowledgeRepository = knowledgeRepository;
        this.textProcessor = textProcessor;
    }

    public List<KnowledgeDocument> searchRelevantDocuments(String question) {
        List<String> relevantWords = textProcessor.extractRelevantWords(question);

        if (relevantWords.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, KnowledgeDocument> foundDocuments = new LinkedHashMap<>();

        for (String word : relevantWords) {
            for (KnowledgeDocument document : knowledgeRepository.searchByKeyword(word)) {
                String chave = textProcessor.normalize(
                        document.getTitle() + " " + document.getContent());

                boolean jaExiste = foundDocuments.values().stream()
                        .anyMatch(existente -> textProcessor.normalize(
                                existente.getTitle() + " " + existente.getContent()
                        ).equals(chave));

                if (!jaExiste) {
                    foundDocuments.put(document.getId(), document);
                }
            }
        }

        return foundDocuments.values().stream()
                .map(document -> Map.entry(document, calculateRelevance(document, relevantWords)))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<KnowledgeDocument, Integer>comparingByValue().reversed())
                .limit(MAX_DOCUMENTOS)
                .map(Map.Entry::getKey)
                .toList();
    }

    /** Título vale 5 pontos, palavra-chave 3 e conteúdo 2. */
    private int calculateRelevance(KnowledgeDocument document, List<String> relevantWords) {
        int score = 0;

        String title = textProcessor.normalize(document.getTitle());
        String content = textProcessor.normalize(document.getContent());
        List<String> keywords = document.getKeywords() == null
                ? List.of()
                : document.getKeywords();

        for (String word : relevantWords) {
            if (title.contains(word)) {
                score += 5;
            }
            if (content.contains(word)) {
                score += 2;
            }
            for (String keyword : keywords) {
                if (keyword != null && textProcessor.normalize(keyword).contains(word)) {
                    score += 3;
                    break;
                }
            }
        }

        return score;
    }

    public String buildContext(String question) {
        List<KnowledgeDocument> documents = searchRelevantDocuments(question);

        if (documents.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();

        for (KnowledgeDocument document : documents) {
            context.append("Título: ").append(document.getTitle()).append("\n");
            context.append("Conteúdo: ").append(document.getContent()).append("\n\n");
        }

        return context.toString().trim();
    }
}
