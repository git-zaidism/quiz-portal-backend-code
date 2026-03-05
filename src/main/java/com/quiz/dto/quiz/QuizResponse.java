package com.quiz.dto.quiz;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuizResponse(
        @JsonProperty("qId") Long quizId,
        String title,
        String description,
        String maxMarks,
        String numberOfQuestions,
        boolean active,
        Long categoryId,
        String categoryTitle
) {
}
