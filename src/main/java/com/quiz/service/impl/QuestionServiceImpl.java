package com.quiz.service.impl;

import com.quiz.dto.question.QuestionEvaluationRequest;
import com.quiz.dto.question.QuizEvaluationResponse;
import com.quiz.entities.Question;
import com.quiz.entities.Quiz;
import com.quiz.repositoy.QuestionRepository;
import com.quiz.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Question createQuestion(Question question) {
        log.info("Creating question for quizId={}",
                question.getQuiz() != null ? question.getQuiz().getId() : null);
        Question createdQuestion = this.questionRepository.save(question);
        log.info("Question created successfully questionId={}", createdQuestion.getId());
        return createdQuestion;
    }

    @Override
    public Question updateQuestion(Question question) {
        log.info("Updating question questionId={}", question.getId());
        Question updatedQuestion = this.questionRepository.save(question);
        log.info("Question updated successfully questionId={}", updatedQuestion.getId());
        return updatedQuestion;
    }

    @Override
    public Set<Question> getAllQuestions() {
        Set<Question> questions = new HashSet<>(this.questionRepository.findAll());
        log.debug("Loaded {} questions", questions.size());
        return questions;
    }

    @Override
    public Question getQuestionById(Long questionId) {
        log.debug("Loading question by questionId={}", questionId);
        return this.questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found with id: " + questionId));
    }

    @Override
    public Set<Question> getQuestionsByQuiz(Quiz quiz) {
        Set<Question> questions = this.questionRepository.findByQuiz(quiz);
        log.debug("Loaded {} questions for quizId={}", questions.size(), quiz.getId());
        return questions;
    }

    @Override
    public void deleteQuestionById(Long questionId) {
        log.info("Deleting question questionId={}", questionId);
        if (!this.questionRepository.existsById(questionId)) {
            log.warn("Question deletion failed, questionId not found: {}", questionId);
            throw new NoSuchElementException("Question not found with id: " + questionId);
        }
        this.questionRepository.deleteById(questionId);
        log.info("Question deleted successfully questionId={}", questionId);
    }

    @Override
    public List<Question> getQuestionsForQuizAttempt(Quiz quiz) {
        log.debug("Preparing randomized questions for quizId={}", quiz.getId());
        List<Question> questions = new ArrayList<>(quiz.getQuestions());
        int maxQuestionCount;
        try {
            maxQuestionCount = Integer.parseInt(quiz.getNumberOfQuestions());
        } catch (NumberFormatException exception) {
            log.warn("Invalid numberOfQuestions for quizId={} value={}", quiz.getId(), quiz.getNumberOfQuestions());
            throw new IllegalArgumentException("Number of questions must be numeric.");
        }

        Collections.shuffle(questions);
        if (questions.size() <= maxQuestionCount) {
            log.debug("Returning all {} questions for quizId={}", questions.size(), quiz.getId());
            return questions;
        }
        List<Question> selectedQuestions = questions.subList(0, maxQuestionCount);
        log.debug("Returning {} out of {} questions for quizId={}",
                selectedQuestions.size(), questions.size(), quiz.getId());
        return selectedQuestions;
    }

    @Override
    public QuizEvaluationResponse evaluateQuiz(List<QuestionEvaluationRequest> submissions) {
        log.info("Evaluating quiz with {} submissions", submissions == null ? 0 : submissions.size());
        if (submissions == null || submissions.isEmpty()) {
            log.debug("Evaluation request has no submissions");
            return new QuizEvaluationResponse(0, 0, 0);
        }

        double marksObtained = 0;
        int correctAnswers = 0;
        int attempted = 0;

        Question firstQuestion = this.getQuestionById(submissions.get(0).questionId());
        double maxMarks;
        try {
            maxMarks = Double.parseDouble(firstQuestion.getQuiz().getMaxMarks());
        } catch (NumberFormatException exception) {
            log.warn("Invalid maxMarks configured for quizId={} value={}",
                    firstQuestion.getQuiz().getId(), firstQuestion.getQuiz().getMaxMarks());
            throw new IllegalArgumentException("Quiz max marks must be numeric.");
        }
        double marksPerQuestion = maxMarks / submissions.size();

        for (QuestionEvaluationRequest submission : submissions) {
            Question question = this.getQuestionById(submission.questionId());

            if (submission.givenAnswer() != null && !submission.givenAnswer().isBlank()) {
                attempted++;
            }

            if (Objects.equals(question.getAnswer(), submission.givenAnswer())) {
                correctAnswers++;
                marksObtained += marksPerQuestion;
            }
        }

        log.info("Quiz evaluation completed: marksObtained={}, correctAnswers={}, attempted={}",
                marksObtained, correctAnswers, attempted);
        return new QuizEvaluationResponse(marksObtained, correctAnswers, attempted);
    }
}
