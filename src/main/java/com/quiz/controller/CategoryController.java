package com.quiz.controller;

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

import com.quiz.dto.category.CategoryRequest;
import com.quiz.dto.category.CategoryResponse;
import com.quiz.mapper.CategoryMapper;
import com.quiz.service.CategoryService;

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
@RequestMapping("/category")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Category Management", description = "APIs for managing quiz categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @Operation(summary = "Create Category", description = "Create a new quiz category")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("Category creation requested title={}", request.title());
        return ResponseEntity.ok(this.categoryMapper.toResponse(
                this.categoryService.createCategory(this.categoryMapper.toEntity(request))
        ));
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get Category by ID", description = "Retrieve category details by category ID")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public CategoryResponse getCategoryById(@PathVariable("categoryId") Long categoryId) {
        log.debug("Fetching category by categoryId={}", categoryId);
        return this.categoryMapper.toResponse(this.categoryService.getCategoryById(categoryId));
    }

    @GetMapping
    @Operation(summary = "Get All Categories", description = "Retrieve all available quiz categories")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    public ResponseEntity<Set<CategoryResponse>> getAllCategories() {
        Set<CategoryResponse> categories = this.categoryService.getAllCategories().stream()
                .map(this.categoryMapper::toResponse)
                .collect(Collectors.toSet());
        log.debug("Fetched {} categories", categories.size());
        return ResponseEntity.ok(categories);
    }

    @PutMapping
    @Operation(summary = "Update Category", description = "Update an existing category")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public CategoryResponse updateCategory(@Valid @RequestBody CategoryRequest request) {
        log.info("Category update requested categoryId={}", request.categoryId());
        return this.categoryMapper.toResponse(this.categoryService.updateCategory(this.categoryMapper.toEntity(request)));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Category", description = "Delete a category by category ID")
    @SecurityRequirement(name = "bearer-jwt")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public void deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        log.info("Category deletion requested categoryId={}", categoryId);
        this.categoryService.deleteCategoryById(categoryId);
    }
}
