package com.quiz.mapper;

import com.quiz.dto.category.CategoryRequest;
import com.quiz.dto.category.CategoryResponse;
import com.quiz.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        Category category = new Category();
        if (request.categoryId() != null && request.categoryId() != 0) {
            category.setId(request.categoryId());
        }
        category.setTitle(request.title());
        category.setDescription(request.description());
        return category;
    }

    public CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getTitle(),
                category.getDescription()
        );
    }
}
