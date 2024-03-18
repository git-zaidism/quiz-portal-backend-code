package com.quiz.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuizDTO {
    private Long qId;
    private String title;
    private String description;
    private String maxMarks;
    private String numberOfQuestions;
    private boolean active;
    private Long categoryId; // Added to reference the parent Category
}
