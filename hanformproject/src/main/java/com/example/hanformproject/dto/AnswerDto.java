package com.example.hanformproject.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class AnswerDto {
    private Long answerId;
    private Long userId;
    private Long questionId;
    private Long surveyId;
    private String answerText;


    // 답변 제출을 받기 위한 클래스
    @Data
    public static class ResponseWrapper{
        private List<AnswerDto> responses;
    }
}