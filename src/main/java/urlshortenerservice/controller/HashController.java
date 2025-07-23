package urlshortenerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortenerservice.dto.ErrorResponseDto;
import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.service.hash.HashService;

@Tag(name = "URL Shortener", description = "Операции по созданию и перенаправлению коротких ссылок")
@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class HashController {
    private final HashService hashService;

    @PostMapping
    @Operation(summary = "Создать короткую ссылку", description = "Сохраняет длинный URL и возвращает сокращённый вариант")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Короткая ссылка успешно создана",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверный URL",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервиса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public UrlResponseDto createShortUrl(@Valid @NotNull @NotBlank @RequestBody UrlRequestDto urlRequestDto) {
        return hashService.createShortUrl(urlRequestDto);
    }

    @GetMapping("/{hash}")
    @Operation(summary = "Перенаправить", description = "Перенаправляет по сокращённой ссылке")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Успешное перенаправление"),
            @ApiResponse(responseCode = "404", description = "Ссылка не найдена",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервиса",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<String> redirectToOriginalUrl(@Valid @NotNull @NotBlank @PathVariable String hash) {
        return hashService.redirectToOriginalUrl(hash);
    }
}
