package com.example.hanformproject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionId", nullable = false) // nullable = false null 값이면 안된다는 의미.
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY) // SurveyEntity에 실제로 접근해야할 때 fetch 한다는 의미.
    @JoinColumn(name = "surveyId", nullable = false)
    private SurveyEntity survey;

    @Column(name = "questionNumber", nullable = false)
    private int questionNumber;

    @Column(name = "questionText", nullable = false)
    private String questionText;

    @Column(name = "questionType", nullable = false)
    private String questionType;

    @Column(name = "isRequired", nullable = false)
    private Boolean isRequired;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionEntity> options = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnswerEntity> answers = new ArrayList<>();

}
