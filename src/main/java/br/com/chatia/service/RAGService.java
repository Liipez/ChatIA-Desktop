package br.com.chatia.service;

import br.com.chatia.model.KnowledgeDocument;
import br.com.chatia.repository.KnowledgeRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        return rank(knowledgeRepository.findAll(), question);
    }

    /**
     * Ordena os documentos pela relevância em relação à pergunta.
     * Título e conteúdo passam pela MESMA normalização da pergunta
     * (minúsculas, sem acentos, sem pontuação), e a comparação é feita
     * palavra a palavra, não por trecho de texto.
     */
    public List<KnowledgeDocument> rank(List<KnowledgeDocument> documents, String question) {
        List<String> relevantWords = textProcessor.extractRelevantWords(question);

        if (relevantWords.isEmpty() || documents == null) {
            return new ArrayList<>();
        }

        Map<String, KnowledgeDocument> unicos = new LinkedHashMap<>();
        for (KnowledgeDocument document : documents) {
            String chave = textProcessor.normalize(document.getTitle() + " " + document.getContent());
            unicos.putIfAbsent(chave, document);
        }

        return unicos.values().stream()
                .map(document -> Map.entry(document, calculateRelevance(document, relevantWords)))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<KnowledgeDocument, Integer>comparingByValue().reversed())
                .limit(MAX_DOCUMENTOS)
                .map(Map.Entry::getKey)
                .toList();
    }

    /** Título vale 5 pontos, palavra-chave 3 e conteúdo 2. */
    private int calculateRelevance(KnowledgeDocument document, List<String> relevantWords) {
        Set<String> title = new HashSet<>(textProcessor.tokenize(document.getTitle()));
        Set<String> content = new HashSet<>(textProcessor.tokenize(document.getContent()));
        Set<String> keywords = new HashSet<>();
        if (document.getKeywords() != null) {
            for (String keyword : document.getKeywords()) {
                keywords.addAll(textProcessor.tokenize(keyword));
            }
        }

        int score = 0;
        for (String word : relevantWords) {
            if (textProcessor.matchesAny(word, title)) {
                score += 5;
            }
            if (textProcessor.matchesAny(word, keywords)) {
                score += 3;
            }
            if (textProcessor.matchesAny(word, content)) {
                score += 2;
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
