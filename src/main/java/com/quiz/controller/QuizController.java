package com.quiz.controller;

import com.quiz.dto.quiz.QuizRequest;
import com.quiz.dto.quiz.QuizResponse;
import com.quiz.mapper.QuizMapper;
import com.quiz.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {

    private final QuizService quizService;
    private final QuizMapper quizMapper;

    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        log.info("Quiz creation requested title={} categoryId={}", request.title(), request.categoryId());
        return ResponseEntity.ok(this.quizMapper.toResponse(this.quizService.createQuiz(this.quizMapper.toEntity(request))));
    }

    @PutMapping
    public ResponseEntity<QuizResponse> updateQuiz(@Valid @RequestBody QuizRequest request) {
        log.info("Quiz update requested quizId={}", request.quizId());
        return ResponseEntity.ok(this.quizMapper.toResponse(this.quizService.updateQuiz(this.quizMapper.toEntity(request))));
    }

    @GetMapping
    public ResponseEntity<Set<QuizResponse>> getAllQuizzes() {
        Set<QuizResponse> quizzes = this.quizService.getAllQuizzes().stream()
                .map(this.quizMapper::toResponse)
                .collect(Collectors.toSet());
        log.debug("Fetched {} quizzes", quizzes.size());
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{quizId}")
    public QuizResponse getQuizById(@PathVariable("quizId") Long quizId) {
        log.debug("Fetching quiz by quizId={}", quizId);
        return this.quizMapper.toResponse(this.quizService.getQuizById(quizId));
    }

    @DeleteMapping("/{quizId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuizById(@PathVariable("quizId") Long quizId) {
        log.info("Quiz deletion requested quizId={}", quizId);
        this.quizService.deleteQuizById(quizId);
    }

    @GetMapping("/category/{categoryId}")
    public List<QuizResponse> getQuizzesByCategory(@PathVariable("categoryId") Long categoryId) {
        List<QuizResponse> quizzes = this.quizService.getQuizzesByCategory(this.quizMapper.toCategoryReference(categoryId)).stream()
                .map(this.quizMapper::toResponse)
                .toList();
        log.debug("Fetched {} quizzes for categoryId={}", quizzes.size(), categoryId);
        return quizzes;
    }

    @GetMapping("/active")
    public List<QuizResponse> getActiveQuizzes() {
        List<QuizResponse> quizzes = this.quizService.getAllActiveQuizzes().stream().map(this.quizMapper::toResponse).toList();
        log.debug("Fetched {} active quizzes", quizzes.size());
        return quizzes;
    }

    @GetMapping("/category/active/{categoryId}")
    public List<QuizResponse> getActiveQuizzesByCategory(@PathVariable("categoryId") Long categoryId) {
        List<QuizResponse> quizzes = this.quizService.getActiveQuizzesByCategory(this.quizMapper.toCategoryReference(categoryId)).stream()
                .map(this.quizMapper::toResponse)
                .toList();
        log.debug("Fetched {} active quizzes for categoryId={}", quizzes.size(), categoryId);
        return quizzes;
    }
}
