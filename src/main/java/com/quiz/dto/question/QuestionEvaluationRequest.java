package com.quiz.dto.question;

import jakarta.validation.constraints.NotNull;

public record QuestionEvaluationRequest(
        @NotNull Long questionId,
        String givenAnswer
) {
}
