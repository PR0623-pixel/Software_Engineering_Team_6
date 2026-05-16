-- 관리자 계정
INSERT IGNORE INTO users (email, password, nickname, status, role) VALUES
('admin@vibevoca.com', 'admin1234', '관리자', 'ACTIVE', 'ADMIN');

-- 테스트 사용자 계정
INSERT IGNORE INTO users (email, password, nickname, status, role) VALUES
('test@vibevoca.com', 'user1234', '테스트유저', 'ACTIVE', 'USER');

-- TOEIC 기본 단어 50개 (english_word, korean_meaning, part_of_speech, level)
INSERT IGNORE INTO words (english_word, korean_meaning, part_of_speech, level) VALUES
('abundant',      '풍부한',             'ADJECTIVE',  'BEGINNER'),
('acquire',       '얻다, 습득하다',      'VERB',       'BEGINNER'),
('advocate',      '지지하다, 지지자',    'VERB',       'INTERMEDIATE'),
('allocate',      '할당하다',            'VERB',       'INTERMEDIATE'),
('alternative',   '대안, 대체의',        'NOUN',       'BEGINNER'),
('anticipate',    '예상하다',            'VERB',       'BEGINNER'),
('apparent',      '명백한',              'ADJECTIVE',  'BEGINNER'),
('assess',        '평가하다',            'VERB',       'BEGINNER'),
('assign',        '배정하다',            'VERB',       'NEWBIE'),
('authorize',     '승인하다',            'VERB',       'BEGINNER'),
('beneficial',    '이로운',              'ADJECTIVE',  'BEGINNER'),
('budget',        '예산',                'NOUN',       'NEWBIE'),
('candidate',     '후보자',              'NOUN',       'NEWBIE'),
('capacity',      '수용 능력, 역량',     'NOUN',       'BEGINNER'),
('collaborate',   '협력하다',            'VERB',       'BEGINNER'),
('commence',      '시작하다',            'VERB',       'BEGINNER'),
('compensate',    '보상하다',            'VERB',       'BEGINNER'),
('comply',        '따르다, 준수하다',    'VERB',       'BEGINNER'),
('comprehensive', '포괄적인',            'ADJECTIVE',  'INTERMEDIATE'),
('conclude',      '결론 내리다',         'VERB',       'NEWBIE'),
('conduct',       '수행하다, 행동',      'VERB',       'NEWBIE'),
('confirm',       '확인하다',            'VERB',       'NEWBIE'),
('consecutive',   '연속적인',            'ADJECTIVE',  'INTERMEDIATE'),
('contract',      '계약, 계약하다',      'NOUN',       'NEWBIE'),
('deadline',      '마감일',              'NOUN',       'NEWBIE'),
('decline',       '감소하다, 거절하다',  'VERB',       'NEWBIE'),
('dedicate',      '헌신하다',            'VERB',       'BEGINNER'),
('delay',         '지연시키다',          'VERB',       'NEWBIE'),
('demonstrate',   '시연하다, 증명하다',  'VERB',       'BEGINNER'),
('distribute',    '배포하다',            'VERB',       'BEGINNER'),
('efficient',     '효율적인',            'ADJECTIVE',  'NEWBIE'),
('eliminate',     '제거하다',            'VERB',       'BEGINNER'),
('emphasis',      '강조',                'NOUN',       'BEGINNER'),
('establish',     '설립하다',            'VERB',       'NEWBIE'),
('evaluate',      '평가하다',            'VERB',       'BEGINNER'),
('expansion',     '확장',                'NOUN',       'BEGINNER'),
('facilitate',    '용이하게 하다',       'VERB',       'INTERMEDIATE'),
('flexible',      '유연한',              'ADJECTIVE',  'NEWBIE'),
('generate',      '생성하다',            'VERB',       'NEWBIE'),
('implement',     '실행하다',            'VERB',       'BEGINNER'),
('indicate',      '나타내다',            'VERB',       'NEWBIE'),
('inevitable',    '불가피한',            'ADJECTIVE',  'INTERMEDIATE'),
('inspection',    '검사',                'NOUN',       'BEGINNER'),
('negotiate',     '협상하다',            'VERB',       'BEGINNER'),
('obligation',    '의무',                'NOUN',       'BEGINNER'),
('obtain',        '얻다',                'VERB',       'NEWBIE'),
('productive',    '생산적인',            'ADJECTIVE',  'NEWBIE'),
('profitable',    '수익성 있는',         'ADJECTIVE',  'NEWBIE'),
('promote',       '홍보하다, 승진시키다','VERB',        'NEWBIE'),
('qualify',       '자격을 갖추다',       'VERB',       'NEWBIE');

-- 예문 삽입 (example_sentence, korean_translation)
INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'The region has abundant natural resources.', '그 지역은 천연자원이 풍부하다.'
FROM words w WHERE w.english_word = 'abundant';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'She acquired new skills through online courses.', '그녀는 온라인 강좌를 통해 새 기술을 습득하다.'
FROM words w WHERE w.english_word = 'acquire';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'The manager allocated the budget to each department.', '관리자는 예산을 각 부서에 할당하다.'
FROM words w WHERE w.english_word = 'allocate';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'Regular exercise is beneficial for your health.', '규칙적인 운동은 건강에 이롭다.'
FROM words w WHERE w.english_word = 'beneficial';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'The meeting will commence at 9 a.m.', '회의는 오전 9시에 시작됩니다.'
FROM words w WHERE w.english_word = 'commence';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'We need an efficient way to process the data.', '우리는 데이터를 처리할 효율적인 방법이 필요하다.'
FROM words w WHERE w.english_word = 'efficient';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'Our schedule is flexible enough to make changes.', '우리 일정은 변경할 수 있을 만큼 유연하다.'
FROM words w WHERE w.english_word = 'flexible';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'The software update will be implemented next week.', '소프트웨어 업데이트는 다음 주에 실행됩니다.'
FROM words w WHERE w.english_word = 'implement';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'A productive meeting leads to clear action items.', '생산적인 회의는 명확한 실행 항목으로 이어진다.'
FROM words w WHERE w.english_word = 'productive';

INSERT IGNORE INTO word_examples (word_id, example_sentence, korean_translation)
SELECT w.id, 'You must qualify for the position before applying.', '지원하기 전에 해당 직책의 자격을 갖춰야 합니다.'
FROM words w WHERE w.english_word = 'qualify';
