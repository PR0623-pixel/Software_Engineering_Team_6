<!-- 파일 경로: src/components/NavBar.vue -->
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

      <div class="user-actions" ref="profileMenuRef">
        <template v-if="isLoggedIn">
          <button
            class="user-avatar"
            type="button"
            :aria-expanded="isProfileOpen"
            aria-label="프로필 메뉴 열기"
            @click="toggleProfileMenu"
          >
            <img v-if="profileImg" :src="profileImg" alt="" class="avatar-img" />
            <span v-else class="avatar-text">{{ nicknameInitial }}</span>
          </button>

          <div v-if="isProfileOpen" class="profile-popover">
            <div class="profile-summary">
              <div class="profile-avatar">
                <img v-if="profileImg" :src="profileImg" alt="" class="profile-avatar-img" />
                <span v-else>{{ nicknameInitial }}</span>
              </div>
              <div class="profile-meta">
                <strong>{{ nickname }}</strong>
                <span>{{ levelLabel }}</span>
              </div>
            </div>

            <div class="profile-stats">
              <div class="stat-item">
                <span class="stat-label">레벨</span>
                <strong>{{ levelLabel }}</strong>
              </div>
              <div class="stat-item">
                <span class="stat-label">포인트</span>
                <strong>{{ formattedPoints }}P</strong>
              </div>
            </div>

            <button class="profile-link" type="button" @click="goProfile">
              마이페이지 바로가기
            </button>
          </div>

          <button class="btn-text" type="button" @click="handleLogout">로그아웃</button>
        </template>
        <template v-else>
          <button class="btn-outline" type="button" @click="$router.push('/login')">로그인</button>
          <button class="btn-primary" type="button" @click="$router.push('/register')">회원가입</button>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onBeforeUnmount, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import api from '../api/axios'
import { setRole, clearRole, setPoints } from '../composables/useAuth'

const router = useRouter()
const route = useRoute()

watch(() => route.path, () => checkLoginStatus())

// [정보은닉] 프로필 UI에 필요한 사용자 상태를 컴포넌트 내부에서만 관리합니다.
const nickname = ref('')
const profileImg = ref('')
const level = ref(1)
const points = ref(0)
const isLoggedIn = ref(false)
const isProfileOpen = ref(false)
const profileMenuRef = ref(null)

const navLinks = [
  { path: '/main', label: '홈' },
  { path: '/words', label: '단어 목록' },
  { path: '/quiz', label: '퀴즈' },
  { path: '/error-note', label: '오답노트' },
]

const nicknameInitial = computed(() => {
  return nickname.value ? nickname.value.charAt(0).toUpperCase() : 'U'
})

const levelLabel = computed(() => {
  return formatLevel(level.value)
})

const formattedPoints = computed(() => {
  return Number(points.value || 0).toLocaleString()
})

const formatLevel = (value) => {
  const labels = {
    STARTER:      'STARTER',
    NEWBIE:       'NEWBIE',
    BEGINNER:     'BEGINNER',
    INTERMEDIATE: 'INTERMEDIATE',
    ADVANCED:     'ADVANCED',
    HIGHLEVEL:    'HIGHLEVEL',
  };
  return labels[value] ?? `Level ${Number(value) || 1}`;
}

// [캡슐화] 백엔드 레벨 필드명이 바뀌어도 이 어댑터에서만 흡수합니다.
const resolveProfileLevel = (data) => {
  return data.level ?? data.userLevel ?? 1
}

// [캡슐화] 포인트 산정 기능이 붙으면 응답 필드 연결을 이 어댑터에서 확장합니다.
const resolveProfilePoints = (data) => {
  return data.points ?? data.point ?? 0
}

const toggleProfileMenu = () => {
  isProfileOpen.value = !isProfileOpen.value
}

const closeProfileMenu = () => {
  isProfileOpen.value = false
}

const goProfile = () => {
  closeProfileMenu()
  router.push('/me')
}

const handleDocumentClick = (event) => {
  if (!profileMenuRef.value?.contains(event.target)) {
    closeProfileMenu()
  }
}

const handleKeydown = (event) => {
  if (event.key === 'Escape') {
    closeProfileMenu()
  }
}

const checkLoginStatus = async () => {
  try {
    const { data } = await api.get('/auth/me')
    nickname.value = data.nickname
    profileImg.value = data.profileImg || ''
    level.value = resolveProfileLevel(data)
    points.value = resolveProfilePoints(data)
    setRole(data.role)
    setPoints(data.points ?? 0)
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
    closeProfileMenu()
    clearRole()
    router.push('/')
  }
}

onMounted(() => {
  checkLoginStatus()
  document.addEventListener('click', handleDocumentClick)
  document.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleDocumentClick)
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped>
:root {
  --primary-color: #6B4EFF;
  --primary-hover: #5538e6;
  --bg-color: #ffffff;
  --text-main: #111827;
  --text-muted: #6b7280;
  --border-color: #f3f4f6;
}

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

.brand-logo {
  font-family: 'Pretendard', 'Inter', sans-serif;
  font-size: 24px;
  font-weight: 800;
  color: var(--text-main, #111827);
  text-decoration: none;
  letter-spacing: -0.5px;
  flex-shrink: 0 !important;
  white-space: nowrap;
  display: inline-block;
}

.brand-logo .highlight {
  color: var(--primary-color, #6B4EFF);
}

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

.user-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

.user-avatar {
  background-color: var(--primary-color, #6B4EFF);
  color: #ffffff;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  min-width: 36px !important;
  min-height: 36px !important;
  aspect-ratio: 1 / 1 !important;
  flex-shrink: 0 !important;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(107, 78, 255, 0.2);
  overflow: hidden;
}

.user-avatar:hover {
  background-color: var(--primary-hover, #5538e6);
  transform: translateY(-1px);
}

.avatar-img,
.profile-avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-text {
  font-weight: 700;
  font-size: 14px;
  line-height: 1;
  margin: 0;
  padding: 0;
  font-variant-numeric: tabular-nums;
}

.profile-popover {
  position: absolute;
  top: 48px;
  right: 58px;
  width: 260px;
  padding: 16px;
  background: #ffffff;
  border: 1px solid #E4E3EC;
  border-radius: 12px;
  box-shadow: 0 18px 40px rgba(26, 26, 46, 0.14);
  z-index: 1001;
}

.profile-popover::before {
  content: '';
  position: absolute;
  top: -7px;
  right: 22px;
  width: 12px;
  height: 12px;
  background: #ffffff;
  border-left: 1px solid #E4E3EC;
  border-top: 1px solid #E4E3EC;
  transform: rotate(45deg);
}

.profile-summary {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: 14px;
  border-bottom: 1px solid #f0f0ff;
}

.profile-avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #6B4EFF;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-weight: 700;
  flex-shrink: 0;
}

.profile-meta {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.profile-meta strong {
  color: #111827;
  font-size: 15px;
  font-weight: 800;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.profile-meta span {
  color: #6B4EFF;
  font-size: 12px;
  font-weight: 700;
}

.profile-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  margin: 14px 0;
}

.stat-item {
  background: #F7F6F2;
  border: 1px solid #E4E3EC;
  border-radius: 8px;
  padding: 10px;
}

.stat-label {
  display: block;
  color: #9090A0;
  font-size: 11px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-item strong {
  color: #1A1A2E;
  font-size: 14px;
  font-weight: 800;
}

.profile-link {
  width: 100%;
  padding: 11px 12px;
  background: #6B4EFF;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-weight: 700;
}

.profile-link:hover {
  background: #5538e6;
}

button {
  font-family: inherit;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  white-space: nowrap;
  flex-shrink: 0;
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

@media (max-width: 720px) {
  .navbar-content {
    height: auto;
    min-height: 70px;
    flex-wrap: wrap;
    gap: 12px;
    padding: 14px 18px;
  }

  .nav-links {
    order: 3;
    width: 100%;
    overflow-x: auto;
  }

  .profile-popover {
    right: 0;
  }
}
</style>
