package com.trivia.trivia_api.service;

import com.trivia.trivia_api.client.TriviaApiClient;
import com.trivia.trivia_api.dto.OpenTriviaQuestion;
import com.trivia.trivia_api.dto.SafeQuestion;
import com.trivia.trivia_api.exception.QuestionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TriviaServiceTest {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    @Mock
    private TriviaApiClient triviaApiClient;

    @InjectMocks
    private TriviaService triviaService;

    private OpenTriviaQuestion sampleQuestion;

    @BeforeEach
    void setUp() {
        sampleQuestion = new OpenTriviaQuestion();
        sampleQuestion.setQuestion("What is 2+2?");
        sampleQuestion.setCategory("Math");
        sampleQuestion.setCorrectAnswer("4");
        sampleQuestion.setIncorrectAnswers(List.of("3", "5", "6"));
    }

    @Test
    void getQuestions_returnsSafeQuestionsWithoutCorrectAnswerField() {
        when(triviaApiClient.fetchQuestions(anyInt())).thenReturn(List.of(sampleQuestion));

        List<SafeQuestion> result = triviaService.getQuestions(1);

        assertThat(result).hasSize(1);
        SafeQuestion safe = result.get(0);
        assertThat(safe.getQuestion()).isEqualTo("What is 2+2?");
        assertThat(safe.getCategory()).isEqualTo("Math");
        assertThat(safe.getId()).matches(UUID_PATTERN);
        assertThat(safe.getAnswers()).containsExactlyInAnyOrder("4", "3", "5", "6");
        assertThat(safe.getAnswers()).doesNotContainSequence("4", "3", "5", "6");
        verify(triviaApiClient).fetchQuestions(1);
    }

    @Test
    void getQuestions_shufflesAnswers() {
        when(triviaApiClient.fetchQuestions(anyInt())).thenReturn(List.of(sampleQuestion));

        List<SafeQuestion> first = triviaService.getQuestions(1);
        when(triviaApiClient.fetchQuestions(anyInt())).thenReturn(List.of(sampleQuestion));
        List<SafeQuestion> second = triviaService.getQuestions(1);

        Set<String> firstSet = Set.copyOf(first.get(0).getAnswers());
        Set<String> secondSet = Set.copyOf(second.get(0).getAnswers());
        assertThat(firstSet).isEqualTo(secondSet);
    }

    @Test
    void getQuestions_generatesUniqueIds() {
        when(triviaApiClient.fetchQuestions(anyInt())).thenReturn(List.of(sampleQuestion, sampleQuestion));

        List<SafeQuestion> result = triviaService.getQuestions(2);

        assertThat(result.get(0).getId()).isNotEqualTo(result.get(1).getId());
        assertThat(result.get(0).getId()).matches(UUID_PATTERN);
        assertThat(result.get(1).getId()).matches(UUID_PATTERN);
    }

    @Test
    void checkAnswer_returnsTrueWhenCorrect() {
        when(triviaApiClient.fetchQuestions(anyInt())).thenReturn(List.of(sampleQuestion));
        List<SafeQuestion> questions = triviaService.getQuestions(1);
        String questionId = questions.get(0).getId();

        boolean correct = triviaService.checkAnswer(questionId, "4");

        assertThat(correct).isTrue();
    }

    @Test
    void checkAnswer_returnsFalseWhenIncorrect() {
        when(triviaApiClient.fetchQuestions(anyInt())).thenReturn(List.of(sampleQuestion));
        List<SafeQuestion> questions = triviaService.getQuestions(1);
        String questionId = questions.get(0).getId();

        boolean correct = triviaService.checkAnswer(questionId, "3");

        assertThat(correct).isFalse();
    }

    @Test
    void checkAnswer_throwsWhenQuestionIdUnknown() {
        assertThatThrownBy(() -> triviaService.checkAnswer("unknown-id", "4"))
                .isInstanceOf(QuestionNotFoundException.class)
                .hasMessageContaining("unknown-id");
    }
}
