 package br.com.chatia;

import br.com.chatia.service.GroqService;
import br.com.chatia.service.KnowledgeService;

import java.util.List;

public class RagTest {

    public static void main(String[] args) throws Exception {

        KnowledgeService knowledgeService = new KnowledgeService();
        GroqService groqService = new GroqService();

        String pergunta =
                "O que é MongoDB e como ele armazena dados?";

        System.out.println("=== PERGUNTA ===");
        System.out.println(pergunta);

        String contexto = knowledgeService.buildContext(pergunta);

        System.out.println("\n=== CONTEXTO RECUPERADO ===");

        if (contexto.isBlank()) {
            System.out.println("Nenhum documento foi recuperado.");
            return;
        }

        System.out.println(contexto);

        System.out.println("\n=== RESPOSTA DA GROQ ===");

        String resposta = groqService.chat(
                pergunta,
                contexto,
                List.of()
        );

        System.out.println(resposta);
    }
}
