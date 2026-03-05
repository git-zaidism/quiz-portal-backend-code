package com.exam.service.impl;

import com.exam.dto.question.QuestionEvaluationRequest;
import com.exam.dto.question.QuizEvaluationResponse;
import com.exam.entities.Question;
import com.exam.entities.Quiz;
import com.exam.repo.QuestionRepository;
import com.exam.service.QuestionService;
import lombok.RequiredArgsConstructor;
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
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Question createQuestion(Question question) {
        return this.questionRepository.save(question);
    }

    @Override
    public Question updateQuestion(Question question) {
        return this.questionRepository.save(question);
    }

    @Override
    public Set<Question> getAllQuestions() {
        return new HashSet<>(this.questionRepository.findAll());
    }

    @Override
    public Question getQuestionById(Long questionId) {
        return this.questionRepository.findById(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found with id: " + questionId));
    }

    @Override
    public Set<Question> getQuestionsByQuiz(Quiz quiz) {
        return this.questionRepository.findByQuiz(quiz);
    }

    @Override
    public void deleteQuestionById(Long questionId) {
        if (!this.questionRepository.existsById(questionId)) {
            throw new NoSuchElementException("Question not found with id: " + questionId);
        }
        this.questionRepository.deleteById(questionId);
    }

    @Override
    public List<Question> getQuestionsForQuizAttempt(Quiz quiz) {
        List<Question> questions = new ArrayList<>(quiz.getQuestions());
        int maxQuestionCount;
        try {
            maxQuestionCount = Integer.parseInt(quiz.getNumberOfQuestions());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Number of questions must be numeric.");
        }

        Collections.shuffle(questions);
        if (questions.size() <= maxQuestionCount) {
            return questions;
        }
        return questions.subList(0, maxQuestionCount);
    }

    @Override
    public QuizEvaluationResponse evaluateQuiz(List<QuestionEvaluationRequest> submissions) {
        if (submissions == null || submissions.isEmpty()) {
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

        return new QuizEvaluationResponse(marksObtained, correctAnswers, attempted);
    }
}
