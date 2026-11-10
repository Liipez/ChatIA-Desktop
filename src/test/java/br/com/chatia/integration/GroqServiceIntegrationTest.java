package br.com.chatia.integration;

import br.com.chatia.service.GroqService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.com.chatia.integration.IntegrationTestSupport.requireEnv;
import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
class GroqServiceIntegrationTest {

    @Test
    void respondeUsandoOContextoDoRag() throws Exception {
        requireEnv("GROQ_API_KEY");

        String contexto = "Título: Projeto Hyvex\nConteúdo: O codinome interno do projeto é AURORA-42.";
        String resposta = new GroqService().chat(
                "Qual é o codinome interno do projeto? Responda só o codinome.",
                contexto,
                List.of());

        assertNotNull(resposta);
        assertTrue(resposta.toUpperCase().contains("AURORA-42"), resposta);
    }

    @Test
    void mensagemVaziaEhRecusada() {
        assertThrows(IllegalArgumentException.class, () -> new GroqService().chat(" "));
    }
}
