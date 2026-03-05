package com.quiz.service.impl;

import com.quiz.entities.Category;
import com.quiz.entities.Quiz;
import com.quiz.repositoy.QuizRepository;
import com.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    public Quiz createQuiz(Quiz quiz) {
        return this.quizRepository.save(quiz);
    }

    @Override
    public Quiz updateQuiz(Quiz quiz) {
        return this.quizRepository.save(quiz);
    }

    @Override
    public Set<Quiz> getAllQuizzes() {
        return new HashSet<>(this.quizRepository.findAll());
    }

    @Override
    public Quiz getQuizById(Long quizId) {
        return this.quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found with id: " + quizId));
    }

    @Override
    public void deleteQuizById(Long quizId) {
        if (!this.quizRepository.existsById(quizId)) {
            throw new NoSuchElementException("Quiz not found with id: " + quizId);
        }
        this.quizRepository.deleteById(quizId);
    }

    @Override
    public List<Quiz> getQuizzesByCategory(Category category) {
        return this.quizRepository.findByCategory(category);
    }


    @Override
    public List<Quiz> getAllActiveQuizzes() {
        return this.quizRepository.findByActive(true);
    }

    @Override
    public List<Quiz> getActiveQuizzesByCategory(Category category) {
        return this.quizRepository.findByCategoryAndActive(category, true);
    }
}
