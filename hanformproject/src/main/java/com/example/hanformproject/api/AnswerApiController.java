package com.example.hanformproject.api;

import com.example.hanformproject.dto.AnswerDto;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/api/{userId}/surveys/{surveyId}/responses")
    public ResponseEntity<?> submitAnswer(@PathVariable Long userId,
                                          @PathVariable Long surveyId,
                                          @RequestBody AnswerDto answerDto) {

        // 존재하지 않는 유저이거나 설문지면 에러 반환
        if (!userRepository.existsById(userId) || !surveyRepository.existsById(surveyId)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Missing required information or invalid survey/questionId.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        // 각 답변에 대해 저장하기.
        // 작성한 유저와 설문지 정보 가져오기
        UserEntity user = userRepository.findById(userId).orElse(null);
        SurveyEntity survey = surveyRepository.findById(surveyId).orElse(null);

        List<AnswerEntity> answers = new ArrayList<>();
        for (AnswerDto.AnswerDetail detail : answerDto.getResponses()) {
            QuestionEntity question = questionRepository.findById(detail.getQuestionId()).orElse(null);
            // question과 survey 비교해서 다르면 badRequest.
            if (question == null || !question.getSurvey().equals(survey)) {
                return ResponseEntity.badRequest().body("questionId 잘못됨");
            }
            // Entity로 변환 후 저장.
            answers.add(detail.toEntity(user, question));
        }

        answerRepository.saveAll(answers);
        return ResponseEntity.ok("저장 완료");
    }

}
