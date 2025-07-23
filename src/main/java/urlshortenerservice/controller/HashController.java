package urlshortenerservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.service.hash.HashService;

@RestController
@RequestMapping("/api/v1/urls")
@RequiredArgsConstructor
public class HashController {
    private final HashService hashService;

    @PostMapping
    public UrlResponseDto createShortUrl(@Valid @NotNull @NotBlank @RequestBody UrlRequestDto urlRequestDto) {
        return hashService.createShortUrl(urlRequestDto);
    }

    @GetMapping("/{hash}")
    public ResponseEntity<String> redirectToOriginalUrl(@Valid @NotNull @NotBlank @PathVariable String hash) {
        return hashService.redirectToOriginalUrl(hash);
    }
}
