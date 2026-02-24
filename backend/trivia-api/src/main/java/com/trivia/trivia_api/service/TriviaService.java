package com.trivia.trivia_api.service;

import com.trivia.trivia_api.client.TriviaApiClient;
import com.trivia.trivia_api.dto.OpenTriviaQuestion;
import com.trivia.trivia_api.dto.SafeQuestion;
import com.trivia.trivia_api.exception.QuestionNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TriviaService {

    private final TriviaApiClient triviaApiClient;
    private final Map<String, String> correctAnswersByQuestionId = new ConcurrentHashMap<>();

    public TriviaService(TriviaApiClient triviaApiClient) {
        this.triviaApiClient = triviaApiClient;
    }

    public List<SafeQuestion> getQuestions(int amount) {
        List<OpenTriviaQuestion> raw = triviaApiClient.fetchQuestions(amount);
        List<SafeQuestion> result = new ArrayList<>(raw.size());
        for (OpenTriviaQuestion q : raw) {
            String id = UUID.randomUUID().toString();
            correctAnswersByQuestionId.put(id, q.getCorrectAnswer());
            List<String> answers = new ArrayList<>();
            answers.add(q.getCorrectAnswer());
            if (q.getIncorrectAnswers() != null) {
                answers.addAll(q.getIncorrectAnswers());
            }
            Collections.shuffle(answers);
            result.add(new SafeQuestion(id, q.getQuestion(), q.getCategory(), answers));
        }
        return result;
    }

    public boolean checkAnswer(String questionId, String answer) {
        String correct = correctAnswersByQuestionId.get(questionId);
        if (correct == null) {
            throw new QuestionNotFoundException(questionId);
        }
        return correct.equals(answer);
    }
}
