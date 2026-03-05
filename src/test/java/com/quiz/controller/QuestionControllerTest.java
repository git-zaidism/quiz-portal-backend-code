package com.quiz.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.quiz.dto.question.QuestionEvaluationRequest;
import com.quiz.dto.question.QuestionPublicResponse;
import com.quiz.dto.question.QuestionRequest;
import com.quiz.dto.question.QuestionResponse;
import com.quiz.dto.question.QuizEvaluationResponse;
import com.quiz.entities.Question;
import com.quiz.entities.Quiz;
import com.quiz.mapper.QuestionMapper;
import com.quiz.service.QuestionService;
import com.quiz.service.QuizService;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuestionController Tests")
class QuestionControllerTest {

    private QuestionController controller;

    @Mock
    private QuestionService questionService;

    @Mock
    private QuizService quizService;

    @Mock
    private QuestionMapper questionMapper;

    private Question testQuestion;
    private QuestionResponse testQuestionResponse;
    private QuestionPublicResponse testPublicResponse;
    private QuestionRequest testQuestionRequest;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        controller = new QuestionController(questionService, quizService, questionMapper);

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Java Basics");

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setContent("What is Java?");
        testQuestion.setOption1("Language");
        testQuestion.setOption2("Platform");
        testQuestion.setOption3("Tool");
        testQuestion.setOption4("All");
        testQuestion.setAnswer("All");
        testQuestion.setQuiz(testQuiz);

        testQuestionResponse = new QuestionResponse(
                1L,
                "What is Java?",
                null,
                "Language",
                "Platform",
                "Tool",
                "All",
                "All",
                null,
                1L
        );

        testPublicResponse = new QuestionPublicResponse(
                1L,
                "What is Java?",
                null,
                "Language",
                "Platform",
                "Tool",
                "All",
                1L
        );

        testQuestionRequest = new QuestionRequest(
                1L,
                "What is Java?",
                null,
                "Language",
                "Platform",
                "Tool",
                "All",
                "All",
                1L
        );
    }

    @Test
    @DisplayName("Should create question and return with HTTP 200")
    void testCreateQuestion_Success() {
        // Arrange
        when(questionMapper.toEntity(testQuestionRequest)).thenReturn(testQuestion);
        when(questionService.createQuestion(testQuestion)).thenReturn(testQuestion);
        when(questionMapper.toResponse(testQuestion)).thenReturn(testQuestionResponse);

        // Act
        ResponseEntity<QuestionResponse> response = controller.createQuestion(testQuestionRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("What is Java?", response.getBody().content());
        verify(questionMapper).toEntity(testQuestionRequest);
        verify(questionService).createQuestion(testQuestion);
    }

    @Test
    @DisplayName("Should map request to entity before creating")
    void testCreateQuestion_MapsRequest() {
        // Arrange
        when(questionMapper.toEntity(testQuestionRequest)).thenReturn(testQuestion);
        when(questionService.createQuestion(testQuestion)).thenReturn(testQuestion);
        when(questionMapper.toResponse(testQuestion)).thenReturn(testQuestionResponse);

        // Act
        controller.createQuestion(testQuestionRequest);

        // Assert
        verify(questionMapper).toEntity(testQuestionRequest);
    }

    @Test
    @DisplayName("Should update question and return with HTTP 200")
    void testUpdateQuestion_Success() {
        // Arrange
        testQuestion.setContent("Updated content");
        when(questionMapper.toEntity(testQuestionRequest)).thenReturn(testQuestion);
        when(questionService.updateQuestion(testQuestion)).thenReturn(testQuestion);
        when(questionMapper.toResponse(testQuestion)).thenReturn(testQuestionResponse);

        // Act
        ResponseEntity<QuestionResponse> response = controller.updateQuestion(testQuestionRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(questionService).updateQuestion(testQuestion);
    }

    @Test
    @DisplayName("Should get questions for quiz attempt without answers")
    void testGetQuestionsForQuizAttempt_Success() {
        // Arrange
        List<Question> questions = List.of(testQuestion);
        when(quizService.getQuizById(1L)).thenReturn(testQuiz);
        when(questionService.getQuestionsForQuizAttempt(testQuiz)).thenReturn(questions);
        when(questionMapper.toPublicResponse(testQuestion)).thenReturn(testPublicResponse);

        // Act
        ResponseEntity<List<QuestionPublicResponse>> response = controller.getQuestionsForQuizAttempt(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(quizService).getQuizById(1L);
        verify(questionService).getQuestionsForQuizAttempt(testQuiz);
    }

    @Test
    @DisplayName("Should return empty list when no questions for quiz")
    void testGetQuestionsForQuizAttempt_Empty() {
        // Arrange
        when(quizService.getQuizById(1L)).thenReturn(testQuiz);
        when(questionService.getQuestionsForQuizAttempt(testQuiz)).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<QuestionPublicResponse>> response = controller.getQuestionsForQuizAttempt(1L);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getBody().size());
    }

    @Test
    @DisplayName("Should get all questions for quiz with answers (admin)")
    void testGetQuestionsByQuizForAdmin_Success() {
        // Arrange
        Set<Question> questions = Set.of(testQuestion);
        when(questionMapper.toQuizReference(1L)).thenReturn(testQuiz);
        when(questionService.getQuestionsByQuiz(testQuiz)).thenReturn(questions);
        when(questionMapper.toResponse(testQuestion)).thenReturn(testQuestionResponse);

        // Act
        ResponseEntity<List<QuestionResponse>> response = controller.getQuestionsByQuizForAdmin(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("All", response.getBody().get(0).answer());
    }

    @Test
    @DisplayName("Should get question by id successfully")
    void testGetQuestionById_Success() {
        // Arrange
        when(questionService.getQuestionById(1L)).thenReturn(testQuestion);
        when(questionMapper.toResponse(testQuestion)).thenReturn(testQuestionResponse);

        // Act
        QuestionResponse response = controller.getQuestionById(1L);

        // Assert
        assertNotNull(response);
        assertEquals("What is Java?", response.content());
        verify(questionService).getQuestionById(1L);
    }

    @Test
    @DisplayName("Should delete question by id successfully")
    void testDeleteQuestionById_Success() {
        // Act
        controller.deleteQuestionById(1L);

        // Assert
        verify(questionService).deleteQuestionById(1L);
    }

    @Test
    @DisplayName("Should evaluate quiz with submissions")
    void testEvaluateQuiz_Success() {
        // Arrange
        QuestionEvaluationRequest submission = new QuestionEvaluationRequest(1L, "All");
        List<QuestionEvaluationRequest> submissions = List.of(submission);
        QuizEvaluationResponse evaluationResponse = new QuizEvaluationResponse(100, 1, 1);

        when(questionService.evaluateQuiz(submissions)).thenReturn(evaluationResponse);

        // Act
        ResponseEntity<QuizEvaluationResponse> response = controller.evaluateQuiz(submissions);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(100, response.getBody().marksGot());
        assertEquals(1, response.getBody().correctAnswers());
        verify(questionService).evaluateQuiz(submissions);
    }

    @Test
    @DisplayName("Should handle evaluation with zero marks")
    void testEvaluateQuiz_ZeroMarks() {
        // Arrange
        QuestionEvaluationRequest submission = new QuestionEvaluationRequest(1L, "Wrong");
        List<QuestionEvaluationRequest> submissions = List.of(submission);
        QuizEvaluationResponse evaluationResponse = new QuizEvaluationResponse(0, 0, 1);

        when(questionService.evaluateQuiz(submissions)).thenReturn(evaluationResponse);

        // Act
        ResponseEntity<QuizEvaluationResponse> response = controller.evaluateQuiz(submissions);

        // Assert
        assertEquals(0, response.getBody().marksGot());
        assertEquals(0, response.getBody().correctAnswers());
    }

    @Test
    @DisplayName("Should throw exception when quiz not found")
    void testGetQuestionsForQuizAttempt_QuizNotFound() {
        // Arrange
        when(quizService.getQuizById(999L))
                .thenThrow(new NoSuchElementException("Quiz not found"));

        // Act & Assert
        assertThrows(NoSuchElementException.class, 
                () -> controller.getQuestionsForQuizAttempt(999L));
    }

    @Test
    @DisplayName("Should throw exception when question not found")
    void testGetQuestionById_NotFound() {
        // Arrange
        when(questionService.getQuestionById(999L))
                .thenThrow(new NoSuchElementException("Question not found"));

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> controller.getQuestionById(999L));
    }

    @Test
    @DisplayName("Should map question response correctly")
    void testGetQuestionById_MapsResponse() {
        // Arrange
        when(questionService.getQuestionById(1L)).thenReturn(testQuestion);
        when(questionMapper.toResponse(testQuestion)).thenReturn(testQuestionResponse);

        // Act
        controller.getQuestionById(1L);

        // Assert
        verify(questionMapper).toResponse(testQuestion);
    }

    @Test
    @DisplayName("Should hide answers in public response")
    void testGetQuestionsForQuizAttempt_HidesAnswers() {
        // Arrange
        List<Question> questions = List.of(testQuestion);
        when(quizService.getQuizById(1L)).thenReturn(testQuiz);
        when(questionService.getQuestionsForQuizAttempt(testQuiz)).thenReturn(questions);
        when(questionMapper.toPublicResponse(testQuestion)).thenReturn(testPublicResponse);

        // Act
        ResponseEntity<List<QuestionPublicResponse>> response = controller.getQuestionsForQuizAttempt(1L);

        // Assert
        verify(questionMapper).toPublicResponse(testQuestion);
    }

    @Test
    @DisplayName("Should map all questions in admin view")
    void testGetQuestionsByQuizForAdmin_MapsAllQuestions() {
        // Arrange
        Set<Question> questions = Set.of(testQuestion);
        when(questionMapper.toQuizReference(1L)).thenReturn(testQuiz);
        when(questionService.getQuestionsByQuiz(testQuiz)).thenReturn(questions);
        when(questionMapper.toResponse(testQuestion)).thenReturn(testQuestionResponse);

        // Act
        controller.getQuestionsByQuizForAdmin(1L);

        // Assert
        verify(questionMapper).toResponse(testQuestion);
    }

    @Test
    @DisplayName("Should create question response with id")
    void testCreateQuestion_ResponseHasId() {
        // Arrange
        when(questionMapper.toEntity(testQuestionRequest)).thenReturn(testQuestion);
        when(questionService.createQuestion(testQuestion)).thenReturn(testQuestion);
        when(questionMapper.toResponse(testQuestion)).thenReturn(testQuestionResponse);

        // Act
        ResponseEntity<QuestionResponse> response = controller.createQuestion(testQuestionRequest);

        // Assert
        assertEquals(1L, response.getBody().questionId());
    }

    @Test
    @DisplayName("Should evaluate quiz with multiple submissions")
    void testEvaluateQuiz_MultipleSubmissions() {
        // Arrange
        QuestionEvaluationRequest s1 = new QuestionEvaluationRequest(1L, "All");
        QuestionEvaluationRequest s2 = new QuestionEvaluationRequest(2L, "Wrong");
        List<QuestionEvaluationRequest> submissions = Arrays.asList(s1, s2);
        QuizEvaluationResponse evaluationResponse = new QuizEvaluationResponse(50, 1, 2);

        when(questionService.evaluateQuiz(submissions)).thenReturn(evaluationResponse);

        // Act
        ResponseEntity<QuizEvaluationResponse> response = controller.evaluateQuiz(submissions);

        // Assert
        assertEquals(2, response.getBody().attempted());
        assertEquals(1, response.getBody().correctAnswers());
    }
}
