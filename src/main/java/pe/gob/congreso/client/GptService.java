package pe.gob.congreso.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pe.gob.congreso.dto.OpenAIMessage;
import pe.gob.congreso.dto.OpenAIRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Service
public class GptService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url}")
    private String apiUrl;

    @Value("${openai.api.model}")
    private String model;

    public String sendDataToGPT(String instruction) {
        try {
            String jsonRequest = generateOpenAIRequest(instruction);
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();
            ObjectMapper objectMapper = new ObjectMapper();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode rootNode = objectMapper.readTree(response.body());

            JsonNode choices = rootNode.path("choices");
            if (!choices.isEmpty()) {
                return choices.get(0).path("message").path("content").asText();
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    private String generateOpenAIRequest(String instruction) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    new OpenAIRequest(model, List.of(
                            new OpenAIMessage("developer", "You are a helpful assistant."),
                            new OpenAIMessage("user", instruction)
                    ))
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al generar JSON para OpenAI", e);
        }
    }
}