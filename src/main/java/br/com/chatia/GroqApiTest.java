package br.com.chatia;

import br.com.chatia.service.GroqService;

public class GroqApiTest {

    public static void main(String[] args) {
        try {
            GroqService groqService = new GroqService();

            System.out.println("Conectando à inteligência artificial...");
            System.out.println();

            String resposta = groqService.chat(
                    "Olá! Meu nome é Luiz. Você pode se apresentar " +
                            "e explicar brevemente o que consegue fazer?"
            );

            System.out.println("Resposta da Groq:");
            System.out.println(resposta);

        } catch (Exception e) {
            System.out.println("Não foi possível obter uma resposta.");
            System.out.println("Motivo: " + e.getMessage());
        }
    }
}
