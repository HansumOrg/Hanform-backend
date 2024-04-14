package com.example.hanformproject.api;

import com.example.hanformproject.dto.*;
import com.example.hanformproject.entity.AnswerEntity;
import com.example.hanformproject.entity.QuestionEntity;
import com.example.hanformproject.entity.SurveyEntity;
import com.example.hanformproject.entity.UserEntity;
import com.example.hanformproject.repository.AnswerRepository;
import com.example.hanformproject.repository.QuestionRepository;
import com.example.hanformproject.repository.SurveyRepository;
import com.example.hanformproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.hanformproject.dto.SurveyDto.formatTimestampToString;

@Slf4j
@RestController
public class AnswerApiController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    // 답변 제출
    @PostMapping("/api/{userId}/surveys/{surveyId}/responses")
    public ResponseEntity<?> submitAnswer(@PathVariable Long userId,
                                          @PathVariable Long surveyId,
                                          @RequestBody AnswerDto.ResponseWrapper responseWrapper) {

        List<AnswerDto> answerDto = responseWrapper.getResponses();
        SurveyEntity survey = surveyRepository.findById(surveyId).orElse(null);

        // answer값이 비어있다면 badRequest
        if (answerDto == null) {
            return ResponseEntity.badRequest().body("답이 비어있음");
        }

        // 유저 정보 가져오기
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("없는 계정");
        }

        // Dto -> Entity
        List<AnswerEntity> answerEntities = answerDto.stream().map(answer -> {
            QuestionEntity questionEntity = questionRepository.findById(answer.getQuestionId()).orElseThrow(
                    () -> new IllegalArgumentException("일치하는 질문 없음")
            );

            AnswerEntity answerEntity = new AnswerEntity();
            answerEntity.setUser(user);
            answerEntity.setQuestion(questionEntity);
            answerEntity.setSurvey(survey);
            answerEntity.setAnswerText(answer.getAnswerText());
            return answerEntity;
        }).collect(Collectors.toList());

        answerRepository.saveAll(answerEntities);
        return ResponseEntity.ok("답변 성공");

    }

    // 답변 조회
    @GetMapping("/api/{userId}/surveys/{surveyId}/details")
    public ResponseEntity<Object> getSurveyDetails(@PathVariable Long userId,
                                                   @PathVariable Long surveyId) {
        // Entity 가져오기
        SurveyEntity survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null) {
            return ResponseEntity.badRequest().body("Survey not found.");
        }

        // Entity -> DTO
        SurveyDto surveyDto = convertEntityToDto(survey);

        // 결과 반환.
        Map<String, Object> response = new HashMap<>();
        response.put("survey", surveyDto);
        return ResponseEntity.ok(response);
    }

    private SurveyDto convertEntityToDto(SurveyEntity survey) {
        List<QuestionDto> questionDtos = survey.getQuestions().stream()
                .map(question -> {
                    List<AnswerDto> answerDtos = question.getAnswers().stream()
                            .map(answer -> new AnswerDto(
                                    answer.getAnswerId(),
                                    answer.getUser().getUserId(),
                                    answer.getQuestion().getQuestionId(),
                                    answer.getSurvey().getSurveyId(),
                                    answer.getAnswerText()
                            )).collect(Collectors.toList());

                    return new QuestionDto(
                            question.getQuestionId(),
                            question.getQuestionNumber(),
                            question.getQuestionText(),
                            question.getQuestionType(),
                            question.getIsRequired(),
                            answerDtos  // 추가된 옵션 리스트
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
}
