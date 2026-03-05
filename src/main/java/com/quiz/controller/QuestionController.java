package com.quiz.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.quiz.dto.question.QuestionEvaluationRequest;
import com.quiz.dto.question.QuestionPublicResponse;
import com.quiz.dto.question.QuestionRequest;
import com.quiz.dto.question.QuestionResponse;
import com.quiz.dto.question.QuizEvaluationResponse;
import com.quiz.entities.Question;
import com.quiz.entities.Quiz;
import com.quiz.mapper.QuestionMapper;
import com.quiz.service.QuestionService;
import com.quiz.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin("*")
@RequestMapping("/question")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Management", description = "APIs for managing quiz questions and evaluating quiz submissions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;
    private final QuestionMapper questionMapper;

    @PostMapping
    @Operation(summary = "Create Question", description = "Create a new question for a quiz")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
        log.info("Question creation requested for quizId={}", request.quizId());
        return ResponseEntity.ok(this.questionMapper.toResponse(this.questionService.createQuestion(this.questionMapper.toEntity(request))));
    }

    @PutMapping
    @Operation(summary = "Update Question", description = "Update an existing question")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public ResponseEntity<QuestionResponse> updateQuestion(@Valid @RequestBody QuestionRequest request) {
        log.info("Question update requested questionId={}", request.questionId());
        return ResponseEntity.ok(this.questionMapper.toResponse(this.questionService.updateQuestion(this.questionMapper.toEntity(request))));
    }

    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get Questions for Quiz Attempt", description = "Retrieve questions for a quiz without showing answers (for quiz taking)")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionPublicResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<List<QuestionPublicResponse>> getQuestionsForQuizAttempt(@PathVariable("quizId") Long quizId) {
        Quiz quiz = this.quizService.getQuizById(quizId);
        List<Question> questions = this.questionService.getQuestionsForQuizAttempt(quiz);
        List<QuestionPublicResponse> questionResponses = questions.stream().map(this.questionMapper::toPublicResponse).toList();
        log.debug("Prepared {} questions for quiz attempt quizId={}", questionResponses.size(), quizId);
        return ResponseEntity.ok(questionResponses);
    }

    @GetMapping("/quiz/all/{quizId}")
    @Operation(summary = "Get All Questions for Quiz (Admin)", description = "Retrieve all questions with answers for a quiz (admin only)")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<List<QuestionResponse>> getQuestionsByQuizForAdmin(@PathVariable("quizId") Long quizId) {
        Set<Question> questions = this.questionService.getQuestionsByQuiz(this.questionMapper.toQuizReference(quizId));
        List<QuestionResponse> questionResponses = questions.stream().map(this.questionMapper::toResponse).toList();
        log.debug("Fetched {} questions for admin quizId={}", questionResponses.size(), quizId);
        return ResponseEntity.ok(questionResponses);
    }

    @GetMapping("/{questionId}")
    @Operation(summary = "Get Question by ID", description = "Retrieve a specific question by ID")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Question retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuestionResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public QuestionResponse getQuestionById(@PathVariable("questionId") Long questionId) {
        log.debug("Fetching question by questionId={}", questionId);
        return this.questionMapper.toResponse(this.questionService.getQuestionById(questionId));
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Question", description = "Delete a question by ID")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Question deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Question not found")
    })
    public void deleteQuestionById(@PathVariable("questionId") Long questionId) {
        log.info("Question deletion requested questionId={}", questionId);
        this.questionService.deleteQuestionById(questionId);
    }

    @PostMapping("/eval-quiz")
    @Operation(summary = "Evaluate Quiz Submission", description = "Evaluate a user's quiz submission and calculate the score")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz evaluated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuizEvaluationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid submission data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<QuizEvaluationResponse> evaluateQuiz(
            @Valid @RequestBody List<@Valid QuestionEvaluationRequest> evaluationRequests) {
        log.info("Quiz evaluation requested with {} submissions", evaluationRequests.size());
        return ResponseEntity.ok(this.questionService.evaluateQuiz(evaluationRequests));
    }
}
