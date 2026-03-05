package com.quiz.dto.question;

public record QuestionResponse(
        Long questionId,
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
