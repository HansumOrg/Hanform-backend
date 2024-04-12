package com.example.hanformproject.api;

import com.example.hanformproject.dto.SurveyDto;
import com.example.hanformproject.entity.SurveyEntity;
import com.example.hanformproject.entity.UserEntity;
import com.example.hanformproject.repository.SurveyRepository;
import com.example.hanformproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
public class SurveyApiController {

    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private UserRepository userRepository;

    //설문지 조회 기능
    @GetMapping("/api/{userId}/surveys")
    public ResponseEntity<Object> index(@PathVariable Long userId) {

        //0. 존재하는 유저인지 확인
        Optional<UserEntity> userEntity = userRepository.findById(userId);
        if (!userEntity.isPresent()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "NotFound");
            errorResponse.put("message", "당신의 ID는 존재하지 않는 ID입니다.");
            // {userId} 확인 후 존재하지 않은 유저이면 404 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        //1. Repository에 모든 설문지 조회
        List<SurveyEntity> surveys = surveyRepository.findAll();

        //2. 엔티티 -> DTO
        List<SurveyDto> dtos = new ArrayList<SurveyDto>();
        for (int i = 0; i < surveys.size(); i++) {
            SurveyEntity s = surveys.get(i);
            SurveyDto dto = new SurveyDto(s);
            dtos.add(dto);
        }

        //3. 반환 시 dtos와 URL에서 받은 userId를 같이 반환
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("surveys", dtos);

        return ResponseEntity.ok(response);
    }

    //설문지 생성 기능
    @PostMapping("/api/{userId}/surveys")
    public ResponseEntity<SurveyDto> createSurvey(@PathVariable Long userId, @RequestBody SurveyDto surveyDto){

        UserEntity user = userRepository.findByUserId(userId);

        SurveyEntity survey = new SurveyEntity();
        survey.setUserEntity(user);
        survey.setSurveyTitle(surveyDto.getTitle());
        survey.setCreationDate(convertStringToTimestamp(surveyDto.getCreationDate()));

        survey = surveyRepository.save(survey);

        surveyDto.setSurveyId(survey.getSurveyId());
        return ResponseEntity.ok(surveyDto);
    }

    private Timestamp convertStringToTimestamp(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(strDate, formatter);
        return Timestamp.valueOf(dateTime);
    }

}
