package com.quiz.dto.question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuestionRequest(
        Long questionId,
        @NotBlank String content,
        String image,
        @NotBlank String option1,
        @NotBlank String option2,
        @NotBlank String option3,
        @NotBlank String option4,
        @NotBlank String answer,
        @NotNull Long quizId
) {
}
