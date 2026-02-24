package com.trivia.trivia_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenTriviaResponse {

    @JsonProperty("response_code")
    private int responseCode;

    private List<OpenTriviaQuestion> results;
}
