package com.quiz.service.impl;

import com.quiz.entities.Category;
import com.quiz.repositoy.CategoryRepository;
import com.quiz.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public Category createCategory(Category category) {
        log.info("Creating category title={}", category.getTitle());
        Category createdCategory = this.categoryRepository.save(category);
        log.info("Category created successfully categoryId={}", createdCategory.getId());
        return createdCategory;
    }

    @Override
    public Category updateCategory(Category category) {
        log.info("Updating category categoryId={}", category.getId());
        Category updatedCategory = this.categoryRepository.save(category);
        log.info("Category updated successfully categoryId={}", updatedCategory.getId());
        return updatedCategory;
    }

    @Override
    public Set<Category> getAllCategories() {
        Set<Category> categories = new LinkedHashSet<>(this.categoryRepository.findAll());
        log.debug("Loaded {} categories", categories.size());
        return categories;
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        log.debug("Loading category by categoryId={}", categoryId);
        return this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Category not found with id: " + categoryId));
    }

    @Override
    public void deleteCategoryById(Long categoryId) {
        log.info("Deleting category categoryId={}", categoryId);
        if (!this.categoryRepository.existsById(categoryId)) {
            log.warn("Category deletion failed, categoryId not found: {}", categoryId);
            throw new NoSuchElementException("Category not found with id: " + categoryId);
        }
        this.categoryRepository.deleteById(categoryId);
        log.info("Category deleted successfully categoryId={}", categoryId);
    }
}
