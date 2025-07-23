package urlshortenerservice.service.hash;

import org.springframework.http.ResponseEntity;
import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;

public interface HashService {
    public ResponseEntity<String> redirectToOriginalUrl(String hash);

    public UrlResponseDto createShortUrl(UrlRequestDto url);
}
