package com.quiz.dto.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryResponse(
        @JsonProperty("cid") Long categoryId,
        String title,
        String description
) {
}
