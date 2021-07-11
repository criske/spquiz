package pcf.crksdev.spquiz.services.quiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.number.BigDecimalCloseTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import pcf.crksdev.spquiz.data.quiz.Category;
import pcf.crksdev.spquiz.data.quiz.Difficulty;
import pcf.crksdev.spquiz.data.quiz.QuizQuestion;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public final class QuizSessionImplTest {

    private List<QuizQuestion> getQuizQuestions() {
        var mapper = new ObjectMapper();
        QuizQuestion[] quizQuestions = null;
        try {
            File resource = new ClassPathResource("questions.json").getFile();
            quizQuestions = mapper.readValue(
                resource,
                QuizQuestion[].class
            );
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        assert quizQuestions != null;
        return List.of(quizQuestions);
    }

    @Test
    public void startNewSession() {
        var session = new QuizSessionImpl();
        var questionOpt = session.start(this.getQuizQuestions());
        MatcherAssert.assertThat(questionOpt.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(session, Matchers.iterableWithSize(20));
        MatcherAssert.assertThat(session.hasStarted(), Matchers.is(true));

        var question = questionOpt.orElseThrow();
        MatcherAssert.assertThat(question.getId(), Matchers.is(604));
        MatcherAssert.assertThat(question.getTitle(), Matchers.is(
            "Will a comparison of an integer 12 and a string \"13\" work in " +
                "PHP?"
        ));
        MatcherAssert.assertThat(
            question.getDescription(),
            Matchers.is(Optional.empty())
        );
        MatcherAssert.assertThat(
            question.getExplanation().orElseThrow(),
            Matchers.is("In PHP you can compare strings with numbers.")
        );
        MatcherAssert.assertThat(
            question.hasMultipleAnswers(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            question.getAnswers(),
            Matchers.is(List.of("True", "False"))
        );
        MatcherAssert.assertThat(
            question.getCategory(),
            Matchers.is(Category.CODE)
        );
        MatcherAssert.assertThat(
            question.getDifficulty(),
            Matchers.is(Difficulty.EASY)
        );
        MatcherAssert.assertThat(
            question.getTags(),
            Matchers.is(List.of("PHP"))
        );
        MatcherAssert.assertThat(
            question.getPrevious(),
            Matchers.is(OptionalInt.empty())
        );
        MatcherAssert.assertThat(
            question.getNext(),
            Matchers.is(OptionalInt.of(971))
        );
    }

    @Test
    public void shouldStartEmptySessionIfQuestionsAreEmpty() {
        var session = new QuizSessionImpl();
        var question = session.start(List.of());
        MatcherAssert.assertThat(question.isPresent(), Matchers.is(false));
        MatcherAssert.assertThat(session, Matchers.emptyIterable());
        MatcherAssert.assertThat(session.hasStarted(), Matchers.is(true));
    }

    @Test
    public void shouldThrowStartIfAlreadyStarted() {
        var session = new QuizSessionImpl();
        session.start(List.of());
        Assertions.assertThrows(
            QuizSessionException.class,
            () -> session.start(List.of())
        );
    }

    @Test
    public void shouldResumeSession() {
        var session = new QuizSessionImpl();
        var startQuestion =
            session.start(this.getQuizQuestions()).orElseThrow();
        var resumedQuestion = session.resume().orElseThrow();
        MatcherAssert.assertThat(
            startQuestion.getId(),
            Matchers.is(resumedQuestion.getId())
        );
        MatcherAssert.assertThat(
            session.getCursorId().orElseThrow(),
            Matchers.is(resumedQuestion.getId())
        );
    }

    @Test
    public void shouldThrowWhenResumeBeforeStartSession() {
        var session = new QuizSessionImpl();
        Assertions.assertThrows(QuizSessionException.class, session::resume);
    }

    @Test
    public void shouldAnswerQuestionCorrect() {
        var session = new QuizSessionImpl();
        var question = session
            .start(this.getQuizQuestions()).orElseThrow();
        session.answer(question.getId(), 0);
        var answer = session.getAnswer(604);

        MatcherAssert.assertThat(
            question.getSubmittedAnswerPositions(),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(question.isAnswered(), Matchers.is(false));

        MatcherAssert.assertThat(answer.isCorrect(), Matchers.is(true));
        MatcherAssert.assertThat(answer.missed(), Matchers.is(0));
        MatcherAssert.assertThat(answer.isMultiple(), Matchers.is(false));
        MatcherAssert.assertThat(answer.getQuestionId(), Matchers.is(604));
        MatcherAssert.assertThat(
            answer.getCorrectPositions(),
            Matchers.is(List.of(0))
        );
        MatcherAssert.assertThat(
            answer.getSubmittedPositions(),
            Matchers.is(List.of(0))
        );

        var answeredQuestion = session.getQuestion(question.getId());
        MatcherAssert.assertThat(
            answeredQuestion.getSubmittedAnswerPositions(),
            Matchers.is(answer.getSubmittedPositions())
        );
        MatcherAssert.assertThat(
            answeredQuestion.isAnswered(),
            Matchers.is(true)
        );

        MatcherAssert.assertThat(
            "Cursor should advance to next question",
            session.getCursorId(),
            Matchers.is(answeredQuestion.getNext())
        );
    }

    @Test
    public void shouldResumeAfterAnswerAQuestion() {
        var session = new QuizSessionImpl();
        var question = session
            .start(this.getQuizQuestions()).orElseThrow();
        question = session.answer(question.getId(), 0).orElseThrow();
        var resumed = session.resume().orElseThrow();
        MatcherAssert.assertThat(
            question.getId(),
            Matchers.is(resumed.getId())
        );
        MatcherAssert.assertThat(
            session.getCursorId().orElseThrow(),
            Matchers.is(resumed.getId())
        );
    }

    @Test
    public void shouldAnswerQuestionWrong() {
        var session = new QuizSessionImpl();
        var question = session
            .start(this.getQuizQuestions()).orElseThrow();
        session.answer(question.getId(), 1);
        var answer = session.getAnswer(604);
        MatcherAssert.assertThat(answer.isCorrect(), Matchers.is(false));
        MatcherAssert.assertThat(answer.missed(), Matchers.is(1));
        MatcherAssert.assertThat(answer.isMultiple(), Matchers.is(false));
    }

    @Test
    public void shouldThrowIfSubmitMultipleAnsForSingleAnsQuestion() {
        var session = new QuizSessionImpl();
        var question = session
            .start(this.getQuizQuestions()).orElseThrow();
        Assertions.assertThrows(
            QuizSessionException.class,
            () -> session.answer(question.getId(), List.of(0, 1))
        );
    }

    @Test
    public void shouldThrowIfSubmitSingleAnsForMultipleAnsQuestion() {
        var session = new QuizSessionImpl();
        session.start(this.getQuizQuestions()).orElseThrow();
        Assertions.assertThrows(
            QuizSessionException.class,
            () -> session.answer(1072, List.of(0))
        );
    }

    @Test
    public void shouldThrowAnsPositionIsLowInvalid() {
        var session = new QuizSessionImpl();
        var q = session.start(this.getQuizQuestions()).orElseThrow();
        Assertions.assertThrows(
            QuizSessionException.class,
            () -> session.answer(q.getId(), List.of(-1))
        );
    }

    @Test
    public void shouldThrowAnsPositionIsHighInvalid() {
        var session = new QuizSessionImpl();
        var q = session.start(this.getQuizQuestions()).orElseThrow();
        Assertions.assertThrows(
            QuizSessionException.class,
            () -> session.answer(q.getId(), List.of(Integer.MAX_VALUE))
        );
    }

    @Test
    public void shouldThrowIfNoAnswersAreSubmitted() {
        var session = new QuizSessionImpl();
        var q = session.start(this.getQuizQuestions()).orElseThrow();
        Assertions.assertThrows(
            QuizSessionException.class,
            () -> session.answer(q.getId(), List.of())
        );
    }


    @Test
    public void shouldThrowWhenGetAnswerForInvalidQuestion() {
        var session = new QuizSessionImpl();
        session.start(this.getQuizQuestions()).orElseThrow();
        Assertions.assertThrows(
            QuizSessionException.class,
            () -> session.getAnswer(Integer.MAX_VALUE)
        );
    }

    @Test
    public void shouldThrowWhenGetAnswerForUnansweredQuestion() {
        var session = new QuizSessionImpl();
        var q = session.start(this.getQuizQuestions()).orElseThrow();
        Assertions.assertThrows(
            QuizSessionException.class,
            () -> session.getAnswer(q.getId())
        );
    }

    @Test
    public void shouldGetQuestionById() {
        var session = new QuizSessionImpl();
        session.start(this.getQuizQuestions());

        var question = session.getQuestion(1073);
        MatcherAssert.assertThat(question.getId(), Matchers.is(1073));
        MatcherAssert.assertThat(
            question.getPrevious(),
            Matchers.is(OptionalInt.of(910))
        );
        MatcherAssert.assertThat(
            question.getNext(),
            Matchers.is(OptionalInt.of(1045))
        );

        question = session.getQuestion(811);
        MatcherAssert.assertThat(
            question.getPrevious(),
            Matchers.is(OptionalInt.of(1072))
        );
        MatcherAssert.assertThat(
            question.getNext(),
            Matchers.is(OptionalInt.empty())
        );
    }

    @Test
    public void shouldFinishSession() {
        var session = new QuizSessionImpl();
        var quizQuestions = this.getQuizQuestions()
            .stream()
            .limit(10)
            .collect(Collectors.toList());
        var q = session.start(quizQuestions).orElseThrow();

        q = session.answer(q.getId(), 0).orElseThrow();
        q = session.answer(q.getId(), List.of(0, 1)).orElseThrow();
        q = session.answer(q.getId(), List.of(2)).orElseThrow();
        session.answer(q.getId(), 1).orElseThrow();

        QuizSessionReport report = session.finish().orElseThrow();

        MatcherAssert.assertThat(
            session.hasStarted(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            session.getCursorId(),
            Matchers.is(OptionalInt.empty())
        );
        MatcherAssert.assertThat(
            session.submittedAnswers(),
            Matchers.emptyIterable()
        );
        MatcherAssert.assertThat(
            session,
            Matchers.emptyIterable()
        );


        MatcherAssert.assertThat(
            !report.getId().isEmpty(),
            Matchers.is(true)
        );
        MatcherAssert.assertThat(
            report.getDate().toLocalDate(),
            Matchers.is(LocalDate.now())
        );
        MatcherAssert.assertThat(
            report.isCompleted(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            report.getScore(),
            Matchers.is(BigDecimalCloseTo.closeTo(
                BigDecimal.valueOf(0.20),
                BigDecimal.valueOf(0.05)
            ))
        );
        MatcherAssert.assertThat(
            report.getAnswers(),
            Matchers.iterableWithSize(4)
        );
        MatcherAssert.assertThat(
            report.getQuestions(),
            Matchers.iterableWithSize(10)
        );

    }

    @Test
    public void shouldGenerateEmptyReportIfThereAreNoAnswers() {
        var session = new QuizSessionImpl();
        session.start(this.getQuizQuestions());
        var report = session.finish();
        MatcherAssert.assertThat(
            report,
            Matchers.is(Optional.empty())
        );
    }

    @Test
    public void shouldThrowIfFinishANotStartedSession() {
        var session = new QuizSessionImpl();
        Assertions.assertThrows(
            QuizSessionException.class,
            session::finish
        );
    }

    @Test
    public void shouldAbortSession() {
        var session = new QuizSessionImpl();
        session.start(List.of());
        session.abort();

        MatcherAssert.assertThat(
            session.hasStarted(),
            Matchers.is(false)
        );
    }

    @Test
    public void shouldThrowIfAbortANotStartedSession(){
        var session = new QuizSessionImpl();
        Assertions.assertThrows(
            QuizSessionException.class,
            session::abort
        );
    }
}