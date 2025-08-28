package pe.gob.congreso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OpenAIRequest {
    private String model;
    private List<OpenAIMessage> messages;
}