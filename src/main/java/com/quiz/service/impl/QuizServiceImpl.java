package com.quiz.service.impl;

import com.quiz.entities.Category;
import com.quiz.entities.Quiz;
import com.quiz.repositoy.QuizRepository;
import com.quiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    public Quiz createQuiz(Quiz quiz) {
        log.info("Creating quiz title={} categoryId={}", quiz.getTitle(),
                quiz.getCategory() != null ? quiz.getCategory().getId() : null);
        Quiz createdQuiz = this.quizRepository.save(quiz);
        log.info("Quiz created successfully quizId={}", createdQuiz.getId());
        return createdQuiz;
    }

    @Override
    public Quiz updateQuiz(Quiz quiz) {
        log.info("Updating quiz quizId={}", quiz.getId());
        Quiz updatedQuiz = this.quizRepository.save(quiz);
        log.info("Quiz updated successfully quizId={}", updatedQuiz.getId());
        return updatedQuiz;
    }

    @Override
    public Set<Quiz> getAllQuizzes() {
        Set<Quiz> quizzes = new HashSet<>(this.quizRepository.findAll());
        log.debug("Loaded {} quizzes", quizzes.size());
        return quizzes;
    }

    @Override
    public Quiz getQuizById(Long quizId) {
        log.debug("Loading quiz by quizId={}", quizId);
        return this.quizRepository.findById(quizId)
                .orElseThrow(() -> new NoSuchElementException("Quiz not found with id: " + quizId));
    }

    @Override
    public void deleteQuizById(Long quizId) {
        log.info("Deleting quiz quizId={}", quizId);
        if (!this.quizRepository.existsById(quizId)) {
            log.warn("Quiz deletion failed, quizId not found: {}", quizId);
            throw new NoSuchElementException("Quiz not found with id: " + quizId);
        }
        this.quizRepository.deleteById(quizId);
        log.info("Quiz deleted successfully quizId={}", quizId);
    }

    @Override
    public List<Quiz> getQuizzesByCategory(Category category) {
        List<Quiz> quizzes = this.quizRepository.findByCategory(category);
        log.debug("Loaded {} quizzes for categoryId={}", quizzes.size(), category.getId());
        return quizzes;
    }


    @Override
    public List<Quiz> getAllActiveQuizzes() {
        List<Quiz> activeQuizzes = this.quizRepository.findByActive(true);
        log.debug("Loaded {} active quizzes", activeQuizzes.size());
        return activeQuizzes;
    }

    @Override
    public List<Quiz> getActiveQuizzesByCategory(Category category) {
        List<Quiz> activeQuizzes = this.quizRepository.findByCategoryAndActive(category, true);
        log.debug("Loaded {} active quizzes for categoryId={}", activeQuizzes.size(), category.getId());
        return activeQuizzes;
    }
}
