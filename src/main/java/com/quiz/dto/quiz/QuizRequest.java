package com.quiz.dto.quiz;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuizRequest(
        @JsonProperty("qId") @JsonAlias("quizId") Long quizId,
        @NotBlank String title,
        String description,
        @NotBlank String maxMarks,
        @NotBlank String numberOfQuestions,
        boolean active,
        @NotNull Long categoryId
) {
}
