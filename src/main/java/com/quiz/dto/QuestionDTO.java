package com.quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionDTO {
    private Long quesId;
    private String content;
    private String image;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String answer;
    private String givenAnswer;
    private Long quizId; // Added to reference the parent Quiz
}
