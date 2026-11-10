package br.com.chatia.config;

public final class GroqConfig {

    private GroqConfig() {
    }

    public static String getApiKey() {
        String apiKey = AppConfig.get("GROQ_API_KEY");

        if (apiKey == null) {
            throw new IllegalStateException(
                    "A chave da IA não está configurada. Defina GROQ_API_KEY "
                            + "(variável de ambiente ou " + AppConfig.configFile() + ")."
            );
        }

        return apiKey;
    }

    public static boolean isConfigured() {
        return AppConfig.isSet("GROQ_API_KEY");
    }
}
