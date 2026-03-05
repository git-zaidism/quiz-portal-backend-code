package com.exam.dto.question;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuestionResponse(
        @JsonProperty("quesId") Long questionId,
        String content,
        String image,
        String option1,
        String option2,
        String option3,
        String option4,
        String answer,
        String givenAnswer,
        Long quizId
) {
}
