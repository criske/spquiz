package pcf.crksdev.spquiz.services.quiz;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QuizSessionReport {
    private final String id;
    private final boolean isCompleted;
    private final List<QuizSessionQuestion> questions;
    private final List<QuizSessionAnswer> answers;
    private final BigDecimal score;
    private final LocalDateTime date;

    public QuizSessionReport(
        final boolean isCompleted,
        final Collection<QuizSessionQuestion> questions,
        final Collection<QuizSessionAnswer> answers
    ) {
        this.id = UUID.randomUUID().toString();
        this.date = LocalDateTime.now();
        this.isCompleted = isCompleted;
        this.questions = List.copyOf(questions);
        this.answers = List.copyOf(answers);
        this.score = this.calculateScore();
    }

    public String getId() {
        return id;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public List<QuizSessionQuestion> getQuestions() {
        return questions;
    }

    public List<QuizSessionAnswer> getAnswers() {
        return answers;
    }

    public BigDecimal getScore() {
        return this.score;
    }

    public LocalDateTime getDate() {
        return date;
    }

    private BigDecimal calculateScore() {
        final BigDecimal total = BigDecimal.valueOf(this.questions.size());
        final BigDecimal correct = BigDecimal.valueOf(this.answers.stream()
            .filter(QuizSessionAnswer::isCorrect)
            .count());
        return correct
            .divide(total)
            .setScale(2, RoundingMode.HALF_EVEN);
    }
}
