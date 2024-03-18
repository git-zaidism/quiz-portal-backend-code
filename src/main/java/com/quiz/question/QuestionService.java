package com.quiz.question;

import com.quiz.entities.Question;
import com.quiz.entities.Quiz;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class QuestionService {

  private final QuestionRepository questionRepository;

  public Question addQuestion(Question question) {
    return this.questionRepository.save(question);
  }

  public Question updateQuestion(Question question) {
    return this.questionRepository.save(question);
  }

  public Set<Question> getQuestions() {
    return new HashSet<>(this.questionRepository.findAll());
  }

  public Question getQuestion(Long questionId) {
    return this.questionRepository.findById(questionId).get();
  }

  public Set<Question> getQuestionsOfQuiz(Quiz quiz) {
    return this.questionRepository.findByQuiz(quiz);
  }

  public void deleteQuestion(Long quesId) {
    Question question = new Question();
    question.setQuesId(quesId);
    this.questionRepository.delete(question);
  }

  public Question get(Long questionsId) {
    return this.questionRepository.getOne(questionsId);
  }
}
