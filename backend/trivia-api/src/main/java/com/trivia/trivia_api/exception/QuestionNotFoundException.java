package com.trivia.trivia_api.exception;

public class QuestionNotFoundException extends RuntimeException {

    public QuestionNotFoundException(String questionId) {
        super("Question not found: " + questionId);
    }
}
