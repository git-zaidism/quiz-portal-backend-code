package com.exam.repo;

import com.exam.model.entities.Category;
import com.exam.model.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findBycategory(Category category);

    List<Quiz> findByActive(Boolean b);

    List<Quiz> findByCategoryAndActive(Category c, Boolean b);
}
