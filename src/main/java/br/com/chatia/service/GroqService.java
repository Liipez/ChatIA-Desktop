package br.com.chatia.service;

import br.com.chatia.config.GroqConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class GroqService {

    private static final String API_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private static final String MODEL = "openai/gpt-oss-20b";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GroqService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(20))
                .build();

        this.objectMapper = new ObjectMapper();
    }

    public String chat(String prompt)
            throws IOException, InterruptedException {
        return chat(prompt, "", List.of());
    }

    public String chat(String prompt, String context)
            throws IOException, InterruptedException {
        return chat(prompt, context, List.of());
    }

    public String chat(
            String prompt,
            String context,
            List<MessageHistory> history
    ) throws IOException, InterruptedException {

        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException(
                    "A mensagem não pode estar vazia."
            );
        }

        if (context == null) {
            context = "";
        }

        if (history == null) {
            history = List.of();
        }

        ObjectNode body = objectMapper.createObjectNode();

        body.put("model", MODEL);
        body.put("temperature", 0.7);
        body.put("max_tokens", 1024);

        ArrayNode messages = body.putArray("messages");

        ObjectNode systemMessage = messages.addObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", """
                Você é o assistente virtual HyvexIA.

                Responda de forma clara, útil e organizada.
                Responda no idioma utilizado pelo usuário.

                Considere as mensagens anteriores para manter
                a continuidade da conversa.

                Você poderá receber um contexto recuperado de uma
                base de conhecimento.

                Quando o contexto contiver informações relevantes,
                utilize-as para responder à pergunta.

                Não invente informações e não afirme que algo está
                presente no contexto se não estiver.

                Se o contexto não contiver informações suficientes,
                informe isso com transparência. Você ainda pode
                complementar a resposta com conhecimentos gerais,
                deixando claro quando estiver indo além do contexto.

                CONTEXTO DA BASE DE CONHECIMENTO:
                """ + (context.isBlank()
                ? "\nNenhum documento relevante foi encontrado."
                : "\n" + context));

        for (MessageHistory item : history) {
            if (item == null || item.content() == null
                    || item.content().isBlank()) {
                continue;
            }

            if (!"user".equals(item.role())
                    && !"assistant".equals(item.role())) {
                continue;
            }

            ObjectNode historyMessage = messages.addObject();
            historyMessage.put("role", item.role());
            historyMessage.put("content", item.content());
        }

        ObjectNode userMessage = messages.addObject();
        userMessage.put("role", "user");
        userMessage.put("content", prompt);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(60))
                .header(
                        "Authorization",
                        "Bearer " + GroqConfig.getApiKey()
                )
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        objectMapper.writeValueAsString(body)
                ))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {
            // O corpo da resposta vai só para o console (pode conter detalhes técnicos).
            System.err.println("[Groq] HTTP " + response.statusCode() + ": " + response.body());
            throw new GroqException(
                    response.statusCode(),
                    "Erro na API Groq (HTTP " + response.statusCode() + ")"
            );
        }

        JsonNode json = objectMapper.readTree(response.body());

        JsonNode content = json.path("choices")
                .path(0)
                .path("message")
                .path("content");

        if (content.isMissingNode() || content.isNull()) {
            throw new IOException(
                    "A Groq respondeu, mas não retornou o texto esperado."
            );
        }

        return content.asText();
    }

    public record MessageHistory(String role, String content) {
    }
}