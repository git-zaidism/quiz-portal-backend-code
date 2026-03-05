package com.exam.dto.question;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record QuestionEvaluationRequest(
        @NotNull @JsonProperty("quesId") @JsonAlias("questionId") Long questionId,
        String givenAnswer
) {
}
