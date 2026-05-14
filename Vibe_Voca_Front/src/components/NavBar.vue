<template>
  <nav class="navbar-container">
    <div class="navbar-content">
      
      <router-link to="/main" class="brand-logo">
        Vibe<span class="highlight">Voca</span>
      </router-link>

      <div class="nav-links">
        <router-link
          v-for="link in navLinks"
          :key="link.path"
          :to="link.path"
          class="nav-item"
          active-class="active"
        >
          {{ link.label }}
        </router-link>
      </div>

      <div class="user-actions">
        <template v-if="isLoggedIn">
          <div class="user-avatar">
            <span class="avatar-text">{{ nicknameInitial }}</span>
          </div>
          <button class="btn-text" @click="handleLogout">로그아웃</button>
        </template>
        <template v-else>
          <button class="btn-outline" @click="$router.push('/login')">로그인</button>
          <button class="btn-primary" @click="$router.push('/register')">회원가입</button>
        </template>
      </div>

    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

const router = useRouter()

// [정보은닉] 사용자 상태 보호
const nickname = ref('')
const isLoggedIn = ref(false)

const navLinks = [
  { path: '/main', label: '홈' },
  { path: '/words', label: '단어 목록' },
  { path: '/quiz', label: '퀴즈' },
  { path: '/error-note', label: '오답노트' },
]

const nicknameInitial = computed(() => {
  return nickname.value ? nickname.value.charAt(0).toUpperCase() : 'U'
})

const checkLoginStatus = async () => {
  try {
    const res = await api.get('/auth/me')
    nickname.value = res.data.nickname
    isLoggedIn.value = true
  } catch {
    isLoggedIn.value = false
  }
}

const handleLogout = async () => {
  try {
    await api.post('/api/users/logout')
  } catch {
    console.error('Logout Error')
  } finally {
    isLoggedIn.value = false
    nickname.value = ''
    router.push('/')
  }
}

onMounted(() => {
  checkLoginStatus()
})
</script>

<style scoped>
/* 전역 테마 색상 (보라색 포인트) */
:root {
  --primary-color: #6B4EFF;
  --primary-hover: #5538e6;
  --bg-color: #ffffff;
  --text-main: #111827;
  --text-muted: #6b7280;
  --border-color: #f3f4f6;
}

/* 네비게이션 래퍼 */
.navbar-container {
  background-color: var(--bg-color, #ffffff);
  border-bottom: 1px solid var(--border-color, #f3f4f6);
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.02);
}

.navbar-content {
  max-width: 1200px;
  margin: 0 auto;
  height: 70px;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* ====================================================
   [버그 픽스 1] 로고 찌그러짐 및 납작해짐 완벽 방어 
==================================================== */
.brand-logo {
  font-family: 'Pretendard', 'Inter', sans-serif;
  font-size: 24px;
  font-weight: 800;
  color: var(--text-main, #111827);
  text-decoration: none;
  
  /* 자간을 넓게 주어 폰트가 깨져도 납작해 보이지 않게 설계 */
  letter-spacing: -0.5px; 
  
  /* 레이아웃 수축 절대 방지 */
  flex-shrink: 0 !important; 
  white-space: nowrap;
  display: inline-block;
}

.brand-logo .highlight {
  color: var(--primary-color, #6B4EFF);
}

/* 링크 스타일 */
.nav-links {
  display: flex;
  gap: 8px;
}

.nav-item {
  text-decoration: none;
  color: var(--text-muted, #6b7280);
  font-weight: 600;
  font-size: 15px;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.nav-item:hover {
  color: var(--text-main, #111827);
  background-color: #f9fafb;
}

.nav-item.active {
  color: var(--primary-color, #6B4EFF);
  background-color: #f0f0ff;
}

/* 유저 액션 및 버튼 영역 */
.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* ====================================================
   [버그 픽스 2] 아바타 및 숫자 뱃지 타원형 변형 원천 차단
==================================================== */
.user-avatar {
  background-color: var(--primary-color, #6B4EFF);
  color: #ffffff;
  border-radius: 50%;
  
  /* 크기를 고정하고 최소 크기를 보장 (찌그러짐 방지) */
  width: 36px;
  height: 36px;
  min-width: 36px !important;
  min-height: 36px !important;
  
  /* 가로세로 1:1 비율을 강제하여 완벽한 원형 유지 */
  aspect-ratio: 1 / 1 !important;
  
  /* 어떠한 경우에도 외부 레이아웃에 의해 줄어들지 않음 */
  flex-shrink: 0 !important; 
  
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(107, 78, 255, 0.2);
}

.avatar-text {
  font-weight: 700;
  font-size: 14px;
  line-height: 1;
  /* 글자가 눌리지 않도록 여백 제거 및 폭 교정 */
  margin: 0;
  padding: 0;
  font-variant-numeric: tabular-nums;
}

/* 공통 버튼 스타일 */
button {
  font-family: inherit;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  white-space: nowrap;
  flex-shrink: 0; /* 버튼 압착 방지 */
}

.btn-text {
  background: none;
  border: none;
  color: var(--text-muted, #6b7280);
  padding: 8px 12px;
}
.btn-text:hover { color: var(--text-main, #111827); }

.btn-outline {
  background: #ffffff;
  border: 1px solid #d1d5db;
  color: var(--text-main, #111827);
  padding: 8px 18px;
}
.btn-outline:hover { background: #f9fafb; border-color: #9ca3af; }

.btn-primary {
  background: var(--primary-color, #6B4EFF);
  border: none;
  color: #ffffff;
  padding: 8px 20px;
  box-shadow: 0 2px 4px rgba(107, 78, 255, 0.1);
}
.btn-primary:hover { background: var(--primary-hover, #5538e6); }
</style>