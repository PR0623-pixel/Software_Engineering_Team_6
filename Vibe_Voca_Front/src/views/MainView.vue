<!-- src/views/MainView.vue -->
<template>
  <div class="main-page">
    <!-- [모듈화] 네비게이션 바를 별도 컴포넌트로 분리 -->
    <NavBar />

    <div class="main-content">

      <!-- 히어로 섹션 -->
      <section class="hero">
        <div class="hero-badge">TOEIC 단어 학습</div>
        <h1 class="hero-title">매일 꾸준히<br><span>단어</span>를 익혀요</h1>
        <p class="hero-sub">퀴즈로 실력을 테스트하고 오답을 반복 학습하세요</p>
        <div class="hero-btns">
          <button class="btn btn-purple" @click="$router.push('/quiz')">퀴즈 시작하기</button>
          <button class="btn btn-dark" @click="$router.push('/words')">단어 보기</button>
        </div>
      </section>

      <!-- [캡슐화] 통계 카드 — 나중에 API로 실제 데이터 연결 가능 -->
      <section class="stats-section">
        <div class="stat-card" v-for="stat in stats" :key="stat.label">
          <div class="stat-num">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </div>
      </section>

      <!-- 메뉴 그리드 -->
      <section>
        <h2 class="section-title">메뉴</h2>
        <div class="menu-grid">
          <div
            class="menu-card"
            v-for="menu in menuItems"
            :key="menu.label"
            @click="$router.push(menu.path)"
          >
            <div class="menu-icon">{{ menu.icon }}</div>
            <div class="menu-info">
              <h3>{{ menu.label }}</h3>
              <p>{{ menu.desc }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- [정보은닉] 단어 미리보기 — DTO(WordResponseDto)로 받은 데이터만 표시 -->
      <section>
        <h2 class="section-title">오늘의 단어 미리보기</h2>
        <div class="word-list">
          <div class="word-list-header">
            <span class="word-list-title">추천 단어</span>
            <span class="word-list-count">{{ words.length }}개</span>
          </div>

          <!-- 로딩 상태 -->
          <div v-if="isLoading" class="word-loading">불러오는 중...</div>

          <!-- 단어 목록 -->
          <div
            v-else
            v-for="word in words"
            :key="word.id"
            class="word-item"
          >
            <div>
              <div class="word-en">{{ word.englishWord }}</div>
              <div class="word-ko">{{ word.koreanMeaning }}</div>
            </div>
            <span class="word-badge" :class="getLevelClass(word.level)">
              {{ getLevelLabel(word.level) }}
            </span>
          </div>

          <!-- 데이터 없을 때 -->
          <div v-if="!isLoading && words.length === 0" class="word-empty">
            단어 데이터가 없습니다.
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import NavBar from '../components/NavBar.vue'
import api from '../api/axios'

// [캡슐화] 통계 데이터 — 추후 API 연결
const stats = ref([
  { value: 50, label: '전체 단어 수' },
  { value: 0,  label: '오늘 학습한 단어' },
  { value: 0,  label: '퀴즈 응시 횟수' },
])

// [모듈화] 메뉴 항목 데이터 분리
const menuItems = [
  { icon: '📚', label: '단어 목록', path: '/words',      desc: 'TOEIC 필수 단어를 레벨별로 확인하세요' },
  { icon: '✏️', label: '단어 퀴즈', path: '/quiz',       desc: '4지선다 퀴즈로 실력을 테스트하세요' },
  { icon: '📝', label: '오답노트',  path: '/error-note', desc: '틀린 단어를 모아 반복 학습하세요' },
  { icon: '👤', label: '마이페이지',path: '/me',         desc: '프로필과 학습 현황을 확인하세요' },
]

// [캡슐화] 단어 목록 상태 관리
const words = ref([])
const isLoading = ref(true)

// [정보은닉] API 호출로 단어 데이터 가져오기 — 내부 DB 구조 노출 없이 DTO로만 수신
const fetchWords = async () => {
  try {
    const res = await api.get('/api/words')
    // 미리보기용으로 5개만 표시
    words.value = res.data.slice(0, 5)
  } catch (e) {
    console.error('단어 불러오기 실패:', e)
  } finally {
    isLoading.value = false
  }
}

// [캡슐화] 레벨에 따른 CSS 클래스 반환
const getLevelClass = (level) => {
  const map = {
    NEWBIE: 'newbie',
    BEGINNER: 'beginner',
    INTERMEDIATE: 'intermediate',
    ADVANCED: 'advanced',
    HIGHLEVEL: 'highlevel',
  }
  return map[level] || ''
}

// [모듈화] 백엔드 난이도 enum과 화면의 1~5 레벨 표기를 이곳에서만 연결합니다.
const getLevelLabel = (level) => {
  const map = {
    NEWBIE: 'Level 1',
    BEGINNER: 'Level 2',
    INTERMEDIATE: 'Level 3',
    ADVANCED: 'Level 4',
    HIGHLEVEL: 'Level 5',
  }
  return map[level] || level
}

onMounted(() => {
  fetchWords()
})
</script>

<style scoped>
/* [모듈화] MainView 전용 스타일 */
.main-page {
  min-height: 100vh;
  background: var(--bg);
}

.main-content {
  max-width: 860px;
  margin: 0 auto;
  padding: 2.5rem 1.5rem 4rem;
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}

/* ── 히어로 ── */
.hero {
  text-align: center;
  padding: 2rem 0;
}
.hero-badge {
  display: inline-block;
  background: #EEE9FF;
  color: var(--accent);
  font-size: 12px;
  font-weight: 500;
  padding: 4px 14px;
  border-radius: 99px;
  margin-bottom: 1rem;
  letter-spacing: 0.04em;
}
.hero-title {
  font-family: 'Syne', sans-serif;
  font-size: 42px;
  font-weight: 800;
  letter-spacing: -2px;
  line-height: 1.1;
  margin: 0 0 0.75rem;
  color: var(--text);
}
.hero-title span { color: var(--accent); }
.hero-sub {
  font-size: 16px;
  color: var(--muted);
  margin: 0 0 2rem;
}
.hero-btns {
  display: flex;
  gap: 10px;
  justify-content: center;
}

/* ── 버튼 ── */
.btn {
  padding: 13px 28px;
  border-radius: 10px;
  font-family: 'DM Sans', sans-serif;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background 0.15s, transform 0.1s;
}
.btn:active { transform: scale(0.985); }
.btn-purple { background: var(--accent); color: #fff; }
.btn-purple:hover { background: var(--accent-hover); }
.btn-dark { background: var(--dark); color: #fff; }
.btn-dark:hover { background: #2e2e4a; }

/* ── 통계 카드 ── */
.stats-section {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.stat-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 14px;
  padding: 1.2rem 1.4rem;
  text-align: center;
}
.stat-num {
  /* [버그 픽스 핵심] 넓적하게 디자인된 Syne 폰트를 버리고, 비율이 바른 DM Sans 폰트로 교체합니다. */
  font-family: 'DM Sans', 'Pretendard', sans-serif !important;
  
  /* DM Sans 폰트에 맞게 크기와 굵기를 살짝 조정하여 밸런스를 맞춥니다. */
  font-size: 32px !important;
  font-weight: 700 !important; 
  color: var(--accent) !important;
  
  /* 숫자 폭을 일정하게 유지 */
  font-variant-numeric: tabular-nums !important;
  line-height: 1.2 !important;
}
.stat-label {
  font-size: 13px;
  color: var(--muted);
  margin-top: 4px;
}

/* ── 섹션 타이틀 ── */
.section-title {
  font-family: 'Syne', sans-serif;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: -0.5px;
  margin: 0 0 1rem;
  color: var(--text);
}

/* ── 메뉴 그리드 ── */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}
.menu-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 14px;
  padding: 1.4rem;
  cursor: pointer;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  transition: border-color 0.15s, transform 0.1s;
}
.menu-card:hover {
  border-color: var(--accent);
  transform: translateY(-2px);
}
.menu-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  background: #EEE9FF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}
.menu-info h3 {
  font-size: 15px;
  font-weight: 500;
  margin: 0 0 4px;
  color: var(--text);
}
.menu-info p {
  font-size: 13px;
  color: var(--muted);
  margin: 0;
  line-height: 1.5;
}

/* ── 단어 목록 ── */
.word-list {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: 14px;
  overflow: hidden;
}
.word-list-header {
  padding: 1rem 1.4rem;
  border-bottom: 1px solid var(--border);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.word-list-title { font-size: 14px; font-weight: 500; }
.word-list-count { font-size: 13px; color: var(--muted); }

.word-item {
  padding: 0.85rem 1.4rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border);
  transition: background 0.1s;
  cursor: default;
}
.word-item:last-child { border-bottom: none; }
.word-item:hover { background: #F7F6F2; }

.word-en { font-size: 15px; font-weight: 500; color: var(--text); }
.word-ko { font-size: 13px; color: var(--muted); margin-top: 2px; }

/* [캡슐화] 레벨 배지 — 레벨별 색상 */
.word-badge {
  font-size: 11px;
  font-weight: 500;
  padding: 3px 10px;
  border-radius: 99px;
  background: #EEE9FF;
  color: var(--accent);
}
.word-badge.newbie      { background: #F1EFE8; color: #5F5E5A; }
.word-badge.beginner    { background: #E6F9F1; color: #0F6E56; }
.word-badge.intermediate{ background: #FFF4E0; color: #BA7517; }
.word-badge.advanced    { background: #FAEEE7; color: #993C1D; }
.word-badge.highlevel   { background: #FCEBEB; color: #A32D2D; }

.word-loading,
.word-empty {
  padding: 2rem;
  text-align: center;
  font-size: 14px;
  color: var(--muted);
}
</style>
