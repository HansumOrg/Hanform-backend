package com.example.hanformproject.api;

import com.example.hanformproject.dto.UserDto;
import com.example.hanformproject.entity.UserEntity;
import com.example.hanformproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController // RestAPI용 컨트롤러! JSON을 반환
public class UserApiController {

    @Autowired
    private UserRepository userRepository;

    // 유저 등록
    @PostMapping("/api/users/register")
    public ResponseEntity<Object> register(@RequestBody UserDto userDto) {
        try {
            UserEntity user = userDto.toEntity();
            userRepository.save(user);

            // 성공 시 반환할 객체 생성
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "User registration successful.");

            Map<String, String> userResponse = new HashMap<>();
            userResponse.put("userId", user.getUserId().toString());
            userResponse.put("nickname", user.getNickname());
            successResponse.put("user", userResponse);

            return ResponseEntity.status(HttpStatus.OK).body(successResponse);

        } catch (Exception e) {
            // 실패 시 반환할 객체 생성
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "BadRequest");
            errorResponse.put("message", "Missing required fields or user ID already exists.");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

     // 로그인
    @PostMapping("/api/users/login")
    public ResponseEntity<Object> login(@RequestBody UserDto userDto) {

        try {
            UserEntity user = userDto.toEntity();
            // 아이디 검증
            Optional<UserEntity> target = userRepository.findByLoginId(user.getLoginId());
            // 아이디가 있다면
            if (target.isPresent()) {
                UserEntity target_account = target.get();
                // 비밀번호 검증
                if (target_account.getPassword().equals(user.getPassword())) {
                    Map<String, String> successResponse = new HashMap<>();
                    successResponse.put("message", "로그인 성공!");
                    successResponse.put("loginId", user.getLoginId());
                    return ResponseEntity.ok(successResponse);
                }
            }
            // 인증 실패 응답
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unauthorized");
            errorResponse.put("message", "아이디 또는 비밀번호가 잘못되었습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            // 내부 서버 오류 응답
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // ID 찾기
    @GetMapping("/api/users/id")
    public ResponseEntity<Object> getUserIdByNickname(@RequestParam("nickname") String nickname) {
        try {
            // nickname으로 사용자 검색
            Optional<UserEntity> target = userRepository.findByNickname(nickname);

            if (target.isPresent()) {
                UserEntity user = target.get();
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("message", "당신의 ID를 알려드립니다.");
                successResponse.put("nickname", nickname);
                successResponse.put("loginId", user.getLoginId());

                return ResponseEntity.ok(successResponse);
            } else {
                // 해당 닉네임을 가진 사용자를 찾지 못한 경우
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "NotFound");
                errorResponse.put("message", "해당 닉네임을 가진 사용자를 찾지 못했습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            // 내부 서버 오류 처리
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

    // ID와 닉네임으로 사용자 확인
    @GetMapping("/api/users/verification")
    public ResponseEntity<Object> verifyAccount(@RequestParam("loginId") String loginId,
                                                @RequestParam("nickname") String nickname) {
        try {
            // loginId와 nickname으로 사용자 검색
            Optional<UserEntity> target = userRepository.findByLoginIdAndNickname(loginId, nickname);

            if (target.isPresent()) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("message", "계정 확인됨.");
                successResponse.put("loginId", loginId);
                successResponse.put("nickname", nickname);
                successResponse.put("verificationStatus", true);

                return ResponseEntity.ok(successResponse);
            } else {
                // 해당 loginId와 nickname을 가진 사용자를 찾지 못한 경우
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "NotFound");
                errorResponse.put("message", "계정 확인 안됨.");
                errorResponse.put("verificationStatus", false);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            // 내부 서버 오류 처리
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "서버 오류");
            errorResponse.put("verificationStatus", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // 비밀번호 재설정
    @PutMapping("/api/{loginId}/password")
    public ResponseEntity<Object> resetPassword(@PathVariable("loginId") String loginId,
                                                @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");

        // 비밀번호 요구사항은 Front에서 검증??

        try {
            // loginId로 사용자 검색
            Optional<UserEntity> target = userRepository.findByLoginId(loginId);
            if (target.isPresent()) {
                UserEntity user = target.get();
                user.setPassword(newPassword);
                userRepository.save(user);

                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("message", "비밀번호 재설정 완료");
                successResponse.put("loginId", loginId);
                return ResponseEntity.ok(successResponse);
            } else {
                // 사용자가 존재하지 않는 경우
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "NotFound");
                errorResponse.put("message", "유저가 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            // 내부 서버 오류 처리
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // 사용자 닉네임 조회
    @GetMapping("/api/{loginId}/nickname")
    public ResponseEntity<Object> getNickname(@PathVariable("loginId") String loginId) {
        try {
            // userId로 사용자 검색
            Optional<UserEntity> target = userRepository.findByLoginId(loginId);
            if (target.isPresent()) {
                UserEntity user = target.get();
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("loginId", loginId);
                successResponse.put("nickname", user.getNickname());

                return ResponseEntity.ok(successResponse);
            } else {
                // 해당 loginId를 가진 사용자를 찾지 못한 경우
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "NotFound");
                errorResponse.put("message", "해당 ID의 사용자가 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            // 내부 서버 오류 처리
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // 회원 탈퇴
    @DeleteMapping("/api/{loginId}")
    public ResponseEntity<Object> deleteUser(@PathVariable("loginId") String loginId) {
        try {
            // loginId로 사용자 존재 여부 확인
            Optional<UserEntity> target = userRepository.findByLoginId(loginId);
            if (target.isPresent()) {
                // 사용자가 존재하는 경우, 해당 사용자 정보 삭제
                userRepository.delete(target.get());
                Map<String, String> successResponse = new HashMap<>();
                successResponse.put("message", "유저 정보 삭제.");
                successResponse.put("loginId", loginId);

                return ResponseEntity.ok(successResponse);
            } else {
                // 해당 loginId를 가진 사용자를 찾지 못한 경우
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "NotFound");
                errorResponse.put("message", "해당 사용자 찾지 못함.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            // 내부 서버 오류 처리
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", "서버 오류");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


}
