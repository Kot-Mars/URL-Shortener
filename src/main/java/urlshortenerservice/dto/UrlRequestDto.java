package urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlRequestDto {
    @NotNull(message = "Url can't not be null")
    @Schema(description = "Исходный URL для сокращения", example = "https://example.com/very/long/url", required = true)
    private String url;
}
