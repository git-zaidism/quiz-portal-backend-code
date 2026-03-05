package com.quiz.dto.quiz;

public record QuizResponse(
        Long quizId,
        String title,
        String description,
        String maxMarks,
        String numberOfQuestions,
        boolean active,
        Long categoryId,
        String categoryTitle
) {
}
