package com.quiz.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuizRequest(
        Long quizId,
        @NotBlank String title,
        String description,
        @NotBlank String maxMarks,
        @NotBlank String numberOfQuestions,
        boolean active,
        @NotNull Long categoryId
) {
}
