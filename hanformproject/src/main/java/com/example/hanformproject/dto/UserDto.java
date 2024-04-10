package com.example.hanformproject.dto;

import com.example.hanformproject.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor // Generator 간소화
@ToString
public class UserDto {

    private Long userId;
    private String loginId;
    private String nickname;
    private String password;

    public UserEntity toEntity() {
        return new UserEntity(userId, loginId, nickname, password);
    }
}
