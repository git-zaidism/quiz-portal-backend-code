package com.quiz.quiz;

import com.quiz.entities.Category;
import com.quiz.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findBycategory(Category category);

    List<Quiz> findByActive(Boolean b);

    List<Quiz> findByCategoryAndActive(Category c, Boolean b);
}
