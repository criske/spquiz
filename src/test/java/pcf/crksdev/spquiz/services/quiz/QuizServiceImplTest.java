package pcf.crksdev.spquiz.services.quiz;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

class QuizServiceImplTest {


    @Test
    public void shouldStartSession() {
        var fetcher = Mockito.mock(QuizFetcher.class);
        var session = Mockito.mock(QuizSession.class);
        var quizService = new QuizServiceImpl(fetcher, session);

        Mockito.when(fetcher.fetch()).thenReturn(List.of());
        Mockito.when(session.start(List.of())).thenReturn(
            Optional.empty()
        );

        MatcherAssert.assertThat(
            quizService.startOrResumeSession(),
            Matchers.is(Optional.empty())
        );
    }

    @Test
    public void shouldResumeSession() {
        var fetcher = Mockito.mock(QuizFetcher.class);
        var session = Mockito.mock(QuizSession.class);
        var quizService = new QuizServiceImpl(fetcher, session);

        Mockito.when(fetcher.fetch()).thenReturn(List.of());
        Mockito.when(session.start(List.of())).thenReturn(
            Optional.empty()
        );
        var started = new AtomicBoolean(false);
        Mockito.when(session.hasStarted()).thenAnswer(inv -> started
            .compareAndExchange(false, true));

        quizService.startOrResumeSession();
        quizService.startOrResumeSession();

        MatcherAssert.assertThat(
            quizService.startOrResumeSession(),
            Matchers.is(Optional.empty())
        );

        Mockito.verify(fetcher, Mockito.times(1)).fetch();
        Mockito.verify(session, Mockito.times(1)).start(
            List.of()
        );
        Mockito.verify(session, Mockito.times(2)).resume();
    }

    /**
     * This test verify delegation, constraints and other stuff are fully
     * tested in {@link QuizSessionImplTest}.
     */
    @Test
    public void shouldEnsureDelegationCallsToQuizSession() {
        var fetcher = Mockito.mock(QuizFetcher.class);
        var session = Mockito.mock(QuizSession.class);
        var quizService = new QuizServiceImpl(fetcher, session);

        Mockito.when(fetcher.fetch()).thenReturn(List.of());
        Mockito.when(session.start(List.of())).thenReturn(
            Optional.empty()
        );

        quizService.question(1);
        quizService.answer(1, List.of(0));
        quizService.finish();
        quizService.abort();

        Mockito.verify(session).getQuestion(1);
        Mockito.verify(session).answer(1, List.of(0));
        Mockito.verify(session).finish();
        Mockito.verify(session).abort();
    }

}