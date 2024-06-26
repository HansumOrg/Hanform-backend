package com.example.hanformproject.entity;

import com.example.hanformproject.dto.SurveyDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
@Getter
@Setter
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

    // @OneToMany 1:N 관계를 설정.
    // mappedBy 참조하는 필드 이름을 지정.
    // CascadeType.ALL 저장, 수정, 삭제할 때 연관된 데이터도 같이 처리되어야 함.
    // orphanRemoval 데이터 베이스 삭제시, 부모 가 삭제되면 자식도 같이 삭제됨.
    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionEntity> questions = new ArrayList<>();

    public void patchTitle(SurveyDto surveyDto){
        // 객체 갱싱
        if(surveyDto.getTitle() != null){
            this.surveyTitle = surveyDto.getTitle();
        }
    }
}
