package com.trivia.trivia_api.client;

import com.trivia.trivia_api.dto.OpenTriviaQuestion;
import com.trivia.trivia_api.dto.OpenTriviaResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.Collections;
import java.util.List;

@Component
public class TriviaApiClient {

    private static final String BASE_URL = "https://opentdb.com/api.php";

    private final RestTemplate restTemplate;

    public TriviaApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<OpenTriviaQuestion> fetchQuestions(int amount) {
        String url = BASE_URL + "?amount=" + amount + "&type=multiple";
        OpenTriviaResponse response = restTemplate.getForObject(url, OpenTriviaResponse.class);
        if (response == null || response.getResponseCode() != 0) {
            throw new TriviaApiException("Open Trivia API returned response_code: " +
                    (response != null ? response.getResponseCode() : "null"));
        }
        List<OpenTriviaQuestion> results = response.getResults();
        if (results == null) {
            return Collections.emptyList();
        }
        return results.stream()
                .map(this::decodeQuestion)
                .toList();
    }

    private OpenTriviaQuestion decodeQuestion(OpenTriviaQuestion q) {
        OpenTriviaQuestion decoded = new OpenTriviaQuestion();
        decoded.setType(q.getType());
        decoded.setDifficulty(q.getDifficulty());
        decoded.setCategory(HtmlUtils.htmlUnescape(q.getCategory()));
        decoded.setQuestion(HtmlUtils.htmlUnescape(q.getQuestion()));
        decoded.setCorrectAnswer(HtmlUtils.htmlUnescape(q.getCorrectAnswer()));
        decoded.setIncorrectAnswers(q.getIncorrectAnswers() != null
                ? q.getIncorrectAnswers().stream().map(HtmlUtils::htmlUnescape).toList()
                : Collections.emptyList());
        return decoded;
    }

    public static class TriviaApiException extends RuntimeException {
        public TriviaApiException(String message) {
            super(message);
        }
    }
}
