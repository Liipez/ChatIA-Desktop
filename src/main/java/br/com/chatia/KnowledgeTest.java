package br.com.chatia;

import br.com.chatia.model.KnowledgeDocument;
import java.time.ZoneId;
import java.util.Date;
import br.com.chatia.repository.KnowledgeRepository;
import br.com.chatia.service.KnowledgeService;
import java.util.List;

public class KnowledgeTest {
    public static void main(String[] args) {
        try {
            KnowledgeService knowledgeService = new KnowledgeService();

            knowledgeService.saveDocument(
                    "Java",
                    "Java é uma linguagem de programação orientada a objetos, "
                            + "utilizada para criar aplicações desktop, sistemas web "
                            + "e aplicações empresariais.",
                    List.of("programação", "orientação a objetos", "backend")
            );

            knowledgeService.saveDocument(
                    "Spring Boot",
                    "Spring Boot é um framework Java que facilita a criação "
                            + "de APIs e aplicações web com configuração simplificada.",
                    List.of("java", "framework", "api", "backend")
            );

            knowledgeService.saveDocument(
                    "MongoDB",
                    "MongoDB é um banco de dados NoSQL orientado a documentos. "
                            + "Ele armazena dados em estruturas semelhantes a documentos JSON.",
                    List.of("banco de dados", "nosql", "documentos")
            );

            System.out.println("Documentos cadastrados com sucesso!");
            System.out.println();

            List<KnowledgeDocument> resultados =
                    new br.com.chatia.service.RAGService().searchRelevantDocuments(
                            "O que é Java e para que serve?"
                    );

            System.out.println("Resultados encontrados:");

            for (KnowledgeDocument documento : resultados) {
                System.out.println("- " + documento.getTitle());
            }

            System.out.println();
            System.out.println("Contexto recuperado:");
            System.out.println(
                    new br.com.chatia.service.RAGService().buildContext(
                            "O que é Java e para que serve?"
                    )
            );

        } catch (Exception e) {
            System.out.println("Erro no teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
