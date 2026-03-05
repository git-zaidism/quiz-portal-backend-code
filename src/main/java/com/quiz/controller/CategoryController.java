package com.quiz.controller;

import com.quiz.dto.category.CategoryRequest;
import com.quiz.dto.category.CategoryResponse;
import com.quiz.mapper.CategoryMapper;
import com.quiz.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("Category creation requested title={}", request.title());
        return ResponseEntity.ok(this.categoryMapper.toResponse(
                this.categoryService.createCategory(this.categoryMapper.toEntity(request))
        ));
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse getCategoryById(@PathVariable("categoryId") Long categoryId) {
        log.debug("Fetching category by categoryId={}", categoryId);
        return this.categoryMapper.toResponse(this.categoryService.getCategoryById(categoryId));
    }

    @GetMapping
    public ResponseEntity<Set<CategoryResponse>> getAllCategories() {
        Set<CategoryResponse> categories = this.categoryService.getAllCategories().stream()
                .map(this.categoryMapper::toResponse)
                .collect(Collectors.toSet());
        log.debug("Fetched {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @PutMapping
    public CategoryResponse updateCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("Category update requested categoryId={}", request.categoryId());
        return this.categoryMapper.toResponse(this.categoryService.updateCategory(this.categoryMapper.toEntity(request)));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        log.info("Category deletion requested categoryId={}", categoryId);
        this.categoryService.deleteCategoryById(categoryId);
    }
}
