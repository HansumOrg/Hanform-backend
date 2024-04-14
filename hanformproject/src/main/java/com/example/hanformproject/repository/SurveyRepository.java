package com.example.hanformproject.repository;

import com.example.hanformproject.entity.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

    //등록 된 모든 설문지 조회 기능
    @Override
    ArrayList<SurveyEntity> findAll();

}
