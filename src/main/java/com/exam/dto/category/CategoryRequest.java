package com.exam.dto.category;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @JsonProperty("cid") @JsonAlias("categoryId") Long categoryId,
        @NotBlank String title,
        String description
) {
}
