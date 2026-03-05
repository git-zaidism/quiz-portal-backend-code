package com.exam.service.impl;

import com.exam.entities.Category;
import com.exam.repo.CategoryRepository;
import com.exam.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public Set<Category> getAllCategories() {
        return new LinkedHashSet<>(this.categoryRepository.findAll());
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + categoryId));
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        if (!this.categoryRepository.existsById(categoryId)) {
            throw new NoSuchElementException("Category not found with id: " + categoryId);
        }
        this.categoryRepository.deleteById(categoryId);
    }
}
