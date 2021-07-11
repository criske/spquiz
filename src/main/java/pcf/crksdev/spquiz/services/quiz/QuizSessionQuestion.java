package pcf.crksdev.spquiz.services.quiz;

import pcf.crksdev.spquiz.data.quiz.Category;
import pcf.crksdev.spquiz.data.quiz.Difficulty;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class QuizSessionQuestion {

    private final int id;

    private final OptionalInt previous;

    private final OptionalInt next;

    private final boolean hasMultipleAnswers;

    private final String title;

    private final String description;

    private final String explanation;

    private final List<String> answers;

    private final List<String> tags;

    private final Category category;

    private final Difficulty difficulty;

    private final List<Integer> submittedAnswerPositions;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public QuizSessionQuestion(
        final int id,
        final String title,
        final String description,
        final String explanation,
        final List<String> answers,
        final boolean hasMultipleAnswers,
        final Category category,
        final Difficulty difficulty,
        final List<String> tags,
        final OptionalInt previous,
        final OptionalInt next,
        List<Integer> submittedAnswerPositions
    ) {
        this.id = id;
        this.description = description;
        this.explanation = explanation;
        this.answers = Collections.unmodifiableList(answers);
        this.hasMultipleAnswers = hasMultipleAnswers;
        this.previous = previous;
        this.next = next;
        this.title = title;
        this.tags = Collections.unmodifiableList(tags);
        this.category = category;
        this.difficulty = difficulty;
        this.submittedAnswerPositions = Collections.unmodifiableList(submittedAnswerPositions);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean hasMultipleAnswers() {
        return hasMultipleAnswers;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public List<String> getAnswers() {
        return answers;
    }

    public Category getCategory() {
        return category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public Optional<String> getExplanation() {
        return Optional.ofNullable(explanation);
    }

    public List<String> getTags() {
        return tags;
    }

    public boolean isAnswered() {
        return !this.submittedAnswerPositions.isEmpty();
    }

    public List<Integer> getSubmittedAnswerPositions() {
        return this.submittedAnswerPositions;
    }

    public OptionalInt getPrevious() {
        return previous;
    }

    public OptionalInt getNext() {
        return next;
    }
}
