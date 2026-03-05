package com.exam.controller;

import com.exam.dto.category.CategoryRequest;
import com.exam.dto.category.CategoryResponse;
import com.exam.mapper.CategoryMapper;
import com.exam.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/category")
@CrossOrigin("*")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(this.categoryMapper.toResponse(
                this.categoryService.createCategory(this.categoryMapper.toEntity(request))
        ));
    }

    @GetMapping("/{categoryId}")
    public CategoryResponse getCategoryById(@PathVariable("categoryId") Long categoryId) {
        return this.categoryMapper.toResponse(this.categoryService.getCategoryById(categoryId));
    }

    @GetMapping
    public ResponseEntity<Set<CategoryResponse>> getAllCategories() {
        Set<CategoryResponse> categories = this.categoryService.getAllCategories().stream()
                .map(this.categoryMapper::toResponse)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(categories);
    }

    @PutMapping
    public CategoryResponse updateCategory(@Valid @RequestBody CategoryRequest request) {
        return this.categoryMapper.toResponse(this.categoryService.updateCategory(this.categoryMapper.toEntity(request)));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        this.categoryService.deleteCategoryById(categoryId);
    }
}
