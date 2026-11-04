package br.com.chatia.service;

import br.com.chatia.model.KnowledgeDocument;
import br.com.chatia.repository.KnowledgeRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KnowledgeService {

    private final KnowledgeRepository knowledgeRepository;
    private final TextProcessor textProcessor;

    public KnowledgeService() {
        this.knowledgeRepository = new KnowledgeRepository();
        this.textProcessor = new TextProcessor();
    }

    public List<KnowledgeDocument> searchRelevantDocuments(String question) {
        List<String> relevantWords =
                textProcessor.extractRelevantWords(question);

        if (relevantWords.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, KnowledgeDocument> foundDocuments = new LinkedHashMap<>();

        for (String word : relevantWords) {
            List<KnowledgeDocument> results =
                    knowledgeRepository.searchByKeyword(word);

            for (KnowledgeDocument document : results) {
                String chave = textProcessor.normalize(
                        document.getTitle() + " " + document.getContent()
                );

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
                .map(document -> Map.entry(
                        document,
                        calculateRelevance(document, relevantWords)
                ))
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<KnowledgeDocument, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    private int calculateRelevance(
            KnowledgeDocument document,
            List<String> relevantWords
    ) {
        int score = 0;

        String title = document.getTitle() == null
                ? ""
                : textProcessor.normalize(document.getTitle());

        String content = document.getContent() == null
                ? ""
                : textProcessor.normalize(document.getContent());

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
                if (keyword != null
                        && textProcessor.normalize(keyword).contains(word)) {
                    score += 3;
                    break;
                }
            }
        }

        return score;
    }

    public String buildContext(String question) {
        List<KnowledgeDocument> documents =
                searchRelevantDocuments(question);

        if (documents.isEmpty()) {
            return "";
        }

        StringBuilder context = new StringBuilder();

        for (KnowledgeDocument document : documents) {
            context.append("Título: ")
                    .append(document.getTitle())
                    .append("\n");

            context.append("Conteúdo: ")
                    .append(document.getContent())
                    .append("\n\n");
        }

        return context.toString().trim();
    }

    public void saveDocument(
            String title,
            String content,
            List<String> keywords
    ) {
        KnowledgeDocument document =
                new KnowledgeDocument(title, content);

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
