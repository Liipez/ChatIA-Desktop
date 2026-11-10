package br.com.chatia.service;

import br.com.chatia.model.KnowledgeDocument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Testes do RAG sem banco de dados (os documentos são criados em memória). */
class RAGServiceTest {

    private final TextProcessor textProcessor = new TextProcessor();
    private final RAGService rag = new RAGService(null, textProcessor);

    private KnowledgeDocument doc(String title, String content, String... keywords) {
        KnowledgeDocument d = new KnowledgeDocument(title, content);
        d.setKeywords(List.of(keywords));
        return d;
    }

    private final List<KnowledgeDocument> base = List.of(
            doc("Java", "Java é uma linguagem de programação orientada a objetos.", "programação", "backend"),
            doc("JavaScript", "Linguagem usada em páginas web.", "frontend"),
            doc("MongoDB", "Banco de dados NoSQL orientado a documentos.", "banco de dados", "nosql")
    );

    @Test
    void removeStopWords() {
        assertEquals(List.of("mongodb", "armazena", "dados"),
                textProcessor.extractRelevantWords("O que é o MongoDB e como armazena os dados?"));
    }

    @Test
    void encontraPalavraComAcento() {
        // Antes da correção, "programação" não encontrava nada.
        List<KnowledgeDocument> r = rag.rank(base, "Me fale sobre programação");
        assertFalse(r.isEmpty());
        assertEquals("Java", r.get(0).getTitle());
    }

    @Test
    void encontraSemAcentoNaPergunta() {
        List<KnowledgeDocument> r = rag.rank(base, "programacao orientada a objetos");
        assertEquals("Java", r.get(0).getTitle());
    }

    @Test
    void javaNaoTrazJavaScript() {
        List<KnowledgeDocument> r = rag.rank(base, "O que é Java?");
        assertEquals(1, r.size());
        assertEquals("Java", r.get(0).getTitle());
    }

    @Test
    void reconhecePlural() {
        List<KnowledgeDocument> r = rag.rank(base, "Como funciona um banco de documento?");
        assertEquals("MongoDB", r.get(0).getTitle());
    }

    @Test
    void palavraInexistenteNaoRetornaNada() {
        assertTrue(rag.rank(base, "Explique Kubernetes").isEmpty());
    }

    @Test
    void perguntaSoComStopWordsNaoRetornaNada() {
        assertTrue(rag.rank(base, "o que é de para com").isEmpty());
    }
}
