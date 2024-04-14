package com.example.hanformproject.api;

import com.example.hanformproject.dto.OptionDto;
import com.example.hanformproject.dto.QuestionDto;
import com.example.hanformproject.dto.SurveyDto;
import com.example.hanformproject.entity.OptionEntity;
import com.example.hanformproject.entity.QuestionEntity;
import com.example.hanformproject.entity.SurveyEntity;
import com.example.hanformproject.entity.UserEntity;
import com.example.hanformproject.repository.OptionRepository;
import com.example.hanformproject.repository.QuestionRepository;
import com.example.hanformproject.repository.SurveyRepository;
import com.example.hanformproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import static com.example.hanformproject.dto.SurveyDto.formatTimestampToString;

@Slf4j
@RestController
public class SurveyApiController {

    @Autowired
    private SurveyRepository surveyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private OptionRepository optionRepository;

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
        dtos = surveys.stream().map(this::convertEntityToDto).collect(Collectors.toList());

        //3. 반환 시 dtos와 URL에서 받은 userId를 같이 반환
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("surveys", dtos);

        return ResponseEntity.ok(response);
    }

    //상대방 설문지 조회 기능
    @GetMapping("/api/{userId}/surveys/{surveyId}")
    public ResponseEntity<Object> showQuestions(@PathVariable Long userId, @PathVariable Long surveyId) {

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
        Optional<SurveyEntity> surveyOpt = surveyRepository.findById(surveyId);
        if (surveyOpt.isEmpty()) {  // Optional이 비어있는지 직접 확인
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "NotFound");
            errorResponse.put("message", "해당 설문지가 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        //2. SurveyEntity -> SurveyDTo 변환
        SurveyEntity survey = surveyOpt.get();
        SurveyDto surveyDto = convertEntityToDto(survey);

        //3. 반환 시 surveyDto와 URL에서 받은 userId를 같이 반환
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("survey", surveyDto);

        return ResponseEntity.ok(response);
    }

    //SurveyEntity를 SurveyDto로 변경하는 함수.
    //이 함수를 SurveyDto 클래스에 정의하고 싶었으나 자바 문법 한계로 인해서 실패
    private SurveyDto convertEntityToDto(SurveyEntity survey) {
        List<QuestionDto> questionDtos = survey.getQuestions().stream()
                .map(question -> {
                    List<OptionDto> optionDtos = question.getOptions().stream()
                            .map(option -> new OptionDto(
                                    option.getOptionId(),
                                    option.getQuestion().getQuestionId(),
                                    option.getOptionNumber(),
                                    option.getOptionText()
                            )).collect(Collectors.toList());

                    return new QuestionDto(
                            question.getQuestionId(),
                            question.getQuestionNumber(),
                            question.getQuestionText(),
                            question.getQuestionType(),
                            question.getIsRequired(),
                            optionDtos  // 추가된 옵션 리스트
                    );
                }).collect(Collectors.toList());

        return new SurveyDto(
                survey.getSurveyId(),
                survey.getUserEntity().getUserId(),
                survey.getSurveyTitle(),
                formatTimestampToString(survey.getCreationDate()),
                questionDtos
        );
    }

    //설문지 생성 기능
    @Transactional
    @PostMapping("/api/{userId}/surveys")
    public ResponseEntity<Object> createSurvey(@PathVariable Long userId, @RequestBody SurveyDto surveyDto) {

        // 0. 유저 확인
        UserEntity user = userRepository.findByUserId(userId);
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "NotFound");
            errorResponse.put("message", "당신의 ID는 존재하지 않는 ID입니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        if (surveyDto.getTitle() == null || surveyDto.getTitle().isEmpty()) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "NotMatch");
            errorResponse.put("message", "입력 데이터가 올바르지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        // 1. dto -> entity
        SurveyEntity survey = surveyDto.toEntity(user);

        // 2. entity 저장
        survey = surveyRepository.save(survey);

        // 3. 자동으로 생성된 SurveyId 호출해서 Dto에 추가.
        surveyDto.setSurveyId(survey.getSurveyId());

        // 4. SurveyDto에 질문이 있다면 하나씩 DB에 저장
        if (surveyDto.getQuestions() != null && !surveyDto.getQuestions().isEmpty()) {
            SurveyEntity finalSurvey = survey;
            List<QuestionEntity> questions = surveyDto.getQuestions().stream().map(questionDto -> {

                // Question dto -> entity
                QuestionEntity question = questionDto.toEntity(finalSurvey);

                // Question 먼저 저장
                question = questionRepository.save(question);

                if (questionDto.getOptions() != null && !questionDto.getOptions().isEmpty()) {
                    // Question 저장하고 나서 Option 처리
                    QuestionEntity finalQuestion = question;
                    List<OptionEntity> options = questionDto.getOptions().stream().map(optionDto -> {
                        // Option dto -> entity
                        OptionEntity option = optionDto.toEntity(finalQuestion);
                        return option;

                    }).collect(Collectors.toList());

                    // Option 저장
                    optionRepository.saveAll(options);
                    question.setOptions(options);
                }
                return question;
            }).collect(Collectors.toList());
        }

        // 성공 응답
        return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<String, Object>() {{
            put("message", "Survey created successfully.");
            put("surveyId", surveyDto.getSurveyId());
            put("title", surveyDto.getTitle());
        }});
    }

    //설문지 제목 수정
    @PatchMapping("/api/{userId}/surveys/{surveyId}")
    public ResponseEntity<Object> updateSurveyTitle(@PathVariable Long userId, @PathVariable Long surveyId, @RequestBody SurveyDto surveyDto){

        //0. findById로 존재하는 설문지인지 확인
        SurveyEntity survey = surveyRepository.findById(surveyId).orElse(null);

        if(survey == null){
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "NotFound");
            errorResponse.put("message", "수정하려는 설문지는 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        //1. 유저 권한 확인
        UserEntity user = userRepository.findByUserId(userId);

        if (survey.getUserEntity().getUserId() != userId) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "NotFound");
            errorResponse.put("message", "당신은 ID는 수정 권한이 존재하지 않습니다.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        //2. 존재한다면 SurveyDto에서 제목부분만 업데이트
        survey.patch(surveyDto);
        surveyRepository.save(survey);

        log.info(survey.toString());

        //3. 변경된 설문지 제목 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<String, Object>() {{
            put("message", "Survey title updated successfully.");
            put("surveyId", surveyDto.getSurveyId());
            put("title", surveyDto.getTitle());
        }});
    }

    // 해당 설문지에 대한 질문지 수정
    @PutMapping("/api/{userId}/surveys/{surveyId}")
    public ResponseEntity<Object> updateSurveyQuestions(@PathVariable Long userId, @PathVariable Long surveyId, @RequestBody SurveyDto surveyDto){

        SurveyEntity survey = surveyRepository.findById(surveyId).orElse(null);

        survey.setSurveyTitle(surveyDto.getTitle());

        // Question and Option update logic
        updateQuestions(survey, surveyDto.getQuestions());

        surveyRepository.save(survey);

        return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<String, Object>() {{
            put("message", "Survey updated successfully.");
            put("surveyId", surveyDto.getSurveyId());
        }});
    }

    // 질문지 수정 시 기존 질문들을 모두 삭제하고 새로운 질문지들을 덮어 쓰는 메소드
    private void updateQuestions(SurveyEntity survey, List<QuestionDto> questionDtos) {
        // Temporary map to hold existing questions for quick lookup
        Map<Long, QuestionEntity> existingQuestions = survey.getQuestions().stream()
                .collect(Collectors.toMap(QuestionEntity::getQuestionId, question -> question));

        // Process incoming questions
        for (QuestionDto dto : questionDtos) {
            QuestionEntity question = existingQuestions.get(dto.getQuestionId());
            if (question == null) {
                // If the question does not exist, create a new one
                question = new QuestionEntity();
                survey.getQuestions().add(question);
            }
            question.setSurvey(survey);
            question.setQuestionNumber(dto.getQuestionNumber());
            question.setQuestionText(dto.getQuestionText());
            question.setQuestionType(dto.getQuestionType());
            question.setIsRequired(dto.getIsRequired());
            updateOptions(question, dto.getOptions());
        }

        // Optionally remove questions not included in the DTO
        survey.getQuestions().removeIf(question -> !questionDtos.stream()
                .map(QuestionDto::getQuestionId)
                .collect(Collectors.toList())
                .contains(question.getQuestionId()));
    }

    private void updateOptions(QuestionEntity question, List<OptionDto> optionDtos) {
        Map<Integer, OptionEntity> existingOptions = question.getOptions().stream()
                .collect(Collectors.toMap(OptionEntity::getOptionNumber, option -> option));

        for (OptionDto dto : optionDtos) {
            OptionEntity option = existingOptions.get(dto.getOptionNumber());
            if (option == null) {
                option = new OptionEntity();
                question.getOptions().add(option);
            }
            option.setQuestion(question);
            option.setOptionNumber(dto.getOptionNumber());
            option.setOptionText(dto.getOptionText());
        }

        question.getOptions().removeIf(option -> !optionDtos.stream()
                .map(OptionDto::getOptionNumber)
                .collect(Collectors.toList())
                .contains(option.getOptionNumber()));
    }
}
