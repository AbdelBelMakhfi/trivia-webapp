package com.trivia.trivia_api.controller;

import com.trivia.trivia_api.dto.CheckAnswerRequest;
import com.trivia.trivia_api.dto.CheckAnswerResponse;
import com.trivia.trivia_api.dto.SafeQuestion;
import com.trivia.trivia_api.service.TriviaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class TriviaController {

    private static final int DEFAULT_AMOUNT = 5;
    private static final int MAX_AMOUNT = 50;

    private final TriviaService triviaService;

    public TriviaController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @GetMapping("/questions")
    public ResponseEntity<List<SafeQuestion>> getQuestions(
            @RequestParam(required = false) Integer amount) {
        int count = amount != null ? amount : DEFAULT_AMOUNT;
        if (count < 1) {
            count = DEFAULT_AMOUNT;
        }
        if (count > MAX_AMOUNT) {
            count = MAX_AMOUNT;
        }
        return ResponseEntity.ok(triviaService.getQuestions(count));
    }

    @PostMapping("/checkanswers")
    public ResponseEntity<CheckAnswerResponse> checkAnswers(@Valid @RequestBody CheckAnswerRequest request) {
        boolean correct = triviaService.checkAnswer(request.getQuestionId(), request.getAnswer());
        return ResponseEntity.ok(new CheckAnswerResponse(correct));
    }
}
