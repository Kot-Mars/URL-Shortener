package urlshortenerservice.exception;

public class HashGenerationException extends RuntimeException {
    public HashGenerationException(String message) {
        super(message);
    }
}
