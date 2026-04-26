package com.quiz.mapper;

import com.quiz.dto.quiz.QuizRequest;
import com.quiz.dto.quiz.QuizResponse;
import com.quiz.entities.Category;
import com.quiz.entities.Quiz;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {

    public Quiz toEntity(QuizRequest request) {
        Quiz quiz = new Quiz();
        if (request.quizId() != null && request.quizId() != 0) {
            quiz.setId(request.quizId());
        }
        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setMaxMarks(request.maxMarks());
        quiz.setNumberOfQuestions(request.numberOfQuestions());
        quiz.setActive(request.active());
        quiz.setCategory(toCategoryReference(request.categoryId()));
        return quiz;
    }

    public Category toCategoryReference(Long categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }

    public QuizResponse toResponse(Quiz quiz) {
        Long categoryId = quiz.getCategory() != null ? quiz.getCategory().getId() : null;
        String categoryTitle = quiz.getCategory() != null ? quiz.getCategory().getTitle() : null;
        return new QuizResponse(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getMaxMarks(),
                quiz.getNumberOfQuestions(),
                quiz.isActive(),
                categoryId,
                categoryTitle
        );
    }
}
