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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.quiz.entities.Category;
import com.quiz.entities.Quiz;
import com.quiz.repositoy.QuizRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuizServiceImpl Tests")
class QuizServiceImplTest {

    private QuizServiceImpl quizService;

    @Mock
    private QuizRepository quizRepository;

    private Quiz testQuiz;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        quizService = new QuizServiceImpl(quizRepository);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setTitle("Java");
        testCategory.setDescription("Java Programming");

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Java Basics");
        testQuiz.setDescription("Basic Java concepts");
        testQuiz.setActive(true);
        testQuiz.setCategory(testCategory);
    }

    @Test
    @DisplayName("Should create quiz and return it with correct id")
    void testCreateQuiz_Success() {
        // Arrange
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        // Act
        Quiz createdQuiz = quizService.createQuiz(testQuiz);

        // Assert
        assertNotNull(createdQuiz);
        assertEquals(1L, createdQuiz.getId());
        assertEquals("Java Basics", createdQuiz.getTitle());
        verify(quizRepository).save(testQuiz);
    }

    @Test
    @DisplayName("Should call repository save when creating quiz")
    void testCreateQuiz_CallsRepository() {
        // Arrange
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        // Act
        quizService.createQuiz(testQuiz);

        // Assert
        verify(quizRepository).save(testQuiz);
    }

    @Test
    @DisplayName("Should update quiz and return updated version")
    void testUpdateQuiz_Success() {
        // Arrange
        testQuiz.setTitle("Updated Java Basics");
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        // Act
        Quiz updatedQuiz = quizService.updateQuiz(testQuiz);

        // Assert
        assertNotNull(updatedQuiz);
        assertEquals("Updated Java Basics", updatedQuiz.getTitle());
        verify(quizRepository).save(testQuiz);
    }

    @Test
    @DisplayName("Should retrieve all quizzes as set")
    void testGetAllQuizzes_Success() {
        // Arrange
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("SQL Basics");
        List<Quiz> quizList = Arrays.asList(testQuiz, quiz2);

        when(quizRepository.findAll()).thenReturn(quizList);

        // Act
        Set<Quiz> result = quizService.getAllQuizzes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testQuiz));
        assertTrue(result.contains(quiz2));
        verify(quizRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty set when no quizzes exist")
    void testGetAllQuizzes_Empty() {
        // Arrange
        when(quizRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        Set<Quiz> result = quizService.getAllQuizzes();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get quiz by id successfully")
    void testGetQuizById_Success() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act
        Quiz foundQuiz = quizService.getQuizById(1L);

        // Assert
        assertNotNull(foundQuiz);
        assertEquals(1L, foundQuiz.getId());
        assertEquals("Java Basics", foundQuiz.getTitle());
        verify(quizRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when quiz id not found")
    void testGetQuizById_NotFound() {
        // Arrange
        when(quizRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> quizService.getQuizById(999L));
    }

    @Test
    @DisplayName("Should delete quiz by id successfully")
    void testDeleteQuizById_Success() {
        // Arrange
        when(quizRepository.existsById(1L)).thenReturn(true);

        // Act
        quizService.deleteQuizById(1L);

        // Assert
        verify(quizRepository).existsById(1L);
        verify(quizRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent quiz")
    void testDeleteQuizById_NotFound() {
        // Arrange
        when(quizRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> quizService.deleteQuizById(999L));
        verify(quizRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should get quizzes by category")
    void testGetQuizzesByCategory_Success() {
        // Arrange
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("Advanced Java");
        quiz2.setCategory(testCategory);

        List<Quiz> quizzes = Arrays.asList(testQuiz, quiz2);
        when(quizRepository.findByCategory(testCategory)).thenReturn(quizzes);

        // Act
        List<Quiz> result = quizService.getQuizzesByCategory(testCategory);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(quizRepository).findByCategory(testCategory);
    }

    @Test
    @DisplayName("Should return empty list when no quizzes for category")
    void testGetQuizzesByCategory_Empty() {
        // Arrange
        when(quizRepository.findByCategory(testCategory)).thenReturn(new ArrayList<>());

        // Act
        List<Quiz> result = quizService.getQuizzesByCategory(testCategory);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get all active quizzes")
    void testGetAllActiveQuizzes_Success() {
        // Arrange
        Quiz inactiveQuiz = new Quiz();
        inactiveQuiz.setId(2L);
        inactiveQuiz.setTitle("Inactive Quiz");
        inactiveQuiz.setActive(false);

        List<Quiz> activeQuizzes = List.of(testQuiz);
        when(quizRepository.findByActive(true)).thenReturn(activeQuizzes);

        // Act
        List<Quiz> result = quizService.getAllActiveQuizzes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(quizRepository).findByActive(true);
    }

    @Test
    @DisplayName("Should return empty list when no active quizzes")
    void testGetAllActiveQuizzes_NoActive() {
        // Arrange
        when(quizRepository.findByActive(true)).thenReturn(new ArrayList<>());

        // Act
        List<Quiz> result = quizService.getAllActiveQuizzes();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get active quizzes by category")
    void testGetActiveQuizzesByCategory_Success() {
        // Arrange
        List<Quiz> activeQuizzes = List.of(testQuiz);
        when(quizRepository.findByCategoryAndActive(testCategory, true))
                .thenReturn(activeQuizzes);

        // Act
        List<Quiz> result = quizService.getActiveQuizzesByCategory(testCategory);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Java Basics", result.get(0).getTitle());
        verify(quizRepository).findByCategoryAndActive(testCategory, true);
    }

    @Test
    @DisplayName("Should return empty list when no active quizzes in category")
    void testGetActiveQuizzesByCategory_NoActive() {
        // Arrange
        when(quizRepository.findByCategoryAndActive(testCategory, true))
                .thenReturn(new ArrayList<>());

        // Act
        List<Quiz> result = quizService.getActiveQuizzesByCategory(testCategory);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should preserve quiz properties during create")
    void testCreateQuiz_PreservesProperties() {
        // Arrange
        testQuiz.setNumberOfQuestions("10");
        testQuiz.setMaxMarks("100");
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        // Act
        Quiz createdQuiz = quizService.createQuiz(testQuiz);

        // Assert
        assertEquals("10", createdQuiz.getNumberOfQuestions());
        assertEquals("100", createdQuiz.getMaxMarks());
    }

    @Test
    @DisplayName("Should get quiz with associated category")
    void testGetQuizById_WithCategory() {
        // Arrange
        when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));

        // Act
        Quiz foundQuiz = quizService.getQuizById(1L);

        // Assert
        assertNotNull(foundQuiz.getCategory());
        assertEquals(1L, foundQuiz.getCategory().getId());
    }

    @Test
    @DisplayName("Should handle create with null category")
    void testCreateQuiz_NullCategory() {
        // Arrange
        testQuiz.setCategory(null);
        when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);

        // Act
        Quiz createdQuiz = quizService.createQuiz(testQuiz);

        // Assert
        assertNotNull(createdQuiz);
        assertNull(createdQuiz.getCategory());
    }
}
