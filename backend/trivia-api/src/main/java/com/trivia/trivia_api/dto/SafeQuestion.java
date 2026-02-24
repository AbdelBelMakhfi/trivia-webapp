package com.trivia.trivia_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SafeQuestion {

    private String id;
    private String question;
    private String category;
    private List<String> answers;
}
