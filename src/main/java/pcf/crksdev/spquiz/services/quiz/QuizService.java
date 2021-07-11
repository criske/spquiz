package pcf.crksdev.spquiz.services.quiz;

import org.springframework.stereotype.Service;
import pcf.crksdev.spquiz.data.quiz.QuizQuestion;

import java.util.List;
import java.util.Optional;

public interface QuizService {

    /**
     * Start new session or resume a session in progress
     *
     * @return Current question in session.
     */
    Optional<QuizSessionQuestion> startOrResumeSession();

    /**
     * Answers a question with provided id and answers positions.
     * The positions must one or more of the indexes of
     * {@link QuizSessionQuestion#getAnswers()}.
     *
     * @param questionId Question id.
     * @param answers Answer positions. 0-index based.
     * @return Next question to be answered or none if the question suite
     * has finished.
     * @throws QuizSessionException if question id is invalid, or one of the
     * submitted position is invalid or the list is empty.
     * @see QuizSessionQuestion#getAnswers()
     */
    Optional<QuizSessionQuestion> answer(
        final int questionId,
        final List<Integer> answers
    );

    /**
     * Get a question.
     *
     * @param id Question id.
     * @return QuizSessionQuestion
     * @throws QuizSessionException if id is invalid.
     */
    QuizSessionQuestion question(final int id);

    /**
     * Finish the current session.
     *
     * @return Report of answered questions or none if there no question
     * answered or simply the question suite is empty.
     * @throws QuizSessionException if session is not started.
     * @see QuizService#abort()
     */
    Optional<QuizSessionReport> finish();

    /**
     * Abort current session without generating a report.
     *
     * @throws QuizSessionException if session is not started.
     * @see QuizService#finish()
     */
    void abort();

}

@Service
class QuizServiceImpl implements QuizService {

    private final QuizFetcher quizFetcher;

    private final QuizSession session;

    QuizServiceImpl(QuizFetcher quizFetcher, QuizSession session) {
        this.quizFetcher = quizFetcher;
        this.session = session;
    }

    @Override
    public Optional<QuizSessionQuestion> startOrResumeSession() {
        if (!this.session.hasStarted()) {
            final List<QuizQuestion> questions = this.quizFetcher.fetch();
            return this.session.start(questions);
        } else {
            return this.session.resume();
        }
    }

    @Override
    public Optional<QuizSessionQuestion> answer(
        int questionId,
        List<Integer> answers
    ) {
        return this.session.answer(questionId, answers);
    }

    @Override
    public QuizSessionQuestion question(int id) {
        return this.session.getQuestion(id);
    }

    @Override
    public Optional<QuizSessionReport> finish() {
        return this.session.finish();
    }

    @Override
    public void abort() {
        this.session.abort();
    }

}



