<!-- 파일 경로: src/views/QuizView.vue -->
<template>
  <div class="quiz-page">
    <NavBar />
    <div class="quiz-content">
      <div v-if="phase === 'setup'" class="phase-box">
        <header class="quiz-header">
          <h1 class="page-title">{{ isErrorNoteMode ? '오답노트 재테스트' : '단어 퀴즈' }}</h1>
          <p class="page-sub">
            {{ isErrorNoteMode ? '오답노트에 저장된 모든 단어를 복습합니다.' : '한국어 뜻을 보고 영어 단어를 입력하세요' }}
          </p>
        </header>

        <div v-if="!isErrorNoteMode" class="option-group">
          <label class="option-label">문제 수</label>
          <div class="filter-row">
            <button
              v-for="n in [5, 10, 20]"
              :key="n"
              class="filter-btn"
              :class="{ active: setupCount === n }"
              type="button"
              @click="setupCount = n"
            >
              {{ n }}문제
            </button>
          </div>
        </div>

        <div class="timer-preview">
          <span>예상 제한 시간</span>
          <strong>{{ formatSeconds(calculatePreviewTimerSeconds(setupCount)) }}</strong>
        </div>

        <p v-if="setupError" class="error-msg">{{ setupError }}</p>

        <button class="btn-primary" type="button" :disabled="setupLoading" @click="startQuiz">
          {{ setupLoading ? '불러오는 중...' : (isErrorNoteMode ? '복습 시작하기' : '퀴즈 시작하기') }}
        </button>
      </div>

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
              :disabled="isAnswered || isFinishing"
              @keydown.enter="submitAnswer"
            />
            <button
              v-if="!isAnswered"
              class="btn-submit"
              type="button"
              :disabled="!typedAnswer.trim() || isFinishing"
              @click="submitAnswer"
            >
              확인
            </button>
          </div>

          <div v-if="isAnswered" class="result-badge" :class="isCorrect ? 'correct' : 'wrong'">
            <template v-if="isCorrect">정답입니다.</template>
            <template v-else>오답입니다. 정답: <strong>{{ currentQ.correctAnswer }}</strong></template>
          </div>

          <button v-if="isAnswered" class="btn-primary" type="button" :disabled="isFinishing" @click="nextQuestion">
            {{ currentIndex + 1 < questions.length ? '다음 문제' : '결과 보기' }}
          </button>
        </div>
      </div>

      <div v-else-if="phase === 'result'" class="phase-box result-box">
        <div v-if="timeExpiredNotice" class="timeout-notice result-timeout">
          시간이 종료되어 미입력 문제를 오답으로 처리했습니다.
        </div>

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

        <div v-else class="perfect-msg">모든 단어를 맞혔어요.</div>

        <button class="btn-primary" type="button" @click="resetQuiz">다시 시작</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import api from '../api/axios'

const route = useRoute()
const isErrorNoteMode = computed(() => route.query.mode === 'errorNote')

const userId = ref(null)

onMounted(async () => {
  try {
    const res = await api.get('/auth/me')
    userId.value = res.data.id
  } catch {
    // 로그인 안 된 상태
  }
})

const phase = ref('setup')
const timerQuestionBlockSize = 5
const timerSecondsPerBlock = 45

const setupCount = ref(10)
const setupLoading = ref(false)
const setupError = ref('')

const questions = ref([])
const currentIndex = ref(0)
const typedAnswer = ref('')
const isAnswered = ref(false)
const isCorrect = ref(false)
const answers = ref([])
const isFinishing = ref(false)

const correctCount = ref(0)
const wrongAnswers = ref([])

const totalTimerSeconds = ref(0)
const remainingSeconds = ref(0)
const timerId = ref(null)
const timeExpiredNotice = ref(false)

const answerInput = ref(null)

const currentQ = computed(() => questions.value[currentIndex.value] ?? {})
const progressPct = computed(() => {
  if (!questions.value.length) return 0
  return ((currentIndex.value + 1) / questions.value.length) * 100
})

const timerPct = computed(() => {
  if (!totalTimerSeconds.value) return 0
  return Math.max(0, (remainingSeconds.value / totalTimerSeconds.value) * 100)
})

const formattedRemainingTime = computed(() => formatSeconds(remainingSeconds.value))

const scoreClass = computed(() => {
  if (!questions.value.length) return 'poor'
  const r = correctCount.value / questions.value.length
  if (r >= 0.8) return 'great'
  if (r >= 0.5) return 'ok'
  return 'poor'
})

const scoreMessage = computed(() => {
  if (!questions.value.length) return ''
  const r = correctCount.value / questions.value.length
  if (r === 1) return '완벽해요! 모든 단어를 알고 있네요.'
  if (r >= 0.8) return '훌륭해요! 조금만 더 연습하면 완벽해요.'
  if (r >= 0.5) return '절반 이상 맞혔어요. 꾸준히 연습해봐요.'
  return '틀린 단어를 다시 복습해봐요.'
})

const formatSeconds = (seconds) => {
  const safeSeconds = Math.max(Number(seconds) || 0, 0)
  const minutes = Math.floor(safeSeconds / 60)
  const restSeconds = safeSeconds % 60
  return `${minutes}:${String(restSeconds).padStart(2, '0')}`
}

// [모듈화] 백엔드 일반 퀴즈 타이머 정책과 같은 기준으로 시작 전 예상 시간을 표시합니다.
const calculatePreviewTimerSeconds = (questionCount) => {
  const blockCount = Math.ceil(Math.max(questionCount, 1) / timerQuestionBlockSize)
  return blockCount * timerSecondsPerBlock
}

// [캡슐화] 일반 퀴즈는 화면 정책을 우선하고, 수준 테스트처럼 고정 타이머일 때만 서버 값을 사용합니다.
const resolveTimerSeconds = (data, questionCount) => {
  if (data?.fixedTimer) {
    return Number(data.timerSeconds) || calculatePreviewTimerSeconds(questionCount)
  }

  return calculatePreviewTimerSeconds(questionCount)
}

const stopTimer = () => {
  if (timerId.value) {
    clearInterval(timerId.value)
    timerId.value = null
  }
}

const startTimer = () => {
  stopTimer()
  timerId.value = setInterval(() => {
    if (remainingSeconds.value <= 1) {
      remainingSeconds.value = 0
      stopTimer()
      handleTimeExpired()
      return
    }
    remainingSeconds.value -= 1
  }, 1000)
}

const resetQuizState = () => {
  currentIndex.value = 0
  typedAnswer.value = ''
  isAnswered.value = false
  isCorrect.value = false
  answers.value = []
  correctCount.value = 0
  wrongAnswers.value = []
  isFinishing.value = false
  timeExpiredNotice.value = false
}

const startQuiz = async () => {
  setupError.value = ''
  setupLoading.value = true
  try {
    if (isErrorNoteMode.value) {
      const { data } = await api.get('/api/error-notes/quiz', {
        params: { userId: userId.value }
      })
      const list = Array.isArray(data) ? data : []
      if (list.length === 0) {
        setupError.value = '오답노트에 저장된 단어가 없습니다.'
        return
      }
      questions.value = list
      resetQuizState()
      totalTimerSeconds.value = calculatePreviewTimerSeconds(list.length)
      remainingSeconds.value = totalTimerSeconds.value
    } else {
      const { data } = await api.get('/api/quizzes/generate', {
        params: {
          count: setupCount.value,
          quizType: route.query.quizType || 'quiz',
        },
      })
      const list = Array.isArray(data) ? data : data.questions ?? []
      if (list.length === 0) {
        setupError.value = '퀴즈 문제가 없습니다.'
        return
      }
      questions.value = list
      resetQuizState()
      totalTimerSeconds.value = resolveTimerSeconds(data, list.length)
      remainingSeconds.value = totalTimerSeconds.value
    }
    phase.value = 'quiz'
    startTimer()
    await nextTick()
    answerInput.value?.focus()
  } catch {
    setupError.value = '퀴즈를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.'
  } finally {
    setupLoading.value = false
  }
}

const submitAnswer = () => {
  if (isAnswered.value || !typedAnswer.value.trim() || isFinishing.value) return

  const submitted = typedAnswer.value.trim()
  const correct = currentQ.value.correctAnswer || ''
  isCorrect.value = submitted.toLowerCase() === correct.toLowerCase()
  isAnswered.value = true

  if (isCorrect.value) {
    correctCount.value++
  } else {
    wrongAnswers.value.push({
      wordId: currentQ.value.wordId,
      questionMeaning: currentQ.value.questionMeaning,
      correctAnswer: correct,
<<<<<<< HEAD
      submittedWord: submitted,
    })
  }

  answers.value.push({ wordId: currentQ.value.wordId, submittedAnswer: submitted })
}

const nextQuestion = async () => {
  if (isFinishing.value) return

  if (currentIndex.value + 1 < questions.value.length) {
    currentIndex.value++
    typedAnswer.value = ''
    isAnswered.value = false
    isCorrect.value = false
    await nextTick()
    answerInput.value?.focus()
  } else {
    await finishQuiz(false)
  }
}

const handleTimeExpired = async () => {
  if (isFinishing.value || phase.value !== 'quiz') return

  timeExpiredNotice.value = true
  const answeredIds = new Set(answers.value.map((answer) => answer.wordId))

  questions.value.forEach((question) => {
    if (!answeredIds.has(question.wordId)) {
      answers.value.push({ wordId: question.wordId, submittedAnswer: '' })
      wrongAnswers.value.push({
        wordId: question.wordId,
        questionMeaning: question.questionMeaning,
        correctAnswer: question.correctAnswer,
        submittedWord: '',
      })
    }
  })

  await finishQuiz(true)
}

const finishQuiz = async (timeExpired = false) => {
  if (isFinishing.value) return

  isFinishing.value = true
  stopTimer()
  try {
    await api.post('/api/quizzes/submit', {
      userId: userId.value,
      totalQuestionCount: questions.value.length,
      timeExpired,
      answers: answers.value,
    })
  } catch {
    // 서버 저장에 실패해도 사용자가 최종 결과는 확인할 수 있게 둡니다.
  } finally {
    phase.value = 'result'
    isFinishing.value = false
  }
}

const resetQuiz = () => {
  stopTimer()
  phase.value = 'setup'
  questions.value = []
  totalTimerSeconds.value = 0
  remainingSeconds.value = 0
  resetQuizState()
}

onBeforeUnmount(stopTimer)
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
  to { opacity: 1; transform: translateY(0); }
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

.timer-preview {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--muted);
  font-size: 13px;
}
.timer-preview strong {
  color: var(--accent);
  font-size: 18px;
  font-weight: 800;
}

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

.progress-row {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 8px;
}
.progress-text { font-size: 13px; font-weight: 500; color: var(--muted); }
.progress-bar { height: 4px; background: var(--border); border-radius: 2px; overflow: hidden; }
.progress-fill { height: 100%; background: var(--accent); border-radius: 2px; transition: width 0.3s ease; }

.timer-card {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.timer-label {
  color: var(--muted);
  font-size: 11px;
  font-weight: 700;
}
.timer-card strong {
  color: var(--text);
  font-size: 22px;
  font-weight: 800;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}
.timer-card.urgent strong { color: #A32D2D; }
.timer-track {
  height: 4px;
  background: var(--border);
  border-radius: 2px;
  overflow: hidden;
}
.timer-fill {
  height: 100%;
  background: var(--accent);
  border-radius: 2px;
  transition: width 0.3s ease;
}
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
.result-timeout { width: 100%; text-align: center; }

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
  min-width: 0;
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
.answer-input.input-wrong { border-color: #A32D2D; background: #FCEBEB; }

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
.result-badge.wrong { background: #FCEBEB; color: #A32D2D; }

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
.score-circle.ok { border-color: #854F0B; background: #FAEEDA; color: #854F0B; }
.score-circle.poor { border-color: #A32D2D; background: #FCEBEB; color: #A32D2D; }
.score-num { font-family: 'Syne', sans-serif; font-size: 32px; font-weight: 800; line-height: 1; }
.score-total { font-size: 13px; font-weight: 500; }
.score-msg { font-size: 14px; color: var(--muted); margin: 0; }

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
.w-en { font-size: 15px; font-weight: 500; color: var(--text); }
.w-meaning { font-size: 12px; color: #3B6D11; }
.w-my { font-size: 12px; color: #A32D2D; flex-shrink: 0; }

.perfect-msg { font-size: 16px; color: var(--text); text-align: center; }

@media (max-width: 560px) {
  .quiz-status {
    grid-template-columns: 1fr;
  }

  .input-row {
    flex-direction: column;
  }

  .wrong-item {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
