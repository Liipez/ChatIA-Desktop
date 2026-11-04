
package br.com.chatia;

public class GroqConfigTest {

    public static void main(String[] args) {
        if (GroqConfig.isConfigured()) {
            System.out.println("GROQ_API_KEY configurada com sucesso!");
        } else {
            System.out.println("GROQ_API_KEY não encontrada.");
        }
    }
}
