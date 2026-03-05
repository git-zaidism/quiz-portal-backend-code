package com.quiz.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.quiz.dto.quiz.QuizRequest;
import com.quiz.dto.quiz.QuizResponse;
import com.quiz.entities.Category;
import com.quiz.entities.Quiz;
import com.quiz.mapper.QuizMapper;
import com.quiz.service.QuizService;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuizController Tests")
class QuizControllerTest {

    private QuizController controller;

    @Mock
    private QuizService quizService;

    @Mock
    private QuizMapper quizMapper;

    private Quiz testQuiz;
    private QuizResponse testQuizResponse;
    private QuizRequest testQuizRequest;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        controller = new QuizController(quizService, quizMapper);

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setTitle("Java");

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Java Basics");
        testQuiz.setDescription("Basic Java concepts");
        testQuiz.setActive(true);
        testQuiz.setCategory(testCategory);

        testQuizResponse = new QuizResponse(
                1L,
                "Java Basics",
                "Basic Java concepts",
                "100",
                "10",
                true,
                1L,
                "Java"
        );

        testQuizRequest = new QuizRequest(
                1L,
                "Java Basics",
                "Basic Java concepts",
                "10",
                "100",
                true,
                1L
        );
    }

    @Test
    @DisplayName("Should create quiz and return with HTTP 200")
    void testCreateQuiz_Success() {
        // Arrange
        when(quizMapper.toEntity(testQuizRequest)).thenReturn(testQuiz);
        when(quizService.createQuiz(testQuiz)).thenReturn(testQuiz);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        ResponseEntity<QuizResponse> response = controller.createQuiz(testQuizRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Java Basics", response.getBody().title());
        verify(quizMapper).toEntity(testQuizRequest);
        verify(quizService).createQuiz(testQuiz);
    }

    @Test
    @DisplayName("Should map request to entity before creating")
    void testCreateQuiz_MapsRequest() {
        // Arrange
        when(quizMapper.toEntity(testQuizRequest)).thenReturn(testQuiz);
        when(quizService.createQuiz(testQuiz)).thenReturn(testQuiz);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        controller.createQuiz(testQuizRequest);

        // Assert
        verify(quizMapper).toEntity(testQuizRequest);
    }

    @Test
    @DisplayName("Should update quiz and return with HTTP 200")
    void testUpdateQuiz_Success() {
        // Arrange
        testQuizRequest = new QuizRequest(1L, "Updated Title", "Updated desc", "50", "5", false, 1L);
        testQuiz.setTitle("Updated Title");
        when(quizMapper.toEntity(testQuizRequest)).thenReturn(testQuiz);
        when(quizService.updateQuiz(testQuiz)).thenReturn(testQuiz);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        ResponseEntity<QuizResponse> response = controller.updateQuiz(testQuizRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(quizService).updateQuiz(testQuiz);
    }

    @Test
    @DisplayName("Should get all quizzes and return as set")
    void testGetAllQuizzes_Success() {
        // Arrange
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("SQL Basics");
        Set<Quiz> quizzes = new HashSet<>(Arrays.asList(testQuiz, quiz2));

        QuizResponse response2 = new QuizResponse(2L, "SQL Basics", "desc", "50", "5", true, 1L, "SQL");

        when(quizService.getAllQuizzes()).thenReturn(quizzes);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);
        when(quizMapper.toResponse(quiz2)).thenReturn(response2);

        // Act
        ResponseEntity<Set<QuizResponse>> response = controller.getAllQuizzes();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(quizService).getAllQuizzes();
    }

    @Test
    @DisplayName("Should return empty set when no quizzes exist")
    void testGetAllQuizzes_Empty() {
        // Arrange
        when(quizService.getAllQuizzes()).thenReturn(new HashSet<>());

        // Act
        ResponseEntity<Set<QuizResponse>> response = controller.getAllQuizzes();

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Should get quiz by id successfully")
    void testGetQuizById_Success() {
        // Arrange
        when(quizService.getQuizById(1L)).thenReturn(testQuiz);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        QuizResponse response = controller.getQuizById(1L);

        // Assert
        assertNotNull(response);
        assertEquals("Java Basics", response.title());
        verify(quizService).getQuizById(1L);
    }

    @Test
    @DisplayName("Should delete quiz by id successfully")
    void testDeleteQuizById_Success() {
        // Act
        controller.deleteQuizById(1L);

        // Assert
        verify(quizService).deleteQuizById(1L);
    }

    @Test
    @DisplayName("Should get quizzes by category")
    void testGetQuizzesByCategory_Success() {
        // Arrange
        List<Quiz> quizzes = Arrays.asList(testQuiz);
        when(quizMapper.toCategoryReference(1L)).thenReturn(testCategory);
        when(quizService.getQuizzesByCategory(testCategory)).thenReturn(quizzes);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        List<QuizResponse> response = controller.getQuizzesByCategory(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(quizService).getQuizzesByCategory(testCategory);
    }

    @Test
    @DisplayName("Should return empty list when no quizzes for category")
    void testGetQuizzesByCategory_Empty() {
        // Arrange
        when(quizMapper.toCategoryReference(1L)).thenReturn(testCategory);
        when(quizService.getQuizzesByCategory(testCategory)).thenReturn(new ArrayList<>());

        // Act
        List<QuizResponse> response = controller.getQuizzesByCategory(1L);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    @DisplayName("Should get active quizzes")
    void testGetActiveQuizzes_Success() {
        // Arrange
        List<Quiz> activeQuizzes = Arrays.asList(testQuiz);
        when(quizService.getAllActiveQuizzes()).thenReturn(activeQuizzes);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        List<QuizResponse> response = controller.getActiveQuizzes();

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertTrue(response.get(0).active());
    }

    @Test
    @DisplayName("Should return empty list when no active quizzes")
    void testGetActiveQuizzes_Empty() {
        // Arrange
        when(quizService.getAllActiveQuizzes()).thenReturn(new ArrayList<>());

        // Act
        List<QuizResponse> response = controller.getActiveQuizzes();

        // Assert
        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Test
    @DisplayName("Should create quiz with valid response")
    void testCreateQuiz_ResponseContainsId() {
        // Arrange
        when(quizMapper.toEntity(testQuizRequest)).thenReturn(testQuiz);
        when(quizService.createQuiz(testQuiz)).thenReturn(testQuiz);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        ResponseEntity<QuizResponse> response = controller.createQuiz(testQuizRequest);

        // Assert
        assertEquals(1L, response.getBody().quizId());
    }

    @Test
    @DisplayName("Should throw exception when quiz not found")
    void testGetQuizById_NotFound() {
        // Arrange
        when(quizService.getQuizById(999L))
                .thenThrow(new NoSuchElementException("Quiz not found"));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> controller.getQuizById(999L));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent quiz")
    void testDeleteQuizById_NotFound() {
        // Arrange
        doThrow(new NoSuchElementException("Quiz not found"))
                .when(quizService).deleteQuizById(999L);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> controller.deleteQuizById(999L));
    }

    @Test
    @DisplayName("Should map quiz response correctly")
    void testGetQuizById_MapsResponse() {
        // Arrange
        when(quizService.getQuizById(1L)).thenReturn(testQuiz);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        controller.getQuizById(1L);

        // Assert
        verify(quizMapper).toResponse(testQuiz);
    }

    @Test
    @DisplayName("Should call mapper for category reference")
    void testGetQuizzesByCategory_MapsCategory() {
        // Arrange
        when(quizMapper.toCategoryReference(1L)).thenReturn(testCategory);
        when(quizService.getQuizzesByCategory(testCategory)).thenReturn(new ArrayList<>());

        // Act
        controller.getQuizzesByCategory(1L);

        // Assert
        verify(quizMapper).toCategoryReference(1L);
    }

    @Test
    @DisplayName("Should map all quizzes in set")
    void testGetAllQuizzes_MapsAllQuizzes() {
        // Arrange
        Set<Quiz> quizzes = Set.of(testQuiz);
        when(quizService.getAllQuizzes()).thenReturn(quizzes);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        controller.getAllQuizzes();

        // Assert
        verify(quizMapper).toResponse(testQuiz);
    }

    @Test
    @DisplayName("Should handle multiple quizzes in category")
    void testGetQuizzesByCategory_MultipleQuizzes() {
        // Arrange
        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);
        quiz2.setTitle("Advanced Java");
        List<Quiz> quizzes = Arrays.asList(testQuiz, quiz2);

        QuizResponse response2 = new QuizResponse(2L, "Advanced Java", "desc", "50", "5", true, 1L, "Java");

        when(quizMapper.toCategoryReference(1L)).thenReturn(testCategory);
        when(quizService.getQuizzesByCategory(testCategory)).thenReturn(quizzes);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);
        when(quizMapper.toResponse(quiz2)).thenReturn(response2);

        // Act
        List<QuizResponse> response = controller.getQuizzesByCategory(1L);

        // Assert
        assertEquals(2, response.size());
    }

    @Test
    @DisplayName("Should preserve quiz properties in response")
    void testGetQuizById_PreservesProperties() {
        // Arrange
        when(quizService.getQuizById(1L)).thenReturn(testQuiz);
        when(quizMapper.toResponse(testQuiz)).thenReturn(testQuizResponse);

        // Act
        QuizResponse response = controller.getQuizById(1L);

        // Assert
        assertEquals("100", response.maxMarks());
        assertEquals("10", response.numberOfQuestions());
    }
}
