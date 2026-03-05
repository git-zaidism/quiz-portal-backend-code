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
import java.util.LinkedHashSet;
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

        syncQuizQuestions(sqlQuiz, loadSeedQuestions("seed/sql-questions.json"));
        syncQuizQuestions(javaQuiz, loadSeedQuestions("seed/java-questions.json"));
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

    private void syncQuizQuestions(Quiz quiz, List<SeedQuestion> seedQuestions) {
        Set<Question> existing = questionRepository.findByQuiz(quiz);
        List<SeedQuestion> desired = buildDesiredSeedSet(quiz, seedQuestions);

        Set<String> existingSignature = existing.stream()
                .map(this::signature)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> desiredSignature = desired.stream()
                .map(this::signature)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (existing.size() == desired.size() && existingSignature.equals(desiredSignature)) {
            return;
        }

        List<Question> newQuestions = new ArrayList<>();
        for (SeedQuestion seed : desired) {
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
        }

        if (!existing.isEmpty()) {
            questionRepository.deleteAll(existing);
            questionRepository.flush();
        }
        questionRepository.saveAll(newQuestions);
    }

    private List<SeedQuestion> buildDesiredSeedSet(Quiz quiz, List<SeedQuestion> source) {
        List<SeedQuestion> desired = new ArrayList<>(source);
        int index = 1;
        while (desired.size() < MIN_QUESTIONS_PER_QUIZ) {
            desired.add(new SeedQuestion(
                    quiz.getTitle() + " extra question " + index,
                    "Option A",
                    "Option B",
                    "Option C",
                    "Option D",
                    "Option A"
            ));
            index++;
        }
        return desired;
    }

    private String signature(Question q) {
        return signature(new SeedQuestion(
                q.getContent(),
                q.getOption1(),
                q.getOption2(),
                q.getOption3(),
                q.getOption4(),
                q.getAnswer()
        ));
    }

    private String signature(SeedQuestion q) {
        return normalize(q.content()) + "|" +
                normalize(q.option1()) + "|" +
                normalize(q.option2()) + "|" +
                normalize(q.option3()) + "|" +
                normalize(q.option4()) + "|" +
                normalize(q.answer());
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private record SeedQuestion(String content, String option1, String option2, String option3, String option4, String answer) {
    }
}
