<!-- src/components/NavBar.vue -->
<template>
  <nav class="navbar">
    <!-- [캡슐화] 로고 클릭 시 홈으로 이동 -->
    <div class="navbar-logo" @click="$router.push('/main')">
      vibe<span>voca</span>
    </div>

    <!-- [모듈화] 네비게이션 링크 목록 -->
    <div class="navbar-links">
      <button
        v-for="link in navLinks"
        :key="link.path"
        class="nav-link"
        :class="{ active: $route.path === link.path }"
        @click="$router.push(link.path)"
      >
        {{ link.label }}
      </button>
    </div>

    <!-- [캡슐화] 로그인 상태에 따라 다른 UI 표시 -->
    <div class="navbar-user">
      <template v-if="isLoggedIn">
        <div class="avatar">{{ nicknameInitial }}</div>
        <button class="nav-link muted" @click="handleLogout">로그아웃</button>
      </template>
      <template v-else>
        <button class="nav-btn-outline" @click="$router.push('/login')">로그인</button>
        <button class="nav-btn-primary" @click="$router.push('/register')">회원가입</button>
      </template>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

const router = useRouter()

// [캡슐화] 사용자 상태를 컴포넌트 내부에서 관리
const nickname = ref('')
const isLoggedIn = ref(false)

// 네비게이션 링크 목록
const navLinks = [
  { path: '/main', label: '홈' },
  { path: '/words', label: '단어 목록' },
  { path: '/quiz', label: '퀴즈' },
  { path: '/error-note', label: '오답노트' },
]

// [캡슐화] 닉네임 첫 글자만 아바타에 표시
const nicknameInitial = computed(() => {
  return nickname.value ? nickname.value.charAt(0) : '?'
})

// [캡슐화] 로그인 상태 확인 — 세션 기반이므로 /auth/me 호출
const checkLoginStatus = async () => {
  try {
    const res = await api.get('/auth/me')
    nickname.value = res.data.nickname
    isLoggedIn.value = true
  } catch {
    isLoggedIn.value = false
  }
}

// [캡슐화] 로그아웃 처리
const handleLogout = async () => {
  try {
    await api.post('/api/users/logout')
  } catch {
    // 로그아웃 실패해도 홈으로 이동
  }
  isLoggedIn.value = false
  nickname.value = ''
  router.push('/')
}

onMounted(() => {
  checkLoginStatus()
})
</script>

<style scoped>
/* [모듈화] NavBar 전용 스타일 */
.navbar {
  background: rgba(247, 246, 242, 0.95);
  border-bottom: 1px solid var(--border);
  padding: 0 2rem;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(8px);
}

.navbar-logo {
  font-family: 'Syne', sans-serif;
  font-size: 22px;
  font-weight: 800;
  letter-spacing: -1.5px;
  color: var(--text);
  cursor: pointer;
  user-select: none;
}
.navbar-logo span { color: var(--accent); }

.navbar-links {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.nav-link {
  font-size: 14px;
  font-weight: 500;
  color: var(--muted);
  padding: 6px 14px;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  background: none;
  font-family: 'DM Sans', sans-serif;
  transition: color 0.15s, background 0.15s;
}
.nav-link:hover { color: var(--text); background: var(--border); }
.nav-link.active { color: var(--accent); background: #EEE9FF; }
.nav-link.muted { font-size: 13px; }

.navbar-user {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* [캡슐화] 아바타 스타일 */
.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--accent);
  color: #fff;
  font-size: 13px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-btn-outline {
  font-size: 13px;
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 8px;
  cursor: pointer;
  border: 1.5px solid var(--border);
  background: none;
  color: var(--text);
  font-family: 'DM Sans', sans-serif;
  transition: border-color 0.15s;
}
.nav-btn-outline:hover { border-color: var(--accent); color: var(--accent); }

.nav-btn-primary {
  font-size: 13px;
  font-weight: 500;
  padding: 6px 16px;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  background: var(--accent);
  color: #fff;
  font-family: 'DM Sans', sans-serif;
  transition: background 0.15s;
}
.nav-btn-primary:hover { background: var(--accent-hover); }
</style>
