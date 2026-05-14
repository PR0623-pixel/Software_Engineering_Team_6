<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/axios'

const router = useRouter()

const userInfo = ref({ id: null, nickname: '', email: '', status: '', profileImg: null })
const isLoading = ref(true)

const isPasswordModalOpen = ref(false)
const passwordForm = ref({ current: '', new: '', confirm: '' })
const passwordError = ref('')
const passwordLoading = ref(false)

const isNicknameModalOpen = ref(false)
const nicknameStep = ref('verify') // 'verify' | 'edit'
const nicknameVerifyPassword = ref('')
const newNickname = ref('')
const nicknameError = ref('')
const nicknameLoading = ref(false)

const isDeleteAccountModalOpen = ref(false)
const deleteConfirmPassword = ref('')
const deleteError = ref('')
const deleteLoading = ref(false)

const isImageModalOpen = ref(false)

const defaultAvatar = `data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23cccccc"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>`

const fetchUserProfile = async () => {
  isLoading.value = true
  try {
    const { data } = await api.get('/auth/me')
    userInfo.value = data
  } catch {
    router.replace('/login')
  } finally {
    isLoading.value = false
  }
}

const logout = async () => {
  try { await api.post('/api/users/logout') } catch { }
  router.replace('/login')
}

const openPasswordModal = () => {
  passwordForm.value = { current: '', new: '', confirm: '' }
  passwordError.value = ''
  isPasswordModalOpen.value = true
}

const changePassword = async () => {
  if (passwordForm.value.new !== passwordForm.value.confirm) {
    passwordError.value = '새 비밀번호가 일치하지 않습니다.'
    return
  }
  passwordLoading.value = true
  passwordError.value = ''
  try {
    await api.patch('/api/users/me/password', {
      currentPassword: passwordForm.value.current,
      newPassword: passwordForm.value.new,
    })
    isPasswordModalOpen.value = false
  } catch (e) {
    passwordError.value = e.response?.data?.message ?? '비밀번호 변경에 실패했습니다.'
  } finally {
    passwordLoading.value = false
  }
}

const openNicknameModal = () => {
  nicknameStep.value = 'verify'
  nicknameVerifyPassword.value = ''
  newNickname.value = userInfo.value.nickname
  nicknameError.value = ''
  isNicknameModalOpen.value = true
}

const verifyForNickname = async () => {
  nicknameLoading.value = true
  nicknameError.value = ''
  try {
    await api.post('/api/users/me/verify-password', { password: nicknameVerifyPassword.value })
    nicknameStep.value = 'edit'
  } catch {
    nicknameError.value = '비밀번호가 올바르지 않습니다.'
  } finally {
    nicknameLoading.value = false
  }
}

const updateNickname = async () => {
  nicknameLoading.value = true
  nicknameError.value = ''
  try {
    const { data } = await api.put('/api/users/me', { nickname: newNickname.value })
    userInfo.value.nickname = data.nickname
    isNicknameModalOpen.value = false
  } catch (e) {
    nicknameError.value = e.response?.data?.message ?? '닉네임 변경에 실패했습니다.'
  } finally {
    nicknameLoading.value = false
  }
}

const openDeleteModal = () => {
  deleteConfirmPassword.value = ''
  deleteError.value = ''
  isDeleteAccountModalOpen.value = true
}

const deleteAccount = async () => {
  if (!deleteConfirmPassword.value) {
    deleteError.value = '비밀번호를 입력해주세요.'
    return
  }
  deleteLoading.value = true
  deleteError.value = ''
  try {
    // TODO: 회원 탈퇴 API 연동 예정
    deleteError.value = '회원 탈퇴 기능은 준비 중입니다.'
  } finally {
    deleteLoading.value = false
  }
}

const goBack = () => router.back()

onMounted(fetchUserProfile)
</script>

<template>
  <div class="profile-container">
    <nav class="top-nav">
      <button class="nav-btn" @click="goBack">
        <span class="nav-icon">←</span> 뒤로가기
      </button>
      <button class="btn btn-outline logout-btn" @click="logout">로그아웃</button>
    </nav>

    <header class="profile-header">
      <span class="badge-title">마이페이지</span>
      <h1>내 프로필</h1>
      <p>나의 계정 정보를 확인하세요</p>
    </header>

    <div v-if="isLoading" class="loading-spinner">데이터를 불러오는 중입니다...</div>

    <div v-else class="profile-content">
      <section class="card">
        <div class="card-header">
          <h2>기본 정보</h2>
        </div>

        <div class="profile-image-section">
          <div class="image-wrapper">
            <img :src="userInfo.profileImg || defaultAvatar" alt="Profile" class="profile-image" />
          </div>
          <button class="btn btn-outline img-change-btn" @click="isImageModalOpen = true">이미지 변경</button>
        </div>

        <ul class="list-group">
          <li class="list-item">
            <span class="label">닉네임</span>
            <span class="value">{{ userInfo.nickname }}</span>
          </li>
          <li class="list-item">
            <span class="label">이메일</span>
            <span class="value">{{ userInfo.email }}</span>
          </li>
          <li class="list-item">
            <span class="label">계정 상태</span>
            <span class="value">{{ userInfo.status }}</span>
          </li>
        </ul>

        <div class="action-buttons">
          <button class="btn btn-outline-primary" @click="openNicknameModal">닉네임 변경</button>
          <button class="btn btn-outline" @click="openPasswordModal">비밀번호 변경</button>
          <button class="btn btn-danger" @click="openDeleteModal">회원 탈퇴</button>
        </div>
      </section>
    </div>

    <!-- 닉네임 변경 모달 -->
    <div v-if="isNicknameModalOpen" class="modal-overlay" @click.self="isNicknameModalOpen = false">
      <div class="modal-content">
        <template v-if="nicknameStep === 'verify'">
          <h3>본인 확인</h3>
          <p class="modal-desc">닉네임 변경을 위해 현재 비밀번호를 입력해주세요.</p>
          <div class="form-group">
            <label>현재 비밀번호</label>
            <input type="password" v-model="nicknameVerifyPassword" @keydown.enter="verifyForNickname" />
          </div>
          <p v-if="nicknameError" class="form-error">{{ nicknameError }}</p>
          <div class="modal-actions">
            <button class="btn btn-outline" @click="isNicknameModalOpen = false">취소</button>
            <button class="btn btn-primary" :disabled="nicknameLoading" @click="verifyForNickname">
              {{ nicknameLoading ? '확인 중...' : '확인' }}
            </button>
          </div>
        </template>

        <template v-else>
          <h3>닉네임 변경</h3>
          <div class="form-group">
            <label>새 닉네임</label>
            <input type="text" v-model="newNickname" @keydown.enter="updateNickname" />
          </div>
          <p v-if="nicknameError" class="form-error">{{ nicknameError }}</p>
          <div class="modal-actions">
            <button class="btn btn-outline" @click="isNicknameModalOpen = false">취소</button>
            <button class="btn btn-primary" :disabled="nicknameLoading" @click="updateNickname">
              {{ nicknameLoading ? '변경 중...' : '변경하기' }}
            </button>
          </div>
        </template>
      </div>
    </div>

    <!-- 비밀번호 변경 모달 -->
    <div v-if="isPasswordModalOpen" class="modal-overlay" @click.self="isPasswordModalOpen = false">
      <div class="modal-content">
        <h3>비밀번호 변경</h3>
        <div class="form-group">
          <label>현재 비밀번호</label>
          <input type="password" v-model="passwordForm.current" />
        </div>
        <div class="form-group">
          <label>새 비밀번호</label>
          <input type="password" v-model="passwordForm.new" />
        </div>
        <div class="form-group">
          <label>새 비밀번호 확인</label>
          <input type="password" v-model="passwordForm.confirm" />
        </div>
        <p v-if="passwordError" class="form-error">{{ passwordError }}</p>
        <div class="modal-actions">
          <button class="btn btn-outline" @click="isPasswordModalOpen = false">취소</button>
          <button class="btn btn-primary" :disabled="passwordLoading" @click="changePassword">
            {{ passwordLoading ? '변경 중...' : '변경하기' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 회원 탈퇴 모달 -->
    <div v-if="isDeleteAccountModalOpen" class="modal-overlay" @click.self="isDeleteAccountModalOpen = false">
      <div class="modal-content">
        <h3 class="danger-text">회원 탈퇴</h3>
        <p class="modal-desc">정말로 Vibe Voca를 탈퇴하시겠습니까?<br/>학습 기록이 삭제되며 복구할 수 없습니다.</p>
        <div class="form-group">
          <label>비밀번호 확인</label>
          <input type="password" v-model="deleteConfirmPassword" placeholder="현재 비밀번호를 입력하세요" @keydown.enter="deleteAccount" />
        </div>
        <p v-if="deleteError" class="form-error">{{ deleteError }}</p>
        <div class="modal-actions">
          <button class="btn btn-outline" @click="isDeleteAccountModalOpen = false">취소</button>
          <button class="btn btn-danger" :disabled="deleteLoading" @click="deleteAccount">
            {{ deleteLoading ? '처리 중...' : '탈퇴하기' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 프로필 이미지 변경 모달 -->
    <div v-if="isImageModalOpen" class="modal-overlay" @click.self="isImageModalOpen = false">
      <div class="modal-content">
        <h3>프로필 이미지 변경</h3>
        <p class="modal-desc">이미지 업로드 기능은 준비 중입니다.</p>
        <div class="form-group">
          <label>이미지 파일</label>
          <input type="file" accept="image/*" disabled class="file-input" />
        </div>
        <div class="modal-actions">
          <button class="btn btn-outline" @click="isImageModalOpen = false">닫기</button>
          <button class="btn btn-primary" disabled>업로드 (준비 중)</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px 20px 40px 20px;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, system-ui, Roboto, sans-serif;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.nav-btn {
  background: none;
  border: none;
  color: #6b7280;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
}
.nav-btn:hover { background-color: #f0f0ff; color: #6B4EFF; }

.nav-icon { font-size: 1.1rem; }

.logout-btn { font-size: 0.9rem; }

.profile-header {
  text-align: center;
  margin-bottom: 40px;
}

.badge-title {
  display: inline-block;
  background-color: #f0f0ff;
  color: #6B4EFF;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 700;
  margin-bottom: 15px;
}

.profile-header h1 {
  font-size: 2.2rem;
  font-weight: 800;
  color: #1a1a1a;
  margin-bottom: 10px;
}

.profile-header p { color: #666; font-size: 1.1rem; }

.card {
  background-color: #ffffff;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.card-header { margin-bottom: 30px; }
.card-header h2 { font-size: 1.4rem; font-weight: 700; color: #1a1a1a; }

.profile-image-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
}

.image-wrapper { margin-bottom: 15px; }

.profile-image {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  background-color: #f8f9fa;
  border: 3px solid #f0f0ff;
}

.list-group {
  list-style: none;
  padding: 0;
  margin: 0 0 35px 0;
  border-top: 1px solid #f3f4f6;
}

.list-item {
  padding: 20px 10px;
  border-bottom: 1px solid #f3f4f6;
  display: flex;
  align-items: center;
}

.label { width: 120px; font-weight: 600; color: #6b7280; }
.value { color: #111827; font-weight: 600; flex: 1; }

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  flex-wrap: wrap;
}

.btn {
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary { background-color: #6B4EFF; color: white; border: none; }
.btn-primary:hover { background-color: #5538e6; }
.btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.btn-outline-primary { background-color: #f0f0ff; color: #6B4EFF; border: none; }
.btn-outline-primary:hover { background-color: #e0e0ff; }

.btn-outline { background-color: white; border: 1px solid #d1d5db; color: #4b5563; }
.btn-outline:hover { background-color: #f3f4f6; }

.btn-danger { background-color: #ef4444; color: white; border: none; }
.btn-danger:hover { background-color: #dc2626; }

.danger-text { color: #ef4444; }

.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: #ffffff;
  padding: 35px;
  border-radius: 16px;
  width: 90%;
  max-width: 420px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

.modal-content h3 { margin-bottom: 16px; font-size: 1.3rem; font-weight: 700; color: #111827; }
.modal-desc { font-size: 0.9rem; color: #6b7280; margin-bottom: 20px; }

.form-group { margin-bottom: 18px; display: flex; flex-direction: column; }
.form-group label { margin-bottom: 8px; font-size: 0.9rem; font-weight: 600; color: #4b5563; }
.form-group input {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.2s;
}
.form-group input:focus { border-color: #6B4EFF; }

.form-error { font-size: 0.85rem; color: #ef4444; margin: -8px 0 8px; }

.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; }

.loading-spinner {
  text-align: center;
  padding: 60px;
  color: #6b7280;
  font-weight: 500;
}

.img-change-btn { font-size: 0.85rem; padding: 7px 16px; }

.file-input {
  padding: 8px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9rem;
  background: #f9fafb;
  cursor: not-allowed;
}
</style>
