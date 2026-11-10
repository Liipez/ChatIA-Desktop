package br.com.chatia.integration;

import org.junit.jupiter.api.Assumptions;

/** Utilitários dos testes de integração (banco e API reais). */
final class IntegrationTestSupport {

    private IntegrationTestSupport() {
    }

    /** Pula o teste quando a variável de ambiente não está configurada. */
    static void requireEnv(String name) {
        String value = System.getenv(name);
        Assumptions.assumeTrue(value != null && !value.isBlank(),
                name + " não configurada: teste de integração ignorado.");
    }

    /** E-mail único para não colidir com usuários reais. */
    static String uniqueEmail(String prefix) {
        return prefix + "." + System.nanoTime() + "@teste.chatia.local";
    }
}
