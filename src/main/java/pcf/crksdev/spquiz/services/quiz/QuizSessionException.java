package pcf.crksdev.spquiz.services.quiz;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public final class QuizSessionException extends RuntimeException {
    public QuizSessionException(String message) {
        super(message);
    }
}
