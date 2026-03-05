package com.exam.dto.question;

public record QuizEvaluationResponse(
        double marksGot,
        int correctAnswers,
        int attempted
) {
}
