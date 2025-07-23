package urlshortenerservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import urlshortenerservice.dto.UrlRequestDto;
import urlshortenerservice.dto.UrlResponseDto;
import urlshortenerservice.entity.Url;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UrlMapper {
    Url toUrl(UrlRequestDto urlRequestDto);

    default UrlResponseDto toUrlResponseDto(Url url, String baseUrl) {
        if (url == null) {
            return null;
        }
        String shortUrl = baseUrl + url.getHash();
        return new UrlResponseDto(shortUrl);
    }
}
