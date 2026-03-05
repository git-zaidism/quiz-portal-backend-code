package com.quiz.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        Long categoryId,
        @NotBlank String title,
        String description
) {
}
