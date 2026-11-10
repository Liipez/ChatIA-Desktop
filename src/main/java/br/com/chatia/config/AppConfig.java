package br.com.chatia.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Lê a configuração externa da aplicação (credenciais NUNCA ficam no código).
 *
 * Ordem de busca:
 * 1. Variável de ambiente (ex.: GROQ_API_KEY)
 * 2. Arquivo config.properties na pasta do usuário:
 *    - Windows: %APPDATA%\HyvexIA\config.properties
 *    - Linux/macOS: ~/.hyvexia/config.properties
 *
 * O arquivo é útil na versão instalada, que não herda as variáveis do IntelliJ.
 */
public final class AppConfig {

    private static Properties fileProperties;

    private AppConfig() {
    }

    public static String get(String name) {
        String value = System.getenv(name);
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        value = fileProperties().getProperty(name);
        return value == null || value.isBlank() ? null : value.trim();
    }

    public static String get(String name, String defaultValue) {
        String value = get(name);
        return value == null ? defaultValue : value;
    }

    public static boolean isSet(String name) {
        return get(name) != null;
    }

    public static Path configFile() {
        String appData = System.getenv("APPDATA");
        if (appData != null && !appData.isBlank()) {
            return Path.of(appData, "HyvexIA", "config.properties");
        }
        return Path.of(System.getProperty("user.home"), ".hyvexia", "config.properties");
    }

    private static synchronized Properties fileProperties() {
        if (fileProperties == null) {
            fileProperties = new Properties();
            Path file = configFile();
            if (Files.isRegularFile(file)) {
                try (InputStream in = Files.newInputStream(file)) {
                    fileProperties.load(in);
                } catch (IOException e) {
                    System.err.println("[HyvexIA] Não foi possível ler " + file + ": " + e.getMessage());
                }
            }
        }
        return fileProperties;
    }
}
