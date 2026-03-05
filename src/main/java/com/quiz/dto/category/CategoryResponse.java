package com.quiz.dto.category;

public record CategoryResponse(
        Long categoryId,
        String title,
        String description
) {
}
