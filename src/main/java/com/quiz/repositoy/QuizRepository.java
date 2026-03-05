package com.quiz.repositoy;

import com.quiz.entities.Category;
import com.quiz.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCategory(Category category);

    List<Quiz> findByActive(boolean active);

    List<Quiz> findByCategoryAndActive(Category category, boolean active);
}
