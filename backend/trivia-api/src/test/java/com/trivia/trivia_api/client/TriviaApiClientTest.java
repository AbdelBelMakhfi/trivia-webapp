package com.trivia.trivia_api.client;

import com.trivia.trivia_api.dto.OpenTriviaQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class TriviaApiClientTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private TriviaApiClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        client = new TriviaApiClient(restTemplate);
    }

    @Test
    void fetchQuestions_parsesResponseAndDecodesHtml() {
        String json = """
                {"response_code":0,"results":[{"type":"multiple","difficulty":"easy","category":"Science","question":"What is 2+2?","correct_answer":"4","incorrect_answers":["3","5","6"]}]}
                """;
        server.expect(requestTo("https://opentdb.com/api.php?amount=5&type=multiple"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        List<OpenTriviaQuestion> result = client.fetchQuestions(5);

        server.verify();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getQuestion()).isEqualTo("What is 2+2?");
        assertThat(result.get(0).getCorrectAnswer()).isEqualTo("4");
        assertThat(result.get(0).getCategory()).isEqualTo("Science");
        assertThat(result.get(0).getIncorrectAnswers()).containsExactly("3", "5", "6");
    }

    @Test
    void fetchQuestions_throwsWhenResponseCodeNonZero() {
        String json = """
                {"response_code":1,"results":[]}
                """;
        server.expect(requestTo("https://opentdb.com/api.php?amount=5&type=multiple"))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> client.fetchQuestions(5))
                .isInstanceOf(TriviaApiClient.TriviaApiException.class)
                .hasMessageContaining("response_code");
    }
}
