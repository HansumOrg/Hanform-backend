package com.example.hanformproject.repository;

import com.example.hanformproject.entity.AnswerEntity;
import com.example.hanformproject.entity.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
}
