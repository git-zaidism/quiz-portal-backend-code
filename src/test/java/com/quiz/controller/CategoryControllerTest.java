package com.quiz.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.quiz.dto.category.CategoryRequest;
import com.quiz.dto.category.CategoryResponse;
import com.quiz.entities.Category;
import com.quiz.mapper.CategoryMapper;
import com.quiz.service.CategoryService;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController Tests")
class CategoryControllerTest {

    private CategoryController controller;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryMapper categoryMapper;

    private Category testCategory;
    private CategoryResponse testCategoryResponse;
    private CategoryRequest testCategoryRequest;

    @BeforeEach
    void setUp() {
        controller = new CategoryController(categoryService, categoryMapper);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setTitle("Java");
        testCategory.setDescription("Java Programming Language");

        testCategoryResponse = new CategoryResponse(
                1L,
                "Java",
                "Java Programming Language"
        );

        testCategoryRequest = new CategoryRequest(
                1L,
                "Java",
                "Java Programming Language"
        );
    }

    @Test
    @DisplayName("Should create category and return with HTTP 200")
    void testCreateCategory_Success() {
        // Arrange
        when(categoryMapper.toEntity(testCategoryRequest)).thenReturn(testCategory);
        when(categoryService.createCategory(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        ResponseEntity<CategoryResponse> response = controller.createCategory(testCategoryRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java", response.getBody().title());
        verify(categoryMapper).toEntity(testCategoryRequest);
        verify(categoryService).createCategory(testCategory);
    }

    @Test
    @DisplayName("Should map request to entity before creating")
    void testCreateCategory_MapsRequest() {
        // Arrange
        when(categoryMapper.toEntity(testCategoryRequest)).thenReturn(testCategory);
        when(categoryService.createCategory(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        controller.createCategory(testCategoryRequest);

        // Assert
        verify(categoryMapper).toEntity(testCategoryRequest);
    }

    @Test
    @DisplayName("Should get category by id successfully")
    void testGetCategoryById_Success() {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        CategoryResponse response = controller.getCategoryById(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Java", response.title());
        verify(categoryService).getCategoryById(1L);
    }

    @Test
    @DisplayName("Should get all categories successfully")
    void testGetAllCategories_Success() {
        // Arrange
        Category category2 = new Category();
        category2.setId(2L);
        category2.setTitle("SQL");
        Set<Category> categories = new HashSet<>(Arrays.asList(testCategory, category2));

        CategoryResponse response2 = new CategoryResponse(2L, "SQL", "SQL Database");

        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);
        when(categoryMapper.toResponse(category2)).thenReturn(response2);

        // Act
        ResponseEntity<Set<CategoryResponse>> response = controller.getAllCategories();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(categoryService).getAllCategories();
    }

    @Test
    @DisplayName("Should return empty set when no categories exist")
    void testGetAllCategories_Empty() {
        // Arrange
        when(categoryService.getAllCategories()).thenReturn(new HashSet<>());

        // Act
        ResponseEntity<Set<CategoryResponse>> response = controller.getAllCategories();

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Should update category successfully")
    void testUpdateCategory_Success() {
        // Arrange
        testCategory.setTitle("Updated Java");
        when(categoryMapper.toEntity(testCategoryRequest)).thenReturn(testCategory);
        when(categoryService.updateCategory(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        CategoryResponse response = controller.updateCategory(testCategoryRequest);

        // Assert
        assertNotNull(response);
        verify(categoryService).updateCategory(testCategory);
    }

    @Test
    @DisplayName("Should delete category by id successfully")
    void testDeleteCategoryById_Success() {
        // Act
        controller.deleteCategoryById(1L);

        // Assert
        verify(categoryService).deleteCategoryById(1L);
    }

    @Test
    @DisplayName("Should throw exception when category not found on retrieval")
    void testGetCategoryById_NotFound() {
        // Arrange
        when(categoryService.getCategoryById(999L))
                .thenThrow(new NoSuchElementException("Category not found"));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> controller.getCategoryById(999L));
    }

    @Test
    @DisplayName("Should throw exception when category not found on deletion")
    void testDeleteCategoryById_NotFound() {
        // Arrange
        doThrow(new NoSuchElementException("Category not found"))
                .when(categoryService).deleteCategoryById(999L);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> controller.deleteCategoryById(999L));
    }

    @Test
    @DisplayName("Should map category response correctly")
    void testGetCategoryById_MapsResponse() {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        controller.getCategoryById(1L);

        // Assert
        verify(categoryMapper).toResponse(testCategory);
    }

    @Test
    @DisplayName("Should create category response with id")
    void testCreateCategory_ResponseHasId() {
        // Arrange
        when(categoryMapper.toEntity(testCategoryRequest)).thenReturn(testCategory);
        when(categoryService.createCategory(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        ResponseEntity<CategoryResponse> response = controller.createCategory(testCategoryRequest);

        // Assert
        assertEquals(1L, response.getBody().categoryId());
    }

    @Test
    @DisplayName("Should create category with correct title")
    void testCreateCategory_CorrectTitle() {
        // Arrange
        when(categoryMapper.toEntity(testCategoryRequest)).thenReturn(testCategory);
        when(categoryService.createCategory(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        ResponseEntity<CategoryResponse> response = controller.createCategory(testCategoryRequest);

        // Assert
        assertEquals("Java", response.getBody().title());
    }

    @Test
    @DisplayName("Should update category with new title")
    void testUpdateCategory_NewTitle() {
        // Arrange
        testCategoryRequest = new CategoryRequest(1L, "Python", "Python Programming");
        testCategory.setTitle("Python");
        testCategoryResponse = new CategoryResponse(1L, "Python", "Python Programming");

        when(categoryMapper.toEntity(testCategoryRequest)).thenReturn(testCategory);
        when(categoryService.updateCategory(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        CategoryResponse response = controller.updateCategory(testCategoryRequest);

        // Assert
        assertEquals("Python", response.title());
    }

    @Test
    @DisplayName("Should get all categories with multiple items")
    void testGetAllCategories_MultipleItems() {
        // Arrange
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setTitle("Java");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setTitle("Python");

        Category cat3 = new Category();
        cat3.setId(3L);
        cat3.setTitle("SQL");

        Set<Category> categories = new HashSet<>(Arrays.asList(cat1, cat2, cat3));
        when(categoryService.getAllCategories()).thenReturn(categories);

        CategoryResponse resp1 = new CategoryResponse(1L, "Java", "desc");
        CategoryResponse resp2 = new CategoryResponse(2L, "Python", "desc");
        CategoryResponse resp3 = new CategoryResponse(3L, "SQL", "desc");

        when(categoryMapper.toResponse(cat1)).thenReturn(resp1);
        when(categoryMapper.toResponse(cat2)).thenReturn(resp2);
        when(categoryMapper.toResponse(cat3)).thenReturn(resp3);

        // Act
        ResponseEntity<Set<CategoryResponse>> response = controller.getAllCategories();

        // Assert
        assertEquals(3, response.getBody().size());
    }

    @Test
    @DisplayName("Should handle delete multiple times independently")
    void testDeleteCategoryById_MultipleDeletions() {
        // Act
        controller.deleteCategoryById(1L);
        controller.deleteCategoryById(2L);

        // Assert
        verify(categoryService, times(2)).deleteCategoryById(anyLong());
        verify(categoryService).deleteCategoryById(1L);
        verify(categoryService).deleteCategoryById(2L);
    }

    @Test
    @DisplayName("Should preserve category properties in response")
    void testGetCategoryById_PreservesProperties() {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(testCategory);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        CategoryResponse response = controller.getCategoryById(1L);

        // Assert
        assertEquals("Java Programming Language", response.description());
    }

    @Test
    @DisplayName("Should map all categories in set")
    void testGetAllCategories_MapsAllCategories() {
        // Arrange
        Set<Category> categories = Set.of(testCategory);
        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryMapper.toResponse(testCategory)).thenReturn(testCategoryResponse);

        // Act
        controller.getAllCategories();

        // Assert
        verify(categoryMapper).toResponse(testCategory);
    }
}
