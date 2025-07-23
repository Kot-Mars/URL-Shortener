package urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlResponseDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Сокращённый URL", example = "http://localhost:8091/api/v1/urls/abc123")
    private String shortUrl;
}
