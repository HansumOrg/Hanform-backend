package com.example.hanformproject.dto;

import com.example.hanformproject.entity.SurveyEntity;
import com.example.hanformproject.entity.UserEntity;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
@Getter // 각 필드 값을 조회할 수 있는 getter 메서드 자동 생성
@ToString // 모든 필드를 출력할 수 있는 toString 메서드 자동 생성
@Setter
public class SurveyDto {
    private Long surveyId;
    private Long userId;
    private String title;
    private String creationDate; // LocalDate를 String으로 처리
    private List<QuestionDto> questions;

    public SurveyDto(Long surveyId, Long userId, String title, String creationDate){
        this.surveyId = surveyId;
        this.userId = userId;
        this.title = title;
        this.creationDate = creationDate;
    }

    public SurveyDto (SurveyEntity surveyEntity){

        String formattedDate = formatTimestampToString(surveyEntity.getCreationDate());

        this.surveyId = surveyEntity.getSurveyId();
        this.userId = surveyEntity.getUserEntity().getUserId();
        this.title = surveyEntity.getSurveyTitle();
        this.creationDate = formattedDate;
    }

    public SurveyEntity toEntity(UserEntity user){

        SurveyEntity survey = new SurveyEntity();
        survey.setUserEntity(user);
        survey.setSurveyTitle(this.getTitle());
        survey.setCreationDate(convertStringToTimestamp(this.getCreationDate()));
        return survey;
    }

    public Timestamp getTimeStampeCreationDate() {

        Timestamp timestamp = convertStringToTimestamp(this.creationDate);

        return timestamp;
    }


    //formatting 함수 Timestamp -> String
    public static String formatTimestampToString(Timestamp timestamp) {
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

    private Timestamp convertStringToTimestamp(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(strDate, formatter);
        return Timestamp.valueOf(dateTime);
    }

}
