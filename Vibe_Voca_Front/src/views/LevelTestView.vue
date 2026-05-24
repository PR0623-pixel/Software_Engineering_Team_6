<template>
  <div class="quiz-page">
    <NavBar />
    <div class="quiz-content">

      <!-- 1. Intro -->
      <div v-if="phase === 'intro'" class="phase-box">
        <header class="quiz-header">
          <h1 class="page-title">레벨 테스트</h1>
          <p class="page-sub">나의 영어 수준을 측정하는 15문제 테스트예요</p>
        </header>
        <div class="intro-info">
          <div class="info-row">
            <span class="info-icon">📝</span>
            <span>총 15문제 (레벨별 3문제씩)</span>
          </div>
          <div class="info-row">
            <span class="info-icon">🎯</span>
            <span>한국어 뜻을 보고 영어 단어를 입력하세요</span>
          </div>
          <div class="info-row">
            <span class="info-icon">⚡</span>
            <span>완료 후 나의 레벨이 자동으로 설정됩니다</span>
          </div>
        </div>
        <p v-if="loadError" class="error-msg">{{ loadError }}</p>
        <button class="btn-primary" :disabled="loading" @click="startTest">
          {{ loading ? '문제 불러오는 중...' : '테스트 시작하기' }}
        </button>
      </div>

      <!-- 2. Quiz -->
      <div v-else-if="phase === 'quiz'" class="phase-box">
        <div class="quiz-status">
          <div class="progress-row">
            <span class="progress-text">{{ currentIndex + 1 }} / {{ questions.length }}</span>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: progressPct + '%' }"></div>
            </div>
          </div>

          <div class="timer-card" :class="{ urgent: remainingSeconds <= 10 }">
            <span class="timer-label">남은 시간</span>
            <strong>{{ formattedRemainingTime }}</strong>
            <div class="timer-track">
              <div class="timer-fill" :style="{ width: timerPct + '%' }"></div>
            </div>
          </div>
        </div>

        <div v-if="timeExpiredNotice" class="timeout-notice">
          시간이 종료되어 남은 문제는 오답으로 처리되었습니다.
        </div>

        <div class="question-card">
          <p class="q-label">이 뜻에 해당하는 영어 단어는?</p>
          <p class="q-word">{{ currentQ.questionMeaning }}</p>

          <div class="input-row">
            <input
              ref="answerInput"
              v-model="typedAnswer"
              class="answer-input"
              :class="{ 'input-correct': isAnswered && isCorrect, 'input-wrong': isAnswered && !isCorrect }"
              type="text"
              placeholder="영어 단어를 입력하세요"
              :disabled="isAnswered"
              @keydown.enter="submitAnswer"
            />
            <button
              v-if="!isAnswered"
              class="btn-submit"
              :disabled="!typedAnswer.trim()"
              @click="submitAnswer"
            >확인</button>
          </div>

          <div v-if="isAnswered" class="result-badge" :class="isCorrect ? 'correct' : 'wrong'">
            <template v-if="isCorrect">✓ 정답!</template>
            <template v-else>✗ 오답 &mdash; 정답: <strong>{{ currentQ.correctAnswer }}</strong></template>
          </div>

          <button v-if="isAnswered" class="btn-primary" @click="nextQuestion">
            {{ currentIndex + 1 < questions.length ? '다음 문제 →' : '결과 보기' }}
          </button>
        </div>
      </div>

      <!-- 3. Result -->
      <div v-else-if="phase === 'result'" class="phase-box result-box">
        <div class="score-wrap">
          <div class="score-circle" :class="scoreClass">
            <span class="score-num">{{ correctCount }}</span>
            <span class="score-total">/ {{ questions.length }}</span>
          </div>
          <p class="score-msg">{{ correctCount }}개 정답</p>
        </div>

        <div class="level-result">
          <p class="level-label">나의 레벨</p>
          <div class="level-badge" :class="levelClass">{{ levelDisplay }}</div>
          <p class="level-desc">{{ levelDesc }}</p>
        </div>

        <p v-if="saveError" class="error-msg">{{ saveError }}</p>

        <button class="btn-primary" :disabled="saving" @click="goToMain">
          {{ saving ? '저장 중...' : '메인으로 돌아가기' }}
        </button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import NavBar from '../components/NavBar.vue';
import api from '../api/axios';

const router = useRouter();

const phase = ref('intro');
const loading = ref(false);
const loadError = ref('');
const saveError = ref('');

const questions = ref([]);
const currentIndex = ref(0);
const typedAnswer = ref('');
const isAnswered = ref(false);
const isCorrect = ref(false);
const correctCount = ref(0);
const answers = ref([]);
const userId = ref(null);
const saving = ref(false);

const LEVEL_TEST_SECONDS = 180;
const totalTimerSeconds = ref(0);
const remainingSeconds = ref(0);
const timerId = ref(null);
const timeExpiredNotice = ref(false);

const answerInput = ref(null);

const currentQ = computed(() => questions.value[currentIndex.value] ?? {});
const progressPct = computed(() => ((currentIndex.value + 1) / questions.value.length) * 100);

const determinedLevel = computed(() => {
  const s = correctCount.value;
  if (s >= 13) return 'HIGHLEVEL';
  if (s >= 10) return 'ADVANCED';
  if (s >= 7)  return 'INTERMEDIATE';
  if (s >= 4)  return 'BEGINNER';
  return 'NEWBIE';
});

const levelDisplay = computed(() => {
  const labels = {
    NEWBIE:       'NEWBIE (입문)',
    BEGINNER:     'BEGINNER (초급)',
    INTERMEDIATE: 'INTERMEDIATE (중급)',
    ADVANCED:     'ADVANCED (고급)',
    HIGHLEVEL:    'HIGHLEVEL (최고급)',
  };
  return labels[determinedLevel.value];
});

const levelDesc = computed(() => {
  const descs = {
    NEWBIE:       '기초 단어를 차근차근 익혀봐요!',
    BEGINNER:     '기본기가 있네요! 꾸준히 연습하면 금방 늘어요.',
    INTERMEDIATE: '중간 수준이에요. 심화 단어에 도전해 보세요!',
    ADVANCED:     '실력이 좋네요! 조금만 더 하면 최고급이에요.',
    HIGHLEVEL:    '훌륭해요! 최고 수준의 실력을 갖추었어요!',
  };
  return descs[determinedLevel.value];
});

const levelClass = computed(() => determinedLevel.value.toLowerCase());

const scoreClass = computed(() => {
  const r = correctCount.value / questions.value.length;
  if (r >= 0.8) return 'great';
  if (r >= 0.5) return 'ok';
  return 'poor';
});

const timerPct = computed(() =>
  totalTimerSeconds.value ? Math.max(0, (remainingSeconds.value / totalTimerSeconds.value) * 100) : 0
);
const formattedRemainingTime = computed(() => {
  const s = Math.max(remainingSeconds.value, 0);
  return `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;
});

const stopTimer = () => {
  if (timerId.value) { clearInterval(timerId.value); timerId.value = null; }
};

const startTimer = () => {
  stopTimer();
  timerId.value = setInterval(() => {
    if (remainingSeconds.value <= 1) {
      remainingSeconds.value = 0;
      stopTimer();
      handleTimeExpired();
      return;
    }
    remainingSeconds.value -= 1;
  }, 1000);
};

const handleTimeExpired = async () => {
  if (phase.value !== 'quiz') return;
  timeExpiredNotice.value = true;
  const answeredIds = new Set(answers.value.map(a => a.wordId));
  questions.value.forEach(q => {
    if (!answeredIds.has(q.wordId)) {
      answers.value.push({ wordId: q.wordId, submittedAnswer: '' });
    }
  });
  await finishTest();
};

onBeforeUnmount(stopTimer);

const startTest = async () => {
  loadError.value = '';
  loading.value = true;
  try {
    const [levelTestRes, meRes] = await Promise.all([
      api.get('/api/quizzes/level-test'),
      api.get('/auth/me').catch(() => ({ data: null })),
    ]);
    if (!levelTestRes.data.length) { loadError.value = '문제를 불러오지 못했습니다.'; return; }
    questions.value = levelTestRes.data;
    userId.value = meRes.data?.id ?? null;
    currentIndex.value = 0;
    typedAnswer.value = '';
    isAnswered.value = false;
    correctCount.value = 0;
    answers.value = [];
    timeExpiredNotice.value = false;
    totalTimerSeconds.value = LEVEL_TEST_SECONDS;
    remainingSeconds.value = LEVEL_TEST_SECONDS;
    phase.value = 'quiz';
    startTimer();
    await nextTick();
    answerInput.value?.focus();
  } catch {
    loadError.value = '문제를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.';
  } finally {
    loading.value = false;
  }
};

const submitAnswer = () => {
  if (isAnswered.value || !typedAnswer.value.trim()) return;
  const submitted = typedAnswer.value.trim();
  isCorrect.value = submitted.toLowerCase() === currentQ.value.correctAnswer.toLowerCase();
  isAnswered.value = true;
  if (isCorrect.value) correctCount.value++;
  answers.value.push({ wordId: currentQ.value.wordId, submittedAnswer: submitted });
};

const nextQuestion = async () => {
  if (currentIndex.value + 1 < questions.value.length) {
    currentIndex.value++;
    typedAnswer.value = '';
    isAnswered.value = false;
    isCorrect.value = false;
    await nextTick();
    answerInput.value?.focus();
  } else {
    await finishTest();
  }
};

const finishTest = async () => {
  stopTimer();
  phase.value = 'result';
  saving.value = true;
  const tasks = [
    api.patch('/api/users/me/level', { level: determinedLevel.value }),
  ];
  if (userId.value) {
    tasks.push(
      api.post('/api/quizzes/submit', {
        userId: userId.value,
        totalQuestionCount: questions.value.length,
        answers: answers.value,
      }).catch(() => {})
    );
  }
  try {
    await Promise.all(tasks);
  } catch {
    saveError.value = '레벨 저장에 실패했습니다. 다시 시도해주세요.';
  } finally {
    saving.value = false;
  }
};

const goToMain = () => {
  router.push('/main');
};
</script>

<style scoped>
.quiz-page {
  min-height: 100vh;
  background: var(--bg);
}

.quiz-content {
  width: 100%;
  max-width: 560px;
  margin: 0 auto;
  padding: 40px 24px;
  animation: fadeUp 0.3s ease;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to   { opacity: 1; transform: translateY(0); }
}

.phase-box { display: flex; flex-direction: column; gap: 24px; }

.quiz-header { margin-bottom: 4px; }
.page-title {
  font-family: 'Syne', sans-serif;
  font-size: 22px;
  font-weight: 800;
  color: var(--text);
  margin: 0 0 6px;
}
.page-sub { font-size: 14px; color: var(--muted); margin: 0; }

.intro-info {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--text);
}
.info-icon { font-size: 18px; }

.error-msg { font-size: 13px; color: #E24B4A; margin: 0; }

.btn-primary {
  width: 100%;
  padding: 13px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s, transform 0.1s;
}
.btn-primary:hover { background: var(--accent-hover); }
.btn-primary:active { transform: scale(0.985); }
.btn-primary:disabled { background: var(--border); color: var(--muted); cursor: not-allowed; transform: none; }

.quiz-status {
  display: grid;
  grid-template-columns: 1fr 140px;
  gap: 12px;
  align-items: stretch;
}

.progress-row { display: flex; flex-direction: column; justify-content: center; gap: 8px; }
.progress-text { font-size: 13px; font-weight: 500; color: var(--muted); }
.progress-bar { height: 4px; background: var(--border); border-radius: 2px; overflow: hidden; }
.progress-fill { height: 100%; background: var(--accent); border-radius: 2px; transition: width 0.3s ease; }

.timer-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.timer-label { color: var(--muted); font-size: 11px; font-weight: 700; }
.timer-card strong {
  color: var(--text);
  font-size: 22px;
  font-weight: 800;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}
.timer-card.urgent strong { color: #A32D2D; }
.timer-track { height: 4px; background: var(--border); border-radius: 2px; overflow: hidden; }
.timer-fill { height: 100%; background: var(--accent); border-radius: 2px; transition: width 0.3s ease; }
.timer-card.urgent .timer-fill { background: #A32D2D; }

.timeout-notice {
  padding: 11px 14px;
  background: #FCEBEB;
  border: 1px solid #F4B7B7;
  border-radius: var(--radius-sm);
  color: #A32D2D;
  font-size: 13px;
  font-weight: 700;
}

.question-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 28px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.q-label { font-size: 12px; color: var(--muted); margin: 0; text-transform: uppercase; letter-spacing: 0.4px; }
.q-word {
  font-family: 'Syne', sans-serif;
  font-size: 32px;
  font-weight: 800;
  color: var(--text);
  margin: 0;
  line-height: 1.2;
}

.input-row { display: flex; gap: 8px; }

.answer-input {
  flex: 1;
  padding: 11px 14px;
  border: 1.5px solid var(--border);
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  color: var(--text);
  background: #fff;
  outline: none;
  transition: border-color 0.15s;
}
.answer-input:focus { border-color: var(--accent); }
.answer-input:disabled { background: #fafafa; }
.answer-input.input-correct { border-color: #3B6D11; background: #EAF3DE; }
.answer-input.input-wrong   { border-color: #A32D2D; background: #FCEBEB; }

.btn-submit {
  padding: 11px 18px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s;
}
.btn-submit:hover { background: var(--accent-hover); }
.btn-submit:disabled { background: var(--border); color: var(--muted); cursor: not-allowed; }

.result-badge {
  padding: 10px 14px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
}
.result-badge.correct { background: #EAF3DE; color: #3B6D11; }
.result-badge.wrong   { background: #FCEBEB; color: #A32D2D; }

/* Result */
.result-box { align-items: center; }

.score-wrap { text-align: center; }
.score-circle {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin: 0 auto 12px;
  border: 4px solid;
}
.score-circle.great { border-color: #3B6D11; background: #EAF3DE; color: #3B6D11; }
.score-circle.ok    { border-color: #854F0B; background: #FAEEDA; color: #854F0B; }
.score-circle.poor  { border-color: #A32D2D; background: #FCEBEB; color: #A32D2D; }
.score-num   { font-family: 'Syne', sans-serif; font-size: 32px; font-weight: 800; line-height: 1; }
.score-total { font-size: 13px; font-weight: 500; }
.score-msg   { font-size: 14px; color: var(--muted); margin: 0; }

.level-result {
  text-align: center;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 24px 32px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}
.level-label {
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--muted);
  margin: 0;
}
.level-badge {
  font-family: 'Syne', sans-serif;
  font-size: 22px;
  font-weight: 800;
  padding: 6px 20px;
  border-radius: 99px;
}
.level-badge.newbie       { background: #F1EFE8; color: #5F5E5A; }
.level-badge.beginner     { background: #E6F9F1; color: #0F6E56; }
.level-badge.intermediate { background: #FFF4E0; color: #BA7517; }
.level-badge.advanced     { background: #FAEEE7; color: #993C1D; }
.level-badge.highlevel    { background: #FCEBEB; color: #A32D2D; }
.level-desc { font-size: 14px; color: var(--muted); margin: 0; }
</style>
