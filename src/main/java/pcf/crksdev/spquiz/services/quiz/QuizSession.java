package pcf.crksdev.spquiz.services.quiz;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pcf.crksdev.spquiz.data.quiz.QuizQuestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface QuizSession extends Iterable<QuizSessionQuestion> {

    /**
     * Start a new quiz session if there is no other session started.
     *
     * @param newQuestions Question batch.
     * @return First question or none if batch is empty.
     * @throws QuizSessionException if the session is already started.
     */
    Optional<QuizSessionQuestion> start(List<QuizQuestion> newQuestions);

    /**
     * Resume the current session.
     *
     * @return Current question from session was left.
     * @throws QuizSessionException if the session has not started.
     */
    Optional<QuizSessionQuestion> resume();

    /**
     * Returns current question id.
     *
     * @return Id or none if session is not started.
     */
    OptionalInt getCursorId();

    /**
     * Answers a question.
     *
     * @param id Question id.
     * @param positions Answer positions in the list of available answers.
     * 0-index
     * based!
     * @return Next question.
     * @throws QuizSessionException if id or index are invalid or the question
     * require single answer.
     */
    Optional<QuizSessionQuestion> answer(
        final int id,
        final List<Integer> positions
    );

    /**
     * Answers a question.
     *
     * @param id Question id.
     * @param position Answer position in the list of available answers. 0-index
     * based!
     * @return Next question.
     * @throws QuizSessionException if id or index are invalid or the
     * question require multiple answers.
     */
    default Optional<QuizSessionQuestion> answer(
        final int id,
        final int position
    ) {
        return this.answer(id, List.of(position));
    }

    /**
     * Get the submitted answer of a question.
     *
     * @param id Question id.
     * @return QuizSessionAnswer.
     * @throws QuizSessionException if question id is invalid or the question
     * was not answered yet.
     */
    QuizSessionAnswer getAnswer(final int id);


    /**
     * Iterable for submitted answers in current session.
     *
     * @return Iterable.
     */
    Iterable<QuizSessionAnswer> submittedAnswers();

    /**
     * Get a question form batch.
     *
     * @param id Question id.
     * @return Question.
     * @throws QuizSessionException if question id is invalid.
     */
    QuizSessionQuestion getQuestion(final int id);

    /**
     * Finish the current session.
     *
     * @return Report of answered questions or none if there no question
     * answered or simply the question suite is empty.
     * @throws QuizSessionException if session is not started.
     * @see QuizSession#abort()
     */
    Optional<QuizSessionReport> finish();


    /**
     * Abort current session without generating a report.
     *
     * @throws QuizSessionException if session is not started.
     * @see QuizSession#finish()
     */
    void abort();

    /**
     * Has session started?
     *
     * @return Boolean.
     */
    boolean hasStarted();

}

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
class QuizSessionImpl implements QuizSession {

    private static final int NO_CURSOR_ID = 0;

    private static final QuizSessionAnswer NO_ANSWER = new QuizSessionAnswer(
        NO_CURSOR_ID,
        Collections.emptyList(),
        Collections.emptyList()
    );

    private final Map<Integer, NavigableQuizQuestion> questions;

    private final Map<Integer, QuizSessionAnswer> answers;

    private final AtomicBoolean hasStarted;

    private final AtomicInteger cursorId;

    QuizSessionImpl() {
        questions = new ConcurrentHashMap<>();
        answers = new ConcurrentHashMap<>();
        hasStarted = new AtomicBoolean(false);
        cursorId = new AtomicInteger(NO_CURSOR_ID);
    }

    @Override
    public Optional<QuizSessionQuestion> start(List<QuizQuestion> newQuestions) {
        final Optional<QuizSessionQuestion> startQuestion;
        if (!this.hasStarted()) {
            if (newQuestions.size() > 0) {
                startQuestion = NavigableQuizQuestion
                    .setupLinkedListMap(this.questions, newQuestions)
                    .map(this::mapToSessionQuestion);
                startQuestion.ifPresent(q ->
                    cursorId.compareAndExchange(
                        NO_CURSOR_ID,
                        newQuestions.get(0).getId()
                    )
                );
            } else {
                startQuestion = Optional.empty();
            }
            this.hasStarted.compareAndExchange(false, true);
        } else {
            throw new QuizSessionException("Session already started! Must " +
                "finish the session first.");
        }
        return startQuestion;
    }

    @Override
    public Optional<QuizSessionQuestion> resume() {
        if (!this.hasStarted()) {
            throw new QuizSessionException("Can't resume. Session has not " +
                "started.");
        }
        return this.getQuestionOptional(this.cursorId.getAcquire());
    }

    @Override
    public OptionalInt getCursorId() {
        var id = this.cursorId.getAcquire();
        return id > 0 ? OptionalInt.of(id) : OptionalInt.empty();
    }

    @Override
    public Optional<QuizSessionQuestion> answer(
        int id,
        List<Integer> positions
    ) {
        if (positions.isEmpty()) {
            throw new QuizSessionException("Must provide at least one answer.");
        }
        var question = this.getNavigableQuestion(id).question;
        var questionAnswers = question.getAnswers();
        positions.forEach(answer -> {
            if (answer >= questionAnswers.size() || answer < 0) {
                throw new QuizSessionException("Invalid answer position must " +
                    "be between 0 and " + (questionAnswers.size() - 1));
            }
        });
        if (question.hasMultipleAnswers() && positions.size() == 1) {
            throw new QuizSessionException("Question has multiple answers.");
        }
        if (!question.hasMultipleAnswers() && positions.size() > 1) {
            throw new QuizSessionException("Question has a single answer.");
        }

        var correctAnswers = question.getCorrectAnswers();
        var quizSessionAnswer = new QuizSessionAnswer(
            id,
            IntStream.range(0, correctAnswers.size()).boxed()
                .filter(correctAnswers::get)
                .collect(Collectors.toList()),
            positions
        );

        this.answers.put(id, quizSessionAnswer);
        Optional<QuizSessionQuestion> nextQuestion =
            this.getNavigableQuestionOptional(id)
                .flatMap(nav -> Optional.ofNullable(nav.next))
                .map(this::mapToSessionQuestion);
        nextQuestion
            .ifPresent(next -> this.cursorId.updateAndGet(old -> next.getId()));
        return nextQuestion;
    }

    @Override
    @NonNull
    public QuizSessionAnswer getAnswer(int id) {
        if (!this.questions.containsKey(id)) {
            throw new QuizSessionException("Invalid question id: " + id);
        }
        if (!this.answers.containsKey(id)) {
            throw new QuizSessionException("Unanswered question id: " + id);
        }
        return this.answers.get(id);
    }

    @Override
    public Iterable<QuizSessionAnswer> submittedAnswers() {
        return () -> answers.values().iterator();
    }

    @Override
    @NonNull
    public QuizSessionQuestion getQuestion(int id) {
        return this.getQuestionOptional(id)
            .orElseThrow(() -> new QuizSessionException("Invalid question id "
                + id));
    }

    @Override
    public Optional<QuizSessionReport> finish() {
        if (!this.hasStarted()) {
            throw new QuizSessionException("Can't finish a session that has " +
                "not started");
        }
        final Optional<QuizSessionReport> reportOptional;
        if (!this.answers.isEmpty()) {
            final boolean isCompleted =
                this.answers.size() == this.questions.size();
            final QuizSessionReport report = new QuizSessionReport(
                isCompleted,
                this.questions.values()
                    .stream()
                    .map(this::mapToSessionQuestion)
                    .collect(Collectors.toList()),
                new ArrayList<>(this.answers.values())
            );
            reportOptional = Optional.of(report);
        } else {
            reportOptional = Optional.empty();
        }

        this.resetState();
        return reportOptional;
    }

    @Override
    public void abort() {
        if (!this.hasStarted()) {
            throw new QuizSessionException("Can't abort a session that has " +
                "not started");
        }
        this.resetState();
    }

    @Override
    public boolean hasStarted() {
        return this.hasStarted.getAcquire();
    }

    @NotNull
    @Override
    public Iterator<QuizSessionQuestion> iterator() {
        return this.questions.values()
            .stream()
            .map(this::mapToSessionQuestion)
            .iterator();
    }


    private void resetState() {
        this.hasStarted.compareAndExchange(true, false);
        this.questions.clear();
        this.answers.clear();
        this.cursorId.updateAndGet(old -> NO_CURSOR_ID);
    }

    @NotNull
    private Optional<QuizSessionQuestion> getQuestionOptional(final int id) {
        return this.getNavigableQuestionOptional(id)
            .map(this::mapToSessionQuestion);
    }

    @NotNull
    private Optional<NavigableQuizQuestion> getNavigableQuestionOptional(final int id) {
        return Optional.ofNullable(this.questions.get(id));
    }

    @NotNull
    private NavigableQuizQuestion getNavigableQuestion(final int id) {
        return this.getNavigableQuestionOptional(id)
            .orElseThrow(() -> new QuizSessionException("Invalid question id "
                + id));
    }

    @NotNull
    private QuizSessionQuestion mapToSessionQuestion(NavigableQuizQuestion nav) {
        var prev = nav.prev != null ?
            OptionalInt.of(nav.prev.question.getId()) :
            OptionalInt.empty();
        var next = nav.next != null ?
            OptionalInt.of(nav.next.question.getId()) :
            OptionalInt.empty();
        return new QuizSessionQuestion(
            nav.question.getId(),
            nav.question.getTitle(),
            nav.question.getDescription(),
            nav.question.getExplanation(),
            nav.question.getAnswers(),
            nav.question.hasMultipleAnswers(),
            nav.question.getCategory(),
            nav.question.getDifficulty(),
            nav.question.getTags(),
            prev,
            next,
            this.answers
                .getOrDefault(nav.question.getId(), NO_ANSWER)
                .getSubmittedPositions()
        );
    }


    private static class NavigableQuizQuestion {

        /**
         * Setup the linked list map and returns the head.
         *
         * @param linkedListMap Linked list Map.
         * @param questions Questions.
         * @return Head or none if linked map is empty.
         */
        static Optional<NavigableQuizQuestion> setupLinkedListMap(
            final Map<Integer, NavigableQuizQuestion> linkedListMap,
            final List<QuizQuestion> questions
        ) {
            NavigableQuizQuestion prev = null;
            NavigableQuizQuestion head = null;
            for (final QuizQuestion question : questions) {
                NavigableQuizQuestion current =
                    new NavigableQuizQuestion(question);
                if (prev != null) {
                    prev.next = current;
                }
                current.prev = prev;
                linkedListMap.put(question.getId(), current);
                prev = current;
                if (head == null) {
                    head = current;
                }
            }
            return Optional.ofNullable(head);
        }

        final QuizQuestion question;

        NavigableQuizQuestion prev;

        NavigableQuizQuestion next;

        public NavigableQuizQuestion(final QuizQuestion question) {
            this.question = question;
        }

    }

}
