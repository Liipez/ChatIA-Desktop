package br.com.chatia.config;

public class GroqConfig {

    public static String getApiKey() {
        String apiKey = System.getenv("GROQ_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "A variável GROQ_API_KEY não está configurada."
            );
        }

        return apiKey;
    }

    public static boolean isConfigured() {
        String apiKey = System.getenv("GROQ_API_KEY");
        return apiKey != null && !apiKey.isBlank();
    }
}
