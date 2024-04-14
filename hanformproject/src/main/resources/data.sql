-- user 데이터 삽입
INSERT INTO user_entity(user_id, login_id, nickname, password)
VALUES (1, 'brad0809', '김동욱', '1234');

INSERT INTO user_entity(user_id, login_id, nickname, password)
VALUES (2, 'kzkz0319', '김지현', '1234');

INSERT INTO user_entity(user_id, login_id, nickname, password)
VALUES (3, 'jimi1031', '강예정', '1234');


-- survey 관련 데이터
-- survey_entity 데이터 삽입
INSERT INTO survey_entity(user_id, survey_title, creation_date)
VALUES (1, "This is title", "2023-10-05 15:20:45");

-- question_entity 데이터 삽입 (첫 번째 질문)
INSERT INTO question_entity (survey_id, question_number, question_text, question_type, is_required)
VALUES (LAST_INSERT_ID(), 1, '첫 번째 질문의 설명', '단답형', TRUE);

-- QuestionEntity 데이터 삽입 (두 번째 질문)
-- LAST_INSERT_ID()는 이전 삽입 동작에서 생성된 ID를 반환
-- 여기서는 두 번째 질문을 삽입하기 전에 LAST_INSERT_ID() 값을 변수에 저장해야 합니다.
SET @survey_id = LAST_INSERT_ID();

INSERT INTO question_entity (survey_id, question_number, question_text, question_type, is_required)
VALUES (@survey_id, 2, '두 번째 질문의 설명', '객관식', FALSE);

-- 첫 번째 옵션 삽입
-- 마찬가지로, LAST_INSERT_ID() 값을 저장해야 합니다.
SET @question2_id = LAST_INSERT_ID();

INSERT INTO option_entity (question_id, option_number, option_text)
VALUES (@question2_id, 1, 'number1');

-- 두 번째 옵션 삽입
INSERT INTO option_entity (question_id, option_number, option_text)
VALUES (@question2_id, 2, 'number2');