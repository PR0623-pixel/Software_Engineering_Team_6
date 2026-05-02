-- 관리자 계정
INSERT IGNORE INTO users (email, password, nickname, status) VALUES
('admin@vibevoca.com', 'admin1234', '관리자', 'ACTIVE');

-- 테스트 사용자 계정
INSERT IGNORE INTO users (email, password, nickname, status) VALUES
('test@vibevoca.com', 'user1234', '테스트유저', 'ACTIVE');

-- TOEIC 기본 단어 50개
INSERT IGNORE INTO words (word, meaning, part_of_speech, difficulty, etymology, pronunciation) VALUES
('abundant',     '풍부한',             'adjective',     2, 'ab(떨어져) + und(물결) → 넘쳐흐르다',          '/əˈbʌndənt/'),
('acquire',      '얻다, 습득하다',      'verb',          2, 'ad(향해) + quire(찾다)',                       '/əˈkwaɪər/'),
('advocate',     '지지하다, 지지자',    'verb/noun',     3, 'ad(향해) + voc(목소리) → 목소리를 내다',       '/ˈædvəkət/'),
('allocate',     '할당하다',            'verb',          3, 'al + loc(장소) → 장소를 지정하다',             '/ˈæləkeɪt/'),
('alternative',  '대안, 대체의',        'noun/adjective',2, 'alter(다른) → 다른 선택',                      '/ɔːlˈtɜːrnətɪv/'),
('anticipate',   '예상하다',            'verb',          2, 'anti(앞에) + cip(잡다) → 미리 잡다',           '/ænˈtɪsɪpeɪt/'),
('apparent',     '명백한',              'adjective',     2, 'ap + par(나타나다) → 눈에 보이는',             '/əˈpærənt/'),
('assess',       '평가하다',            'verb',          2, 'as + sess(앉다) → 앉아서 평가하다',            '/əˈses/'),
('assign',       '배정하다',            'verb',          1, 'as + sign(표시) → 표시해 배정하다',            '/əˈsaɪn/'),
('authorize',    '승인하다',            'verb',          2, 'author(작자) → 권한을 부여하다',               '/ˈɔːθəraɪz/'),
('beneficial',   '이로운',              'adjective',     2, 'bene(좋음) + fic(만들다) → 좋게 만드는',      '/ˌbenɪˈfɪʃəl/'),
('budget',       '예산',                'noun',          1, 'bougette(가죽 가방) → 예산 가방',              '/ˈbʌdʒɪt/'),
('candidate',    '후보자',              'noun',          1, 'cand(흰색) → 흰옷 입은 사람(로마 관습)',       '/ˈkændɪdət/'),
('capacity',     '수용 능력, 역량',     'noun',          2, 'cap(잡다) → 담을 수 있는 양',                  '/kəˈpæsɪti/'),
('collaborate',  '협력하다',            'verb',          2, 'col(함께) + labor(일) → 함께 일하다',          '/kəˈlæbəreɪt/'),
('commence',     '시작하다',            'verb',          2, 'com + menc(시작) → 시작하다',                  '/kəˈmens/'),
('compensate',   '보상하다',            'verb',          2, 'com + pens(무게달다) → 균형 맞추다',           '/ˈkɒmpenseɪt/'),
('comply',       '따르다, 준수하다',    'verb',          2, 'com + ply(채우다) → 요구를 채우다',            '/kəmˈplaɪ/'),
('comprehensive','포괄적인',            'adjective',     3, 'com + prehend(잡다) → 모두 잡아 담음',         '/ˌkɒmprɪˈhensɪv/'),
('conclude',     '결론 내리다',         'verb',          1, 'con + clud(닫다) → 마무리 짓다',               '/kənˈkluːd/'),
('conduct',      '수행하다, 행동',      'verb/noun',     1, 'con + duct(이끌다) → 함께 이끌다',             '/ˈkɒndʌkt/'),
('confirm',      '확인하다',            'verb',          1, 'con + firm(단단한) → 굳히다',                  '/kənˈfɜːrm/'),
('consecutive',  '연속적인',            'adjective',     3, 'con + secut(따르다) → 연달아 따르는',          '/kənˈsekjətɪv/'),
('contract',     '계약, 계약하다',      'noun/verb',     1, 'con + tract(끌다) → 함께 당기다',              '/ˈkɒntrækt/'),
('deadline',     '마감일',              'noun',          1, NULL,                                           '/ˈdedlaɪn/'),
('decline',      '감소하다, 거절하다',  'verb',          1, 'de(아래) + cline(기울다) → 아래로 기울다',     '/dɪˈklaɪn/'),
('dedicate',     '헌신하다',            'verb',          2, 'de + dic(선언) → 바치다',                      '/ˈdedɪkeɪt/'),
('delay',        '지연시키다',          'verb',          1, 'de(뒤) + lay(놓다) → 뒤로 놓다',              '/dɪˈleɪ/'),
('demonstrate',  '시연하다, 증명하다',  'verb',          2, 'de + monstr(보이다) → 분명히 보이다',          '/ˈdemənstreɪt/'),
('distribute',   '배포하다',            'verb',          2, 'dis(나뉘어) + tribute(주다) → 나눠 주다',     '/dɪˈstrɪbjuːt/'),
('efficient',    '효율적인',            'adjective',     1, 'ef + fic(만들다) → 잘 만들어 내는',            '/ɪˈfɪʃənt/'),
('eliminate',    '제거하다',            'verb',          2, 'e + limin(문지방) → 문밖으로 내보내다',        '/ɪˈlɪmɪneɪt/'),
('emphasis',     '강조',                'noun',          2, 'em + phas(보이다) → 두드러지게 보이다',        '/ˈemfəsɪs/'),
('establish',    '설립하다',            'verb',          1, 'e + stab(서다) → 세우다',                      '/ɪˈstæblɪʃ/'),
('evaluate',     '평가하다',            'verb',          2, 'e + val(가치) → 가치를 매기다',                '/ɪˈvæljueɪt/'),
('expansion',    '확장',                'noun',          2, 'ex(밖으로) + pand(펼치다) → 펼쳐 나감',       '/ɪkˈspænʃən/'),
('facilitate',   '용이하게 하다',       'verb',          3, 'facil(쉬운) → 쉽게 만들다',                   '/fəˈsɪlɪteɪt/'),
('flexible',     '유연한',              'adjective',     1, 'flex(구부리다) → 구부릴 수 있는',              '/ˈfleksɪbəl/'),
('generate',     '생성하다',            'verb',          1, 'gen(낳다) → 만들어 내다',                      '/ˈdʒenəreɪt/'),
('implement',    '실행하다',            'verb',          2, 'im + plement(채우다) → 채워서 실행하다',       '/ˈɪmplɪment/'),
('indicate',     '나타내다',            'verb',          1, 'in + dic(가리키다) → 가리켜 보이다',           '/ˈɪndɪkeɪt/'),
('inevitable',   '불가피한',            'adjective',     3, 'in(부정) + evit(피하다) → 피할 수 없는',      '/ɪnˈevɪtəbəl/'),
('inspection',   '검사',                'noun',          2, 'in + spec(보다) → 안을 들여다보다',            '/ɪnˈspekʃən/'),
('negotiate',    '협상하다',            'verb',          2, 'neg + otium(여가) → 여가 없이 일하다',         '/nɪˈɡoʊʃieɪt/'),
('obligation',   '의무',                'noun',          2, 'ob + lig(묶다) → 묶여 된 것',                  '/ˌɒblɪˈɡeɪʃən/'),
('obtain',       '얻다',                'verb',          1, 'ob + tain(잡다) → 손에 잡다',                  '/əbˈteɪn/'),
('productive',   '생산적인',            'adjective',     1, 'pro + duc(이끌다) → 앞으로 이끌어 내는',      '/prəˈdʌktɪv/'),
('profitable',   '수익성 있는',         'adjective',     1, 'profit(이익) → 이익이 되는',                   '/ˈprɒfɪtəbəl/'),
('promote',      '홍보하다, 승진시키다','verb',          1, 'pro(앞으로) + mot(움직이다) → 앞으로 나아가다','/prəˈmoʊt/'),
('qualify',      '자격을 갖추다',       'verb',          1, 'qual(이다) → 어떤 기준을 충족하다',           '/ˈkwɒlɪfaɪ/');

-- 예문 삽입
INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'The region has abundant natural resources.', '그 지역은 천연자원이 풍부하다.'
FROM words w WHERE w.word = 'abundant';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'She acquired new skills through online courses.', '그녀는 온라인 강좌를 통해 새 기술을 습득하다.'
FROM words w WHERE w.word = 'acquire';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'The manager allocated the budget to each department.', '관리자는 예산을 각 부서에 할당하다.'
FROM words w WHERE w.word = 'allocate';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'Regular exercise is beneficial for your health.', '규칙적인 운동은 건강에 이롭다.'
FROM words w WHERE w.word = 'beneficial';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'The meeting will commence at 9 a.m.', '회의는 오전 9시에 시작됩니다.'
FROM words w WHERE w.word = 'commence';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'We need an efficient way to process the data.', '우리는 데이터를 처리할 효율적인 방법이 필요하다.'
FROM words w WHERE w.word = 'efficient';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'Our schedule is flexible enough to make changes.', '우리 일정은 변경할 수 있을 만큼 유연하다.'
FROM words w WHERE w.word = 'flexible';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'The software update will be implemented next week.', '소프트웨어 업데이트는 다음 주에 실행됩니다.'
FROM words w WHERE w.word = 'implement';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'A productive meeting leads to clear action items.', '생산적인 회의는 명확한 실행 항목으로 이어진다.'
FROM words w WHERE w.word = 'productive';

INSERT IGNORE INTO word_examples (word_id, example_en, example_ko)
SELECT w.id, 'You must qualify for the position before applying.', '지원하기 전에 해당 직책의 자격을 갖춰야 합니다.'
FROM words w WHERE w.word = 'qualify';