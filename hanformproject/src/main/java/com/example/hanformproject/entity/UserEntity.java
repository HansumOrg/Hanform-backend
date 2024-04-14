package com.example.hanformproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity // DB가 해당 객체를 인식 가능!
@AllArgsConstructor
@NoArgsConstructor // 디폴트 생성자를 추가!
@ToString
@Getter
@Setter
public class UserEntity {

    @Id // 대표값
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 알아서 1,2,3 ... 자동 생성
    private Long userId;

    @Column
    private String loginId;

    @Column
    private String nickname;

    @Setter // setPassword 기능 가지고 있음.
    @Column
    private String password;

    public void patch(UserEntity userEntity) {
        if (userEntity.loginId != null)
            this.loginId = userEntity.loginId;
        if (userEntity.nickname != null)
            this.nickname = userEntity.nickname;
        if (userEntity.password != null)
            this.password = userEntity.password;
    }

}
