package com.quiz.service;

import com.quiz.entities.Category;
import com.quiz.entities.Quiz;

import java.util.List;
import java.util.Set;

public interface QuizService {

    Quiz createQuiz(Quiz quiz);

    Quiz updateQuiz(Quiz quiz);

    Set<Quiz> getAllQuizzes();

    Quiz getQuizById(Long quizId);

    void deleteQuizById(Long quizId);

    List<Quiz> getQuizzesByCategory(Category category);

    List<Quiz> getAllActiveQuizzes();

    List<Quiz> getActiveQuizzesByCategory(Category category);
}
