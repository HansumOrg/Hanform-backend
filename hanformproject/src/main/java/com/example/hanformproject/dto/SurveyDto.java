package com.example.hanformproject.dto;

import com.example.hanformproject.entity.SurveyEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
public class SurveyDto {
    private Long surveyId;
    private Long userId;
    private String title;
    private String creationDate; // LocalDate를 String으로 처리

    public static SurveyDto creatSurveyDto(SurveyEntity surveyEntity){
        //Format함수 Timestamp -> String

        String formattedDate = formatTimestampToString(surveyEntity.getCreationDate());

        return new SurveyDto(
                surveyEntity.getSurveyId(),
                surveyEntity.getUserEntity().getUserId(),
                surveyEntity.getSurveyTitle(),
                formattedDate
        );
    }

    public static String formatTimestampToString(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }
}
