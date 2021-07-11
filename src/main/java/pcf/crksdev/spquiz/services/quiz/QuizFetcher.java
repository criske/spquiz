package pcf.crksdev.spquiz.services.quiz;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;
import pcf.crksdev.spquiz.data.quiz.QuizQuestion;

import java.net.URI;
import java.util.Collections;
import java.util.List;

public interface QuizFetcher {

    List<QuizQuestion> fetch();

}

@Service
class QuizFetcherImpl implements QuizFetcher {

    private final RestOperations restOperations;

    private final UriComponentsBuilder uriBuilder;

    QuizFetcherImpl(
        @Value("${pcf.crksdev.spquiz.services.quiz.api-url}") URI apiUrl,
        @Value("${QUIZ_API}") String apiKey
    ) {
        this.restOperations = new RestTemplateBuilder()
            .defaultHeader("X-Api-Key", apiKey)
            .build();
        this.uriBuilder = UriComponentsBuilder.fromUri(apiUrl);
    }

    @Override
    public List<QuizQuestion> fetch() {
        QuizQuestion[] questions = this.restOperations.getForObject(
            uriBuilder.pathSegment("questions").toUriString(),
            QuizQuestion[].class
        );
        return questions != null
            ? List.of(questions)
            : Collections.emptyList();
    }
}
