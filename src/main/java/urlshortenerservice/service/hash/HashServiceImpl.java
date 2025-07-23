package urlshortenerservice.service.hash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import urlshortenerservice.cache.HashCache;
import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.entity.Url;
import urlshortenerservice.exception.InvalidUrlException;
import urlshortenerservice.exception.UrlNotFound;
import urlshortenerservice.mapper.UrlMapper;
import urlshortenerservice.repository.UrlRepository;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;

import static urlshortenerservice.exception.ErrorMessage.INVALID_URL;
import static urlshortenerservice.exception.ErrorMessage.URL_NOT_FOUND_BY_HASH;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {
    private final UrlMapper urlMapper;
    private final UrlRepository urlRepository;
    private final HashCache hashCache;

    @Value("${app.base-url}")
    private String baseUrl;

    @Cacheable(value = "hash", key = "#hash")
    @Transactional(readOnly = true)
    public String getOriginalUrlByHash(String hash) {
        Url url = urlRepository.findByHash(hash)
                .orElseThrow(() -> new UrlNotFound(String.format(URL_NOT_FOUND_BY_HASH, hash)));
        return url.getUrl();
    }

    @Override
    @Transactional
    public ResponseEntity<String> redirectToOriginalUrl(String hash) {
        String originalUrl = getOriginalUrlByHash(hash);
        log.info("Get url: {} by hash: {}", originalUrl, hash);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    @Override
    @Transactional
    @Cacheable(value = "urls", key = "#request.url")
    public UrlResponseDto createShortUrl(UrlRequestDto request) {
        String urlRequest = request.getUrl();
        validUrl(urlRequest);

        Url existingUrl = findByOriginalUrl(urlRequest);
        if (existingUrl != null) {
            log.info("Found url: {}", urlRequest);
            return urlMapper.toUrlResponseDto(existingUrl, baseUrl);

        }

        String hash = hashCache.getHash();

        Url url = Url.builder()
                .hash(hash)
                .url(urlRequest)
                .createdAt(LocalDateTime.now())
                .build();
        urlRepository.save(url);
        log.info("Saved url: {}, hash: {}", url, hash);
        return urlMapper.toUrlResponseDto(url, baseUrl);
    }

    private Url findByOriginalUrl(String originalUrl) {
        return urlRepository.findByUrl(originalUrl).orElse(null);
    }

    private void validUrl(String url) {
        try {
            new URL(url).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            log.error(String.format(INVALID_URL, url), e);
            throw new InvalidUrlException(String.format(INVALID_URL, url));
        }
    }
}
