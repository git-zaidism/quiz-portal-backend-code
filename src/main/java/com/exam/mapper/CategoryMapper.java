package com.exam.mapper;

import com.exam.dto.category.CategoryRequest;
import com.exam.dto.category.CategoryResponse;
import com.exam.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        Category category = new Category();
        category.setId(request.categoryId());
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
