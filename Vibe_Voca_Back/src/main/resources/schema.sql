CREATE TABLE IF NOT EXISTS users (
    id          BIGINT          NOT NULL AUTO_INCREMENT                    COMMENT '자동 증가 고유 ID',
    email       VARCHAR(255)    NOT NULL UNIQUE                            COMMENT '로그인 이메일',
    password    VARCHAR(255)    NOT NULL                                   COMMENT '비밀번호',
    nickname    VARCHAR(50)     NOT NULL                                   COMMENT '사용자 닉네임',
    profile_img VARCHAR(500)    NULL                                       COMMENT '프로필 이미지 URL',
    status      ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE'      COMMENT '계정 상태',
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP         COMMENT '계정 생성 일시',
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                ON UPDATE CURRENT_TIMESTAMP                COMMENT '마지막 수정 일시',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS words (
    id              BIGINT          NOT NULL AUTO_INCREMENT               COMMENT '자동 증가 고유 ID',
    word            VARCHAR(100)    NOT NULL UNIQUE                       COMMENT '영어 단어',
    meaning         VARCHAR(255)    NOT NULL                              COMMENT '한국어 뜻',
    part_of_speech  VARCHAR(30)     NOT NULL                              COMMENT '품사',
    difficulty      TINYINT         NOT NULL DEFAULT 1                    COMMENT '난이도 1~5단계',
    etymology       TEXT            NULL                                  COMMENT '어원 설명',
    pronunciation   VARCHAR(100)    NULL                                  COMMENT 'IPA 발음기호',
    audio_url       VARCHAR(500)    NULL                                  COMMENT '발음 오디오 URL',
    image_url       VARCHAR(500)    NULL                                  COMMENT '단어 이미지 URL',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '등록 일시',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP           COMMENT '마지막 수정 일시',
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS word_examples (
    id              BIGINT          NOT NULL AUTO_INCREMENT   COMMENT '자동 증가 고유 ID',
    word_id         BIGINT          NOT NULL                  COMMENT 'words.id 참조',
    example_en      VARCHAR(500)    NOT NULL                  COMMENT '영어 예문',
    example_ko      VARCHAR(500)    NOT NULL                  COMMENT '한국어 번역 예문',
    PRIMARY KEY (id),
    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS quiz_results (
    id          BIGINT      NOT NULL AUTO_INCREMENT               COMMENT '자동 증가 고유 ID',
    user_id     BIGINT      NOT NULL                              COMMENT 'users.id 참조',
    word_id     BIGINT      NOT NULL                              COMMENT 'words.id 참조',
    is_correct  BOOLEAN     NOT NULL                              COMMENT '정답 여부 (true/false)',
    quiz_date   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '퀴즈 응시 일시',
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS error_notes (
    id          BIGINT      NOT NULL AUTO_INCREMENT               COMMENT '자동 증가 고유 ID',
    user_id     BIGINT      NOT NULL                              COMMENT 'users.id 참조',
    word_id     BIGINT      NOT NULL                              COMMENT 'words.id 참조',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP    COMMENT '오답 등록 일시',
    PRIMARY KEY (id),
    UNIQUE KEY uq_error_note (user_id, word_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (word_id) REFERENCES words(id) ON DELETE CASCADE
);
