<template>
  <div class="quiz-page">
    <NavBar />
    <div class="quiz-content">

      <!-- 1. Setup -->
      <div v-if="phase === 'setup'" class="phase-box">
        <header class="quiz-header">
          <h1 class="page-title">단어 퀴즈</h1>
          <p class="page-sub">한국어 뜻을 보고 영어 단어를 입력하세요</p>
        </header>

        <div class="option-group">
          <label class="option-label">문제 수</label>
          <div class="filter-row">
            <button
              v-for="n in [5, 10, 20]"
              :key="n"
              class="filter-btn"
              :class="{ active: setupCount === n }"
              @click="setupCount = n"
            >{{ n }}문제</button>
          </div>
        </div>

        <p v-if="setupError" class="error-msg">{{ setupError }}</p>

        <button class="btn-primary" :disabled="setupLoading" @click="startQuiz">
          {{ setupLoading ? '불러오는 중...' : '퀴즈 시작하기' }}
        </button>
      </div>

      <!-- 2. Quiz -->
      <div v-else-if="phase === 'quiz'" class="phase-box">
        <div class="progress-row">
          <span class="progress-text">{{ currentIndex + 1 }} / {{ questions.length }}</span>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: progressPct + '%' }"></div>
          </div>
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
          <p class="score-msg">{{ scoreMessage }}</p>
        </div>

        <div v-if="wrongAnswers.length" class="wrong-section">
          <h3 class="wrong-title">틀린 단어 ({{ wrongAnswers.length }}개)</h3>
          <ul class="wrong-list">
            <li v-for="w in wrongAnswers" :key="w.wordId" class="wrong-item">
              <div class="wrong-word-info">
                <span class="w-en">{{ w.correctAnswer }}</span>
                <span class="w-meaning">{{ w.questionMeaning }}</span>
              </div>
              <span class="w-my">내 답: {{ w.submittedWord || '(미입력)' }}</span>
            </li>
          </ul>
        </div>

        <div v-else class="perfect-msg">🎉 모든 단어를 맞혔어요!</div>

        <button class="btn-primary" @click="resetQuiz">다시 시작</button>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue';
import NavBar from '../components/NavBar.vue';
import api from '../api/axios';

const phase = ref('setup');

const setupCount = ref(10);
const setupLoading = ref(false);
const setupError = ref('');

const questions = ref([]);
const currentIndex = ref(0);
const typedAnswer = ref('');
const isAnswered = ref(false);
const isCorrect = ref(false);
const answers = ref([]);

const correctCount = ref(0);
const wrongAnswers = ref([]);

const answerInput = ref(null);

const currentQ = computed(() => questions.value[currentIndex.value] ?? {});
const progressPct = computed(() => ((currentIndex.value + 1) / questions.value.length) * 100);

const scoreClass = computed(() => {
  const r = correctCount.value / questions.value.length;
  if (r >= 0.8) return 'great';
  if (r >= 0.5) return 'ok';
  return 'poor';
});

const scoreMessage = computed(() => {
  const r = correctCount.value / questions.value.length;
  if (r === 1) return '완벽해요! 모든 단어를 알고 있네요 🎉';
  if (r >= 0.8) return '훌륭해요! 조금만 더 연습하면 완벽해요';
  if (r >= 0.5) return '절반 이상 맞혔어요. 꾸준히 연습해봐요';
  return '틀린 단어를 다시 복습해봐요';
});

const startQuiz = async () => {
  setupError.value = '';
  setupLoading.value = true;
  try {
    const { data } = await api.get('/api/quizzes/generate', { params: { count: setupCount.value } });
    const list = Array.isArray(data) ? data : data.questions ?? [];
    if (list.length === 0) { setupError.value = '퀴즈 문제가 없습니다.'; return; }
    questions.value = list;
    currentIndex.value = 0;
    typedAnswer.value = '';
    isAnswered.value = false;
    answers.value = [];
    correctCount.value = 0;
    wrongAnswers.value = [];
    phase.value = 'quiz';
    await nextTick();
    answerInput.value?.focus();
  } catch {
    setupError.value = '퀴즈를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.';
  } finally {
    setupLoading.value = false;
  }
};

const submitAnswer = () => {
  if (isAnswered.value || !typedAnswer.value.trim()) return;

  const submitted = typedAnswer.value.trim();
  const correct = currentQ.value.correctAnswer;
  isCorrect.value = submitted.toLowerCase() === correct.toLowerCase();
  isAnswered.value = true;

  if (isCorrect.value) {
    correctCount.value++;
  } else {
    wrongAnswers.value.push({
      wordId: currentQ.value.wordId,
      questionMeaning: currentQ.value.questionMeaning,
      correctAnswer: correct,
      submittedWord: submitted,
    });
  }

  answers.value.push({ wordId: currentQ.value.wordId, submittedWord: submitted });
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
    await finishQuiz();
  }
};

const finishQuiz = async () => {
  try {
    await api.post('/api/quizzes/submit', { userId: null, answers: answers.value });
  } catch {
    // 결과 화면은 보여줌
  }
  phase.value = 'result';
};

const resetQuiz = () => {
  phase.value = 'setup';
  questions.value = [];
  currentIndex.value = 0;
  typedAnswer.value = '';
  isAnswered.value = false;
  answers.value = [];
  correctCount.value = 0;
  wrongAnswers.value = [];
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

.option-group { display: flex; flex-direction: column; gap: 10px; }
.option-label {
  font-size: 11px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--muted);
}
.filter-row { display: flex; gap: 6px; flex-wrap: wrap; }
.filter-btn {
  padding: 5px 14px;
  border: 1px solid var(--border);
  border-radius: 20px;
  background: #fff;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.15s;
}
.filter-btn.active { background: var(--accent); border-color: var(--accent); color: #fff; }

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

.progress-row { display: flex; flex-direction: column; gap: 8px; }
.progress-text { font-size: 13px; font-weight: 500; color: var(--muted); }
.progress-bar { height: 4px; background: var(--border); border-radius: 2px; overflow: hidden; }
.progress-fill { height: 100%; background: var(--accent); border-radius: 2px; transition: width 0.3s ease; }

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

.input-row {
  display: flex;
  gap: 8px;
}

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

.wrong-section { width: 100%; }
.wrong-title { font-size: 14px; font-weight: 600; color: var(--text); margin: 0 0 10px; }
.wrong-list { list-style: none; display: flex; flex-direction: column; gap: 8px; }
.wrong-item {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 12px 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}
.wrong-word-info { display: flex; flex-direction: column; gap: 2px; }
.w-en      { font-size: 15px; font-weight: 500; color: var(--text); }
.w-meaning { font-size: 12px; color: #3B6D11; }
.w-my      { font-size: 12px; color: #A32D2D; flex-shrink: 0; }

.perfect-msg { font-size: 16px; color: var(--text); text-align: center; }
</style>
