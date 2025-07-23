package urlshortenerservice.service.hash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import urlshortenerservice.cache.HashCache;
import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.entity.Url;
import urlshortenerservice.exception.InvalidUrlException;
import urlshortenerservice.exception.UrlNotFound;
import urlshortenerservice.mapper.UrlMapper;
import urlshortenerservice.repository.UrlRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;


@ExtendWith(MockitoExtension.class)
class HashServiceImplTest {
    @InjectMocks
    private HashServiceImpl hashService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlMapper urlMapper;

    @Mock
    private HashCache hashCache;

    private Url sampleUrl;
    private String baseUrl;
    private UrlRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        sampleUrl = Url.builder()
                .hash("hash123")
                .url("http://example.com")
                .createdAt(LocalDateTime.now())
                .build();

        sampleRequest = new UrlRequestDto("http://example.com");

        baseUrl = "http://example.com/";

        Field field = null;
        try {
            field = HashServiceImpl.class.getDeclaredField("baseUrl");
            field.setAccessible(true);
            field.set(hashService, baseUrl);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetOriginalUrlByHash_whenUrlExists_shouldReturnUrl() {
        when(urlRepository.findByHash("hash123")).thenReturn(Optional.of(sampleUrl));

        String result = hashService.getOriginalUrlByHash("hash123");

        assertEquals("http://example.com", result);
        verify(urlRepository, times(1)).findByHash("hash123");
    }

    @Test
    void testGetOriginalUrlByHash_whenUrlDoesNotExist_shouldThrowException() {
        when(urlRepository.findByHash("hash123")).thenReturn(Optional.empty());

        UrlNotFound exception = assertThrows(UrlNotFound.class, () ->
            hashService.getOriginalUrlByHash("hash123"));

        assertTrue(exception.getMessage().contains("hash123"));
        verify(urlRepository, times(1)).findByHash("hash123");
    }

    @Test
    void testRedirectToOriginalUrl_shouldReturnRedirectResponse() {
        when(urlRepository.findByHash("hash123")).thenReturn(Optional.of(sampleUrl));

        ResponseEntity<String> response = hashService.redirectToOriginalUrl("hash123");

        assertEquals(302, response.getStatusCodeValue());
        assertEquals("http://example.com", response.getHeaders().getLocation().toString());
        verify(urlRepository, times(1)).findByHash("hash123");
    }

    @Test
    void testCreateShortUrl_whenUrlIsValidAndExists_shouldReturnExistingUrlResponse() {
        when(urlRepository.findByUrl("http://example.com")).thenReturn(Optional.of(sampleUrl));
        when(urlMapper.toUrlResponseDto(sampleUrl, baseUrl)).thenReturn(new UrlResponseDto("http://example.com"));

        UrlResponseDto response = hashService.createShortUrl(sampleRequest);

        assertEquals("http://example.com", response.getShortUrl());
        verify(urlRepository, times(1)).findByUrl("http://example.com");
        verify(urlRepository, never()).save(any());
    }

    @Test
    void testCreateShortUrl_whenUrlIsValidAndNew_shouldSaveAndReturnUrlResponse() {
        when(urlRepository.findByUrl("http://example.com")).thenReturn(Optional.empty());
        when(hashCache.getHash()).thenReturn("newHash");
        when(urlMapper.toUrlResponseDto(any(), eq("http://example.com/"))).thenReturn(new UrlResponseDto("http://example.com"));

        UrlResponseDto response = hashService.createShortUrl(sampleRequest);

        assertEquals("http://example.com", response.getShortUrl());
        verify(urlRepository, times(1)).save(any());
    }

    @Test
    void testCreateShortUrl_whenUrlIsInvalid_shouldThrowException() {
        UrlRequestDto invalidRequest = new UrlRequestDto("htp:/invalid-url");

        InvalidUrlException exception = assertThrows(InvalidUrlException.class, () ->
            hashService.createShortUrl(invalidRequest));

        assertTrue(exception.getMessage().contains("htp:/invalid-url"));
        verify(urlRepository, never()).save(any());
    }
}