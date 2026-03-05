package com.quiz.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import com.quiz.dto.question.QuestionEvaluationRequest;
import com.quiz.dto.question.QuizEvaluationResponse;
import com.quiz.entities.Question;
import com.quiz.entities.Quiz;
import com.quiz.repositoy.QuestionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuestionServiceImpl Tests")
class QuestionServiceImplTest {

    private QuestionServiceImpl questionService;

    @Mock
    private QuestionRepository questionRepository;

    private Question testQuestion;
    private Quiz testQuiz;

    @BeforeEach
    void setUp() {
        questionService = new QuestionServiceImpl(questionRepository);

        testQuiz = new Quiz();
        testQuiz.setId(1L);
        testQuiz.setTitle("Java Basics");
        testQuiz.setNumberOfQuestions("5");
        testQuiz.setMaxMarks("100");

        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setContent("What is Java?");
        testQuestion.setOption1("A language");
        testQuestion.setOption2("A platform");
        testQuestion.setOption3("A tool");
        testQuestion.setOption4("All of above");
        testQuestion.setAnswer("All of above");
        testQuestion.setQuiz(testQuiz);
    }

    @Test
    @DisplayName("Should create question successfully")
    void testCreateQuestion_Success() {
        // Arrange
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // Act
        Question createdQuestion = questionService.createQuestion(testQuestion);

        // Assert
        assertNotNull(createdQuestion);
        assertEquals(1L, createdQuestion.getId());
        assertEquals("What is Java?", createdQuestion.getContent());
        verify(questionRepository).save(testQuestion);
    }

    @Test
    @DisplayName("Should call repository save when creating question")
    void testCreateQuestion_CallsRepository() {
        // Arrange
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // Act
        questionService.createQuestion(testQuestion);

        // Assert
        verify(questionRepository).save(testQuestion);
    }

    @Test
    @DisplayName("Should update question successfully")
    void testUpdateQuestion_Success() {
        // Arrange
        testQuestion.setContent("Updated question");
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // Act
        Question updatedQuestion = questionService.updateQuestion(testQuestion);

        // Assert
        assertNotNull(updatedQuestion);
        assertEquals("Updated question", updatedQuestion.getContent());
        verify(questionRepository).save(testQuestion);
    }

    @Test
    @DisplayName("Should retrieve all questions as set")
    void testGetAllQuestions_Success() {
        // Arrange
        Question question2 = new Question();
        question2.setId(2L);
        question2.setContent("What is OOP?");
        List<Question> questions = Arrays.asList(testQuestion, question2);

        when(questionRepository.findAll()).thenReturn(questions);

        // Act
        Set<Question> result = questionService.getAllQuestions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testQuestion));
        assertTrue(result.contains(question2));
    }

    @Test
    @DisplayName("Should return empty set when no questions exist")
    void testGetAllQuestions_Empty() {
        // Arrange
        when(questionRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        Set<Question> result = questionService.getAllQuestions();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should get question by id successfully")
    void testGetQuestionById_Success() {
        // Arrange
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act
        Question foundQuestion = questionService.getQuestionById(1L);

        // Assert
        assertNotNull(foundQuestion);
        assertEquals(1L, foundQuestion.getId());
        assertEquals("What is Java?", foundQuestion.getContent());
    }

    @Test
    @DisplayName("Should throw exception when question id not found")
    void testGetQuestionById_NotFound() {
        // Arrange
        when(questionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> questionService.getQuestionById(999L));
    }

    @Test
    @DisplayName("Should get questions by quiz")
    void testGetQuestionsByQuiz_Success() {
        // Arrange
        Question question2 = new Question();
        question2.setId(2L);
        question2.setQuiz(testQuiz);
        Set<Question> questions = new HashSet<>(Arrays.asList(testQuestion, question2));

        when(questionRepository.findByQuiz(testQuiz)).thenReturn(questions);

        // Act
        Set<Question> result = questionService.getQuestionsByQuiz(testQuiz);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testQuestion));
    }

    @Test
    @DisplayName("Should return empty set when no questions for quiz")
    void testGetQuestionsByQuiz_Empty() {
        // Arrange
        when(questionRepository.findByQuiz(testQuiz)).thenReturn(new HashSet<>());

        // Act
        Set<Question> result = questionService.getQuestionsByQuiz(testQuiz);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Should delete question by id successfully")
    void testDeleteQuestionById_Success() {
        // Arrange
        when(questionRepository.existsById(1L)).thenReturn(true);

        // Act
        questionService.deleteQuestionById(1L);

        // Assert
        verify(questionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent question")
    void testDeleteQuestionById_NotFound() {
        // Arrange
        when(questionRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> questionService.deleteQuestionById(999L));
        verify(questionRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should get questions for quiz attempt with shuffling")
    void testGetQuestionsForQuizAttempt_Success() {
        // Arrange
        Question q1 = new Question();
        q1.setId(1L);
        Question q2 = new Question();
        q2.setId(2L);
        Question q3 = new Question();
        q3.setId(3L);

        Set<Question> questions = new HashSet<>(Arrays.asList(q1, q2, q3));
        testQuiz.setQuestions(questions);
        testQuiz.setNumberOfQuestions("2");

        // Act
        List<Question> result = questionService.getQuestionsForQuizAttempt(testQuiz);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return all questions when count less than max")
    void testGetQuestionsForQuizAttempt_AllQuestions() {
        // Arrange
        Question q1 = new Question();
        q1.setId(1L);
        Question q2 = new Question();
        q2.setId(2L);

        Set<Question> questions = new HashSet<>(Arrays.asList(q1, q2));
        testQuiz.setQuestions(questions);
        testQuiz.setNumberOfQuestions("5");

        // Act
        List<Question> result = questionService.getQuestionsForQuizAttempt(testQuiz);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should throw exception when numberOfQuestions is not numeric")
    void testGetQuestionsForQuizAttempt_InvalidNumberFormat() {
        // Arrange
        Set<Question> questions = new HashSet<>(Arrays.asList(testQuestion));
        testQuiz.setQuestions(questions);
        testQuiz.setNumberOfQuestions("invalid");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> questionService.getQuestionsForQuizAttempt(testQuiz));
    }

    @Test
    @DisplayName("Should evaluate quiz with correct answers")
    void testEvaluateQuiz_WithCorrectAnswers() {
        // Arrange
        QuestionEvaluationRequest submission = new QuestionEvaluationRequest(1L, "All of above");
        List<QuestionEvaluationRequest> submissions = List.of(submission);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act
        QuizEvaluationResponse response = questionService.evaluateQuiz(submissions);

        // Assert
        assertNotNull(response);
        assertEquals(100, response.marksGot());
        assertEquals(1, response.correctAnswers());
        assertEquals(1, response.attempted());
    }

    @Test
    @DisplayName("Should evaluate quiz with wrong answers")
    void testEvaluateQuiz_WithWrongAnswers() {
        // Arrange
        QuestionEvaluationRequest submission = new QuestionEvaluationRequest(1L, "Wrong answer");
        List<QuestionEvaluationRequest> submissions = List.of(submission);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act
        QuizEvaluationResponse response = questionService.evaluateQuiz(submissions);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.marksGot());
        assertEquals(0, response.correctAnswers());
        assertEquals(1, response.attempted());
    }

    @Test
    @DisplayName("Should evaluate quiz with blank answer")
    void testEvaluateQuiz_WithBlankAnswer() {
        // Arrange
        QuestionEvaluationRequest submission = new QuestionEvaluationRequest(1L, "");
        List<QuestionEvaluationRequest> submissions = List.of(submission);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act
        QuizEvaluationResponse response = questionService.evaluateQuiz(submissions);

        // Assert
        assertEquals(0, response.attempted());
    }

    @Test
    @DisplayName("Should evaluate quiz with null answer")
    void testEvaluateQuiz_WithNullAnswer() {
        // Arrange
        QuestionEvaluationRequest submission = new QuestionEvaluationRequest(1L, null);
        List<QuestionEvaluationRequest> submissions = List.of(submission);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act
        QuizEvaluationResponse response = questionService.evaluateQuiz(submissions);

        // Assert
        assertEquals(0, response.attempted());
    }

    @Test
    @DisplayName("Should handle null submissions in evaluation")
    void testEvaluateQuiz_NullSubmissions() {
        // Act
        QuizEvaluationResponse response = questionService.evaluateQuiz(null);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.marksGot());
        assertEquals(0, response.correctAnswers());
        assertEquals(0, response.attempted());
    }

    @Test
    @DisplayName("Should handle empty submissions in evaluation")
    void testEvaluateQuiz_EmptySubmissions() {
        // Act
        QuizEvaluationResponse response = questionService.evaluateQuiz(new ArrayList<>());

        // Assert
        assertNotNull(response);
        assertEquals(0, response.marksGot());
    }

    @Test
    @DisplayName("Should calculate correct marks with multiple questions")
    void testEvaluateQuiz_MultipleQuestions() {
        // Arrange
        Question q2 = new Question();
        q2.setId(2L);
        q2.setAnswer("B");
        q2.setQuiz(testQuiz);

        QuestionEvaluationRequest s1 = new QuestionEvaluationRequest(1L, "All of above");
        QuestionEvaluationRequest s2 = new QuestionEvaluationRequest(2L, "Wrong");
        List<QuestionEvaluationRequest> submissions = Arrays.asList(s1, s2);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.findById(2L)).thenReturn(Optional.of(q2));

        // Act
        QuizEvaluationResponse response = questionService.evaluateQuiz(submissions);

        // Assert
        assertEquals(1, response.correctAnswers());
        assertEquals(2, response.attempted());
        assertEquals(50, response.marksGot());
    }

    @Test
    @DisplayName("Should throw exception when maxMarks is invalid format")
    void testEvaluateQuiz_InvalidMaxMarks() {
        // Arrange
        testQuiz.setMaxMarks("invalid");
        QuestionEvaluationRequest submission = new QuestionEvaluationRequest(1L, "answer");
        List<QuestionEvaluationRequest> submissions = List.of(submission);

        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                () -> questionService.evaluateQuiz(submissions));
    }
}
