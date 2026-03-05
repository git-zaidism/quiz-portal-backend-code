package com.quiz.service;

import com.quiz.entities.Category;

import java.util.Set;

public interface CategoryService {
    Category createCategory(Category category);

    Category updateCategory(Category category);

    Set<Category> getAllCategories();

    Category getCategoryById(Long categoryId);

    void deleteCategoryById(Long categoryId);
}
