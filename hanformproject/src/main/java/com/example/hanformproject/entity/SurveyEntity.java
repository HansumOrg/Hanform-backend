package com.example.hanformproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

@Entity
@ToString
@Getter
@AllArgsConstructor // 모든 필드를 매개변수로 갖는 생성자 자동 생성
@NoArgsConstructor // 매개변수가 아예 없는 기본 생성자 자동 생성
public class SurveyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "surveyId")
    private Long surveyId;

    @ManyToOne // user와 설문지는 1:N 관계이기 때문에 @ManyToOne 어노테이션 사용
    @JoinColumn(name = "userId")
    private UserEntity userEntity;

    @Column(name = "surveyTitle")
    private String surveyTitle;

    @Column(name = "creationDate")
    private Timestamp creationDate;

}
