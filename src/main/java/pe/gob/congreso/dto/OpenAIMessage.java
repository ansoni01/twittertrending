package pe.gob.congreso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenAIMessage {
    private String role;
    private String content;
}
