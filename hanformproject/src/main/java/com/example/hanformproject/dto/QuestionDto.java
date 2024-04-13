package com.example.hanformproject.dto;

import com.example.hanformproject.entity.QuestionEntity;
import com.example.hanformproject.entity.SurveyEntity;
import com.example.hanformproject.entity.UserEntity;
import lombok.*;

import java.util.List;

@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
@Setter
public class QuestionDto {
    private Long questionId;
    private int questionNumber;
    private String questionText;
    private String questionType;
    private Boolean isRequired;
    private List<OptionDto> options;

    public QuestionEntity toEntity(SurveyEntity survey) {

        QuestionEntity question = new QuestionEntity();
        question.setSurvey(survey);
        question.setQuestionNumber(this.getQuestionNumber());
        question.setQuestionText(this.getQuestionText());
        question.setQuestionType(this.getQuestionType());
        question.setIsRequired(this.getIsRequired());

        return question;
    }
}