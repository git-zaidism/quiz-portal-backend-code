package com.exam.service;

import com.exam.dto.question.QuestionEvaluationRequest;
import com.exam.dto.question.QuizEvaluationResponse;
import com.exam.entities.Question;
import com.exam.entities.Quiz;

import java.util.List;
import java.util.Set;

public interface QuestionService {

    Question createQuestion(Question question);

    Question updateQuestion(Question question);

    Set<Question> getAllQuestions();

    Question getQuestionById(Long questionId);

    Set<Question> getQuestionsByQuiz(Quiz quiz);

    void deleteQuestionById(Long questionId);

    List<Question> getQuestionsForQuizAttempt(Quiz quiz);

    QuizEvaluationResponse evaluateQuiz(List<QuestionEvaluationRequest> submissions);

}
