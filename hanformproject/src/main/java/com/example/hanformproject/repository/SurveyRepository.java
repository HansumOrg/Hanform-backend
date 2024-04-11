package com.example.hanformproject.repository;

import com.example.hanformproject.entity.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

    @Override
    ArrayList<SurveyEntity> findAll();
}
