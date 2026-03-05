package com.exam.controller;

import com.exam.dto.question.QuestionEvaluationRequest;
import com.exam.dto.question.QuestionPublicResponse;
import com.exam.dto.question.QuestionRequest;
import com.exam.dto.question.QuestionResponse;
import com.exam.dto.question.QuizEvaluationResponse;
import com.exam.mapper.QuestionMapper;
import com.exam.entities.Question;
import com.exam.entities.Quiz;
import com.exam.service.QuestionService;
import com.exam.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin("*")
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;
    private final QuestionMapper questionMapper;

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(this.questionMapper.toResponse(this.questionService.createQuestion(this.questionMapper.toEntity(request))));
    }

    @PutMapping
    public ResponseEntity<QuestionResponse> updateQuestion(@Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(this.questionMapper.toResponse(this.questionService.updateQuestion(this.questionMapper.toEntity(request))));
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionPublicResponse>> getQuestionsForQuizAttempt(@PathVariable("quizId") Long quizId) {
        Quiz quiz = this.quizService.getQuizById(quizId);
        List<Question> questions = this.questionService.getQuestionsForQuizAttempt(quiz);
        List<QuestionPublicResponse> questionResponses = questions.stream().map(this.questionMapper::toPublicResponse).toList();
        return ResponseEntity.ok(questionResponses);
    }

    @GetMapping("/quiz/all/{quizId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByQuizForAdmin(@PathVariable("quizId") Long quizId) {
        Set<Question> questions = this.questionService.getQuestionsByQuiz(this.questionMapper.toQuizReference(quizId));
        List<QuestionResponse> questionResponses = questions.stream().map(this.questionMapper::toResponse).toList();
        return ResponseEntity.ok(questionResponses);
    }

    @GetMapping("/{questionId}")
    public QuestionResponse getQuestionById(@PathVariable("questionId") Long questionId) {
        return this.questionMapper.toResponse(this.questionService.getQuestionById(questionId));
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestionById(@PathVariable("questionId") Long questionId) {
        this.questionService.deleteQuestionById(questionId);
    }

    @PostMapping("/eval-quiz")
    public ResponseEntity<QuizEvaluationResponse> evaluateQuiz(
            @Valid @RequestBody List<@Valid QuestionEvaluationRequest> evaluationRequests) {
        return ResponseEntity.ok(this.questionService.evaluateQuiz(evaluationRequests));
    }
}
