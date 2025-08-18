package fr.insee.queen.application.interrogation.dto.output;

import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;

@Getter
public enum QuestionnaireContextDto {
    HOUSEHOLD("household"),
    BUSINESS("business");

    @JsonValue
    @NotNull
    private final String label;

    QuestionnaireContextDto(String label) {
        this.label = label;
    }

    public static QuestionnaireContextDto getQuestionnaireContext(@NonNull String label) {
        return Arrays.stream(QuestionnaireContextDto.values())
                .filter(questionnaireContext -> questionnaireContext.getLabel().equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("incorrect context"));
    }
}
