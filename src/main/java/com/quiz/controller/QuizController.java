package com.quiz.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.quiz.dto.quiz.QuizRequest;
import com.quiz.dto.quiz.QuizResponse;
import com.quiz.mapper.QuizMapper;
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
@RequestMapping("/quiz")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Quiz Management", description = "APIs for managing quizzes and retrieving quiz information")
public class QuizController {

    private final QuizService quizService;
    private final QuizMapper quizMapper;

    @PostMapping
    @Operation(summary = "Create Quiz", description = "Create a new quiz")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuizResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        log.info("Quiz creation requested title={} categoryId={}", request.title(), request.categoryId());
        return ResponseEntity.ok(this.quizMapper.toResponse(this.quizService.createQuiz(this.quizMapper.toEntity(request))));
    }

    @PutMapping
    @Operation(summary = "Update Quiz", description = "Update an existing quiz")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuizResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public ResponseEntity<QuizResponse> updateQuiz(@Valid @RequestBody QuizRequest request) {
        log.info("Quiz update requested quizId={}", request.quizId());
        return ResponseEntity.ok(this.quizMapper.toResponse(this.quizService.updateQuiz(this.quizMapper.toEntity(request))));
    }

    @GetMapping
    @Operation(summary = "Get All Quizzes", description = "Retrieve all quizzes in the system")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuizResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<Set<QuizResponse>> getAllQuizzes() {
        Set<QuizResponse> quizzes = this.quizService.getAllQuizzes().stream()
                .map(this.quizMapper::toResponse)
                .collect(Collectors.toSet());
        log.debug("Fetched {} quizzes", quizzes.size());
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "Get Quiz by ID", description = "Retrieve a specific quiz by ID")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quiz retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = QuizResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public QuizResponse getQuizById(@PathVariable("quizId") Long quizId) {
        log.debug("Fetching quiz by quizId={}", quizId);
        return this.quizMapper.toResponse(this.quizService.getQuizById(quizId));
    }

    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Quiz", description = "Delete a quiz by ID")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Quiz deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Quiz not found")
    })
    public void deleteQuizById(@PathVariable("quizId") Long quizId) {
        log.info("Quiz deletion requested quizId={}", quizId);
        this.quizService.deleteQuizById(quizId);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get Quizzes by Category", description = "Retrieve all quizzes for a specific category")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public List<QuizResponse> getQuizzesByCategory(@PathVariable("categoryId") Long categoryId) {
        List<QuizResponse> quizzes = this.quizService.getQuizzesByCategory(this.quizMapper.toCategoryReference(categoryId)).stream()
                .map(this.quizMapper::toResponse)
                .toList();
        log.debug("Fetched {} quizzes for categoryId={}", quizzes.size(), categoryId);
        return quizzes;
    }

    @GetMapping("/active")
    @Operation(summary = "Get Active Quizzes", description = "Retrieve all active/published quizzes")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active quizzes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public List<QuizResponse> getActiveQuizzes() {
        List<QuizResponse> quizzes = this.quizService.getAllActiveQuizzes().stream().map(this.quizMapper::toResponse).toList();
        log.debug("Fetched {} active quizzes", quizzes.size());
        return quizzes;
    }

    @GetMapping("/category/active/{categoryId}")
    @Operation(summary = "Get Active Quizzes by Category", description = "Retrieve all active/published quizzes for a specific category")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active quizzes retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public List<QuizResponse> getActiveQuizzesByCategory(@PathVariable("categoryId") Long categoryId) {
        List<QuizResponse> quizzes = this.quizService.getActiveQuizzesByCategory(this.quizMapper.toCategoryReference(categoryId)).stream()
                .map(this.quizMapper::toResponse)
                .toList();
        log.debug("Fetched {} active quizzes for categoryId={}", quizzes.size(), categoryId);
        return quizzes;
    }
}
