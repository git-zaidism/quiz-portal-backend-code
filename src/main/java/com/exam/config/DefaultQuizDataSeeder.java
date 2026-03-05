package com.exam.config;

import com.exam.model.exam.Category;
import com.exam.model.exam.Question;
import com.exam.model.exam.Quiz;
import com.exam.repo.CategoryRepository;
import com.exam.repo.QuestionRepository;
import com.exam.repo.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DefaultQuizDataSeeder implements CommandLineRunner {

    private static final int MIN_QUESTIONS_PER_QUIZ = 30;

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DefaultQuizDataSeeder(CategoryRepository categoryRepository,
                                 QuizRepository quizRepository,
                                 QuestionRepository questionRepository) {
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Category sqlCategory = getOrCreateCategory("SQL", "Structured Query Language quizzes");
        Category javaCategory = getOrCreateCategory("Java", "Core Java programming quizzes");

        Quiz sqlQuiz = getOrCreateOrUpdateQuiz(
                sqlCategory,
                "SQL Fundamentals Quiz",
                "Default SQL quiz with beginner to intermediate questions",
                "100",
                String.valueOf(MIN_QUESTIONS_PER_QUIZ)
        );

        Quiz javaQuiz = getOrCreateOrUpdateQuiz(
                javaCategory,
                "Java Fundamentals Quiz",
                "Default Java quiz with core language questions",
                "100",
                String.valueOf(MIN_QUESTIONS_PER_QUIZ)
        );

        ensureMinimumQuestions(sqlQuiz, loadSeedQuestions("seed/sql-questions.json"));
        ensureMinimumQuestions(javaQuiz, loadSeedQuestions("seed/java-questions.json"));
    }

    private Category getOrCreateCategory(String title, String description) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getTitle() != null && c.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .map(existing -> {
                    existing.setDescription(description);
                    return categoryRepository.save(existing);
                })
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setTitle(title);
                    category.setDescription(description);
                    return categoryRepository.save(category);
                });
    }

    private Quiz getOrCreateOrUpdateQuiz(Category category,
                                         String title,
                                         String description,
                                         String maxMarks,
                                         String numberOfQuestions) {
        return quizRepository.findAll().stream()
                .filter(q -> q.getTitle() != null && q.getTitle().equalsIgnoreCase(title))
                .filter(q -> q.getCategory() != null && Objects.equals(q.getCategory().getCid(), category.getCid()))
                .findFirst()
                .map(existing -> {
                    existing.setDescription(description);
                    existing.setMaxMarks(maxMarks);
                    existing.setNumberOfQuestions(numberOfQuestions);
                    existing.setActive(true);
                    return quizRepository.save(existing);
                })
                .orElseGet(() -> {
                    Quiz quiz = new Quiz();
                    quiz.setTitle(title);
                    quiz.setDescription(description);
                    quiz.setMaxMarks(maxMarks);
                    quiz.setNumberOfQuestions(numberOfQuestions);
                    quiz.setActive(true);
                    quiz.setCategory(category);
                    return quizRepository.save(quiz);
                });
    }

    private List<SeedQuestion> loadSeedQuestions(String classpathResource) {
        try (InputStream inputStream = new ClassPathResource(classpathResource).getInputStream()) {
            SeedQuestion[] questions = objectMapper.readValue(inputStream, SeedQuestion[].class);
            return Arrays.asList(questions);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load quiz seed file: " + classpathResource, e);
        }
    }

    private void ensureMinimumQuestions(Quiz quiz, List<SeedQuestion> seedQuestions) {
        Set<Question> existing = questionRepository.findByQuiz(quiz);
        if (existing.size() >= MIN_QUESTIONS_PER_QUIZ) {
            return;
        }

        Set<String> existingContent = existing.stream()
                .map(Question::getContent)
                .filter(Objects::nonNull)
                .map(this::normalize)
                .collect(Collectors.toSet());

        int needed = MIN_QUESTIONS_PER_QUIZ - existing.size();
        List<Question> newQuestions = new ArrayList<>();

        for (SeedQuestion seed : seedQuestions) {
            if (needed == 0) {
                break;
            }
            if (existingContent.contains(normalize(seed.content()))) {
                continue;
            }
            Question question = new Question();
            question.setContent(seed.content());
            question.setOption1(seed.option1());
            question.setOption2(seed.option2());
            question.setOption3(seed.option3());
            question.setOption4(seed.option4());
            question.setAnswer(seed.answer());
            question.setImage("");
            question.setQuiz(quiz);
            newQuestions.add(question);
            needed--;
        }

        int index = 1;
        while (needed > 0) {
            Question question = new Question();
            question.setContent(quiz.getTitle() + " extra question " + index);
            question.setOption1("Option A");
            question.setOption2("Option B");
            question.setOption3("Option C");
            question.setOption4("Option D");
            question.setAnswer("Option A");
            question.setImage("");
            question.setQuiz(quiz);
            newQuestions.add(question);
            index++;
            needed--;
        }

        questionRepository.saveAll(newQuestions);
    }

    private String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private record SeedQuestion(String content, String option1, String option2, String option3, String option4, String answer) {
    }
}
