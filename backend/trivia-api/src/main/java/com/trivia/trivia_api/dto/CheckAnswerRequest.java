package com.trivia.trivia_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckAnswerRequest {

    @NotNull
    @NotBlank
    private String questionId;

    @NotNull
    @NotBlank
    private String answer;
}
