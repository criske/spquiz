package pcf.crksdev.spquiz.data.quiz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JsonIgnoreProperties(value = {"correct_answer", "tip"})
public class QuizQuestion {

    private int id;

    @JsonProperty("question")
    private String title;

    private String description;

    @JsonDeserialize(using = AnswersDeserializerStr.class)
    private List<String> answers;

    @JsonProperty("multiple_correct_answers")
    private boolean hasMultipleAnswers;

    @JsonProperty("correct_answers")
    @JsonDeserialize(using = CorrectAnswersDeserializer.class)
    private List<Boolean> correctAnswers;

    private String explanation;

    @JsonDeserialize(using = TagsDeserializer.class)
    private List<String> tags;

    @JsonDeserialize(using = CategoryDeserializer.class)
    private Category category;

    private Difficulty difficulty;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<Boolean> getCorrectAnswers() {
        return correctAnswers;
    }

    public boolean hasMultipleAnswers() {
        return hasMultipleAnswers;
    }

    public void setHasMultipleAnswers(boolean hasMultipleAnswers) {
        this.hasMultipleAnswers = hasMultipleAnswers;
    }

    public String getExplanation() {
        return this.explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }


    static abstract class MapToListValuesDeserializer<T> extends StdDeserializer<List<T>> {

        public MapToListValuesDeserializer() {
            this(null);
        }

        protected MapToListValuesDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public List<T> deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext
        ) throws IOException {
            JsonNode structNode = jsonParser
                .getCodec()
                .readTree(jsonParser);
            final List<T> values = new ArrayList<>();
            if (structNode.isArray()) {
                for (final JsonNode node : structNode) {
                    final var keys = this.acceptKeys();
                    for (String key : keys) {
                        JsonNode candidate = node.get(key);
                        if (candidate != null && !candidate.isNull()) {
                            this.asValue(candidate).ifPresent(values::add);
                        }
                    }
                }
            } else if (structNode.isObject()) {
                final var keys = this.acceptKeys();
                for (String key : keys) {
                    JsonNode candidate = structNode.get(key);
                    if (candidate != null && !candidate.isNull()) {
                        this.asValue(candidate).ifPresent(values::add);
                    }
                }
            } else {
                throw new IllegalStateException("Invalid structure must be " +
                    "object or array but was" + structNode);
            }
            return values;
        }

        protected abstract List<String> acceptKeys();

        protected abstract Optional<T> asValue(JsonNode node);
    }

    static class TagsDeserializer extends MapToListValuesDeserializer<String> {
        @Override
        protected List<String> acceptKeys() {
            return List.of("name");
        }

        @Override
        protected Optional<String> asValue(JsonNode node) {
            return node.isTextual() ? Optional.of(node.textValue()) :
                Optional.empty();
        }
    }

    static abstract class AnswersDeserializer<T> extends MapToListValuesDeserializer<T> {

        @Override
        protected List<String> acceptKeys() {
            return List.of("answer_a", "answer_b", "answer_c", "answer_d",
                "answer_e", "answer_f"
            );
        }
    }

    static class AnswersDeserializerStr extends MapToListValuesDeserializer<String> {

        @Override
        protected List<String> acceptKeys() {
            return List.of("answer_a", "answer_b", "answer_c", "answer_d",
                "answer_e", "answer_f"
            );
        }

        @Override
        protected Optional<String> asValue(JsonNode node) {
            return node.isTextual() ? Optional.of(node.textValue()) :
                Optional.empty();
        }
    }

    static class CorrectAnswersDeserializer extends AnswersDeserializer<Boolean> {

        @Override
        protected List<String> acceptKeys() {
            return super.acceptKeys()
                .stream()
                .map(a -> a + "_correct")
                .collect(Collectors.toList());
        }

        @Override
        protected Optional<Boolean> asValue(JsonNode node) {
            final boolean value;
            if (node.isBoolean()) {
                value = node.booleanValue();
            } else {
                value = Boolean.parseBoolean(node.textValue());
            }
            return Optional.of(value);
        }
    }

    static class CategoryDeserializer extends StdDeserializer<Category> {

        public CategoryDeserializer() {
            this(null);
        }

        protected CategoryDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Category deserialize(
            JsonParser jsonParser,
            DeserializationContext ctxt
        ) throws IOException {
            String value = ((JsonNode) jsonParser.getCodec()
                .readTree(jsonParser))
                .textValue()
                .toUpperCase();
            Category category;
            try {
                category = Category.valueOf(value);
            } catch (IllegalArgumentException exception) {
                category = Category.MISC;
            }
            return category;
        }
    }
}

