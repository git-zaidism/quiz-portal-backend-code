package com.quiz.config.seeder;

import com.quiz.entities.Category;
import com.quiz.entities.Question;
import com.quiz.entities.Quiz;
import com.quiz.repositoy.CategoryRepository;
import com.quiz.repositoy.QuestionRepository;
import com.quiz.repositoy.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class DefaultQuizDataSeeder implements CommandLineRunner {

    private static final int MIN_QUESTIONS_PER_QUIZ = 30;

    private final CategoryRepository categoryRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Starting default quiz data seeding");
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
        log.info("Default quiz data seeding completed");
    }

    private Category getOrCreateCategory(String title, String description) {
        return categoryRepository.findAll().stream()
                .filter(existingCategory -> existingCategory.getTitle() != null
                        && existingCategory.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .map(existing -> {
                    log.debug("Updating existing category title={} categoryId={}", title, existing.getId());
                    existing.setDescription(description);
                    return categoryRepository.save(existing);
                })
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setTitle(title);
                    category.setDescription(description);
                    Category createdCategory = categoryRepository.save(category);
                    log.info("Created default category title={} categoryId={}", title, createdCategory.getId());
                    return createdCategory;
                });
    }

    private Quiz getOrCreateOrUpdateQuiz(Category category,
                                         String title,
                                         String description,
                                         String maxMarks,
                                         String numberOfQuestions) {
        return quizRepository.findAll().stream()
                .filter(existingQuiz -> existingQuiz.getTitle() != null
                        && existingQuiz.getTitle().equalsIgnoreCase(title))
                .filter(existingQuiz -> existingQuiz.getCategory() != null
                        && Objects.equals(existingQuiz.getCategory().getId(), category.getId()))
                .findFirst()
                .map(existing -> {
                    log.debug("Updating existing quiz title={} quizId={}", title, existing.getId());
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
                    Quiz createdQuiz = quizRepository.save(quiz);
                    log.info("Created default quiz title={} quizId={}", title, createdQuiz.getId());
                    return createdQuiz;
                });
    }

    private List<SeedQuestion> loadSeedQuestions(String classpathResource) {
        try (InputStream inputStream = new ClassPathResource(classpathResource).getInputStream()) {
            SeedQuestion[] questions = objectMapper.readValue(inputStream, SeedQuestion[].class);
            log.debug("Loaded {} seed questions from {}", questions.length, classpathResource);
            return Arrays.asList(questions);
        } catch (IOException exception) {
            log.error("Failed to load seed questions from {}", classpathResource, exception);
            throw new IllegalStateException("Unable to load quiz seed file: " + classpathResource, exception);
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
            log.debug("Quiz question set already up-to-date for quizId={}", quiz.getId());
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
        log.info("Synchronized quiz questions for quizId={} oldCount={} newCount={}",
                quiz.getId(), existing.size(), newQuestions.size());
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
                    "Option A",
                    null,
                    null,
                    null,
                    null
            ));
            index++;
        }
        return desired;
    }

    private String signature(Question question) {
        return signature(new SeedQuestion(
                question.getContent(),
                question.getOption1(),
                question.getOption2(),
                question.getOption3(),
                question.getOption4(),
                question.getAnswer(),
                null,
                null,
                null,
                null
        ));
    }

    private String signature(SeedQuestion question) {
        return normalize(question.content()) + "|" +
                normalize(question.option1()) + "|" +
                normalize(question.option2()) + "|" +
                normalize(question.option3()) + "|" +
                normalize(question.option4()) + "|" +
                normalize(question.answer());
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private record SeedQuestion(String content,
                                String option1,
                                String option2,
                                String option3,
                                String option4,
                                String answer,
                                String createdAt,
                                String updatedAt,
                                String createdBy,
                                String updatedBy) {
    }
}
