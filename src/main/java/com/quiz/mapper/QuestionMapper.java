package com.quiz.mapper;

import com.quiz.dto.question.QuestionPublicResponse;
import com.quiz.dto.question.QuestionRequest;
import com.quiz.dto.question.QuestionResponse;
import com.quiz.entities.Question;
import com.quiz.entities.Quiz;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public Question toEntity(QuestionRequest request) {
        Question question = new Question();
        if (request.questionId() != null) {
            question.setId(request.questionId());
        }
        question.setContent(request.content());
        question.setImage(request.image());
        question.setOption1(request.option1());
        question.setOption2(request.option2());
        question.setOption3(request.option3());
        question.setOption4(request.option4());
        question.setAnswer(request.answer());
        question.setQuiz(toQuizReference(request.quizId()));
        return question;
    }

    public Quiz toQuizReference(Long quizId) {
        Quiz quiz = new Quiz();
        quiz.setId(quizId);
        return quiz;
    }

    public QuestionPublicResponse toPublicResponse(Question question) {
        Long quizId = question.getQuiz() != null ? question.getQuiz().getId() : null;
        return new QuestionPublicResponse(
                question.getId(),
                question.getContent(),
                question.getImage(),
                question.getOption1(),
                question.getOption2(),
                question.getOption3(),
                question.getOption4(),
                quizId
        );
    }

    public QuestionResponse toResponse(Question question) {
        Long quizId = question.getQuiz() != null ? question.getQuiz().getId() : null;
        return new QuestionResponse(
                question.getId(),
                question.getContent(),
                question.getImage(),
                question.getOption1(),
                question.getOption2(),
                question.getOption3(),
                question.getOption4(),
                question.getAnswer(),
                question.getGivenAnswer(),
                quizId
        );
    }
}
