package pcf.crksdev.spquiz.services.quiz;

import java.util.List;
import java.util.stream.Collectors;

public final class QuizSessionAnswer {

    private final int questionId;

    private final List<Integer> correctPositions;

    private final List<Integer> submittedPositions;

    private final int missed;

    public QuizSessionAnswer(
        int questionId,
        List<Integer> correctPositions,
        List<Integer> submittedPositions
    ) {
        this.questionId = questionId;
        this.correctPositions = correctPositions
            .stream()
            .sorted()
            .collect(Collectors.toList());
        this.submittedPositions = submittedPositions
            .stream()
            .sorted()
            .collect(Collectors.toList());
        this.missed = (int) this.submittedPositions.
            stream()
            .filter(e -> !this.correctPositions.contains(e))
            .count();
    }

    public int getQuestionId() {
        return questionId;
    }

    public boolean isCorrect() {
        return missed == 0;
    }

    public boolean isMultiple() {
        return correctPositions.size() > 1;
    }

    public int missed() {
        return this.missed;
    }

    public List<Integer> getCorrectPositions() {
        return correctPositions;
    }

    public List<Integer> getSubmittedPositions() {
        return submittedPositions;
    }
}
