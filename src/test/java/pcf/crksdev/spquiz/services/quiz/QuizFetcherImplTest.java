package pcf.crksdev.spquiz.services.quiz;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

class QuizFetcherImplTest {

    @Test
    public void shouldFetchQuizFromRemote()
        throws IOException, InterruptedException {
        var server = new MockWebServer();
        server.start();
        server.enqueue(new MockResponse().setResponseCode(200)
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(Files
                .readString(new ClassPathResource("questions.json").getFile().toPath())));

        var fetched = new QuizFetcherImpl(
            server.url("/api/v1").uri(),
            "api_key_123"
        ).fetch();

        var request = server.takeRequest();

        MatcherAssert.assertThat(
            request.getPath(),
            Matchers.is("/api/v1/questions")
        );

        MatcherAssert.assertThat(
            request.getHeader("X-Api-Key"),
            Matchers.is("api_key_123")
        );

        MatcherAssert.assertThat(fetched, Matchers.iterableWithSize(20));
        MatcherAssert.assertThat(fetched.get(0).getId(), Matchers.is(604));

        server.shutdown();
    }
}