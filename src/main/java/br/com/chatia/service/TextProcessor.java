package br.com.chatia.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TextProcessor {
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "o", "as", "os",
            "de", "da", "do", "das", "dos",
            "em", "para", "por", "com",
            "uma", "um", "uns", "umas",
            "que", "e", "é", "ao", "aos",
            "na", "no", "nas", "nos",
            "se", "como", "mais", "mas",
            "ou", "ser", "foi", "sao"
    );

    public String normalize(String text) {
        if (text == null) {
            return "";
        }

        String normalized = Normalizer.normalize(
                text.toLowerCase().trim(),
                Normalizer.Form.NFD
        );

        return normalized
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public List<String> tokenize(String text) {
        String normalized = normalize(text);

        if (normalized.isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.asList(normalized.split("\\s+"));
    }

    public List<String> removeStopWords(String text) {
        return tokenize(text)
                .stream()
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toList());
    }

    public List<String> extractRelevantWords(String text) {
        return removeStopWords(text)
                .stream()
                .filter(word -> word.length() > 1)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Verifica se a palavra da pergunta corresponde a alguma palavra do documento.
     * Igualdade exata, ou variação da mesma palavra quando ambas têm 5+ letras
     * (ex.: "documento" e "documentos"). Assim "java" NÃO corresponde a "javascript".
     */
    public boolean matchesAny(String word, Set<String> tokens) {
        if (tokens.contains(word)) {
            return true;
        }
        if (word.length() < 5) {
            return false;
        }
        for (String token : tokens) {
            if (token.length() >= 5 && (token.startsWith(word) || word.startsWith(token))
                    && Math.abs(token.length() - word.length()) <= 2) {
                return true;
            }
        }
        return false;
    }
}
