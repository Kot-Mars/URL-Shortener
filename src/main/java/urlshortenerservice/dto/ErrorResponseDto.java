package urlshortenerservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    @Schema(description = "Код ошибки HTTP", example = "404")
    private int status;

    @Schema(description = "Краткое описание ошибки", example = "Url not found")
    private String error;

    @Schema(description = "Подробное сообщение", example = "Url with hash abc123 not found")

    private String message;

    @Schema(description = "Название сервиса", example = "url_shortener_service")
    private String service;
}

