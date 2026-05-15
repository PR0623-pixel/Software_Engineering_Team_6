CREATE TABLE IF NOT EXISTS users (
    id          BIGINT          NOT NULL AUTO_INCREMENT                    COMMENT '자동 증가 고유 ID',
    email       VARCHAR(255)    NOT NULL UNIQUE                            COMMENT '로그인 이메일',
    password    VARCHAR(255)    NOT NULL                                   COMMENT '비밀번호',
    nickname    VARCHAR(50)     NOT NULL                                   COMMENT '사용자 닉네임',
    level       VARCHAR(30)     NOT NULL DEFAULT 'STARTER'                 COMMENT '유저 학습 레벨 (STARTER이면 테스트 미완료)',
    profile_img VARCHAR(500)    NULL                                       COMMENT '프로필 이미지 URL',
    status      VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE'                  COMMENT '계정 상태 (ACTIVE/INACTIVE)',
    role        VARCHAR(20)     NOT NULL DEFAULT 'USER'                    COMMENT '해당 계정의 관리자 권한 유무',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP         COMMENT '계정 생성 일시',
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP                COMMENT '마지막 수정 일시',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS words (
    id              BIGINT          NOT NULL AUTO_INCREMENT               COMMENT '자동 증가 고유 ID',
    english_word    VARCHAR(100)    NOT NULL UNIQUE                       COMMENT '영어 단어',
    korean_meaning  VARCHAR(100)    NOT NULL                              COMMENT '한국어 뜻',
    part_of_speech  VARCHAR(30)     NOT NULL                              COMMENT '품사 (NOUN/VERB/ADJECTIVE 등)',
    level           VARCHAR(30)     NOT NULL                              COMMENT '난이도 (NEWBIE/BEGINNER/INTERMEDIATE/ADVANCED/HIGHLEVEL)',
    created_at      DATETIME        NULL                                  COMMENT '등록 일시',
    updated_at      DATETIME        NULL                                  COMMENT '마지막 수정 일시',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS word_examples (
    id                  BIGINT          NOT NULL AUTO_INCREMENT   COMMENT '자동 증가 고유 ID',
    word_id             BIGINT          NOT NULL                  COMMENT 'words.id 참조',
    example_sentence    VARCHAR(255)    NOT NULL                  COMMENT '영어 예문',
    korean_translation  VARCHAR(255)    NOT NULL                  COMMENT '한국어 번역 예문',
    created_at          DATETIME        NULL                      COMMENT '등록 일시',
    updated_at          DATETIME        NULL                      COMMENT '마지막 수정 일시',
    PRIMARY KEY (id),
    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_results (
    id              BIGINT      NOT NULL AUTO_INCREMENT               COMMENT '자동 증가 고유 ID',
    user_id         BIGINT      NOT NULL                              COMMENT 'users.id 참조',
    score           INT         NOT NULL                              COMMENT '획득 점수',
    total_questions INT         NOT NULL                              COMMENT '총 문항 수',
    created_at      DATETIME    NULL                                  COMMENT '퀴즈 응시 일시',
    updated_at      DATETIME    NULL                                  COMMENT '마지막 수정 일시',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS error_notes (
    id               BIGINT          NOT NULL AUTO_INCREMENT               COMMENT '자동 증가 고유 ID',
    quiz_result_id   BIGINT          NOT NULL                              COMMENT 'quiz_results.id 참조',
    word_id          BIGINT          NOT NULL                              COMMENT 'words.id 참조',
    submitted_answer VARCHAR(100)    NULL                                  COMMENT '제출한 오답',
    created_at       DATETIME        NULL                                  COMMENT '오답 등록 일시',
    updated_at       DATETIME        NULL                                  COMMENT '마지막 수정 일시',
    PRIMARY KEY (id),
    FOREIGN KEY (quiz_result_id) REFERENCES quiz_results(id) ON DELETE CASCADE,
    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE
);
