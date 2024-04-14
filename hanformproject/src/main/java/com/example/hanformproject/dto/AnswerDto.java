package com.example.hanformproject.dto;


import com.example.hanformproject.entity.AnswerEntity;
import com.example.hanformproject.entity.QuestionEntity;
import com.example.hanformproject.entity.UserEntity;
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
    private List<AnswerDetail> responses;

    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    @Getter
    @Setter
    public static class AnswerDetail {
        private Long questionId;
        private String answer;

        public AnswerEntity toEntity(UserEntity user, QuestionEntity questionId) {
            AnswerEntity answer = new AnswerEntity();
            answer.setUser(user);
            answer.setQuestion(questionId);
            answer.setAnswer(this.answer);
            return answer;
        }
    }


}