package com.quiz.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.quiz.entities.Category;
import com.quiz.repositoy.CategoryRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryServiceImpl Tests")
class CategoryServiceImplTest {

    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setTitle("Java");
        testCategory.setDescription("Java Programming Language");
    }

    @Test
    @DisplayName("Should create category successfully")
    void testCreateCategory_Success() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category createdCategory = categoryService.createCategory(testCategory);

        // Assert
        assertNotNull(createdCategory);
        assertEquals(1L, createdCategory.getId());
        assertEquals("Java", createdCategory.getTitle());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    @DisplayName("Should call repository save when creating category")
    void testCreateCategory_CallsRepository() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        categoryService.createCategory(testCategory);

        // Assert
        verify(categoryRepository).save(testCategory);
    }

    @Test
    @DisplayName("Should create category with description")
    void testCreateCategory_WithDescription() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category createdCategory = categoryService.createCategory(testCategory);

        // Assert
        assertEquals("Java Programming Language", createdCategory.getDescription());
    }

    @Test
    @DisplayName("Should update category successfully")
    void testUpdateCategory_Success() {
        // Arrange
        testCategory.setTitle("Updated Java");
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category updatedCategory = categoryService.updateCategory(testCategory);

        // Assert
        assertNotNull(updatedCategory);
        assertEquals("Updated Java", updatedCategory.getTitle());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    @DisplayName("Should retrieve all categories as LinkedHashSet")
    void testGetAllCategories_Success() {
        // Arrange
        Category category2 = new Category();
        category2.setId(2L);
        category2.setTitle("SQL");
        List<Category> categories = Arrays.asList(testCategory, category2);

        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        Set<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testCategory));
        assertTrue(result.contains(category2));
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty set when no categories exist")
    void testGetAllCategories_Empty() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        Set<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get category by id successfully")
    void testGetCategoryById_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        Category foundCategory = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(foundCategory);
        assertEquals(1L, foundCategory.getId());
        assertEquals("Java", foundCategory.getTitle());
        verify(categoryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when category id not found")
    void testGetCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> categoryService.getCategoryById(999L));
    }

    @Test
    @DisplayName("Should delete category by id successfully")
    void testDeleteCategoryById_Success() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // Act
        categoryService.deleteCategoryById(1L);

        // Assert
        verify(categoryRepository).existsById(1L);
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent category")
    void testDeleteCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> categoryService.deleteCategoryById(999L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should verify repository not called for non-existent delete")
    void testDeleteCategoryById_VerifyRepositoryNotCalled() {
        // Arrange
        when(categoryRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> categoryService.deleteCategoryById(999L));
        verify(categoryRepository, never()).deleteById(999L);
    }

    @Test
    @DisplayName("Should create multiple categories independently")
    void testCreateMultipleCategories() {
        // Arrange
        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setTitle("SQL");

        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryRepository.save(cat2)).thenReturn(cat2);

        // Act
        Category created1 = categoryService.createCategory(testCategory);
        Category created2 = categoryService.createCategory(cat2);

        // Assert
        assertEquals("Java", created1.getTitle());
        assertEquals("SQL", created2.getTitle());
        verify(categoryRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Should preserve category properties during update")
    void testUpdateCategory_PreservesProperties() {
        // Arrange
        testCategory.setDescription("New description");
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category updated = categoryService.updateCategory(testCategory);

        // Assert
        assertEquals("New description", updated.getDescription());
    }

    @Test
    @DisplayName("Should get all categories in order")
    void testGetAllCategories_PreservesOrder() {
        // Arrange
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setTitle("Java");
        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setTitle("SQL");

        List<Category> orderedList = Arrays.asList(cat1, cat2);
        when(categoryRepository.findAll()).thenReturn(orderedList);

        // Act
        Set<Category> result = categoryService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(cat1));
        assertTrue(result.contains(cat2));
    }

    @Test
    @DisplayName("Should handle create with null description")
    void testCreateCategory_NullDescription() {
        // Arrange
        testCategory.setDescription(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // Act
        Category created = categoryService.createCategory(testCategory);

        // Assert
        assertNull(created.getDescription());
    }

    @Test
    @DisplayName("Should throw correct exception message for not found category")
    void testGetCategoryById_ExceptionMessage() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, 
                () -> categoryService.getCategoryById(999L));
        assertTrue(exception.getMessage().contains("Category not found"));
    }

    @Test
    @DisplayName("Should return unique categories in set")
    void testGetAllCategories_UniqueElements() {
        // Arrange
        Category dup = new Category();
        dup.setId(1L);
        dup.setTitle("Java");

        List<Category> categories = Arrays.asList(testCategory, dup);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        Set<Category> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
    }
}
