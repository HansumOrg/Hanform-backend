package com.example.hanformproject.repository;

import com.example.hanformproject.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
    @Override
    ArrayList<UserEntity> findAll();

    Optional<UserEntity> findByLoginId(String loginId);

    Optional<UserEntity> findByNickname(String nickname);

    Optional<UserEntity> findByLoginIdAndNickname(String loginId, String nickname);
}
