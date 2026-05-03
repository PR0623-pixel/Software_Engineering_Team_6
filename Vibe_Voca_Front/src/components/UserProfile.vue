<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// [정보은닉 (Information Hiding)]
const userInfo = ref({
  name: '',
  email: '',
  joinDate: '',
  level: 1,
  profileImageUrl: 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="%23cccccc"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>'
})
const isLoading = ref(true)

const isPasswordModalOpen = ref(false)
const isDeleteAccountModalOpen = ref(false)
const passwordForm = ref({ current: '', new: '', confirm: '' })
const fileInput = ref(null)

// 단일 라우팅 함수만 유지 (KISS 원칙)
const goBack = () => {
  router.back() 
}

const fetchUserProfile = async () => {
  isLoading.value = true
  setTimeout(() => {
    userInfo.value = {
      ...userInfo.value,
      name: '소공이',
      email: 'sogong@team6.com',
      joinDate: '2023-09-01',
      level: 5
    }
    isLoading.value = false
  }, 500)
}

const triggerImageUpload = () => {
  fileInput.value.click()
}

const handleProfileImageUpload = (event) => {
  const file = event.target.files[0]
  if (file) {
    userInfo.value.profileImageUrl = URL.createObjectURL(file)
  }
}

const changePassword = () => {
  if (passwordForm.value.new !== passwordForm.value.confirm) {
    alert('새 비밀번호가 일치하지 않습니다.')
    return
  }
  alert('비밀번호가 성공적으로 변경되었습니다.')
  isPasswordModalOpen.value = false
}

const deleteAccount = () => {
  alert('회원 탈퇴 처리가 완료되었습니다.')
  isDeleteAccountModalOpen.value = false
}

onMounted(() => {
  fetchUserProfile()
})
</script>

<template>
  <div class="profile-container">
    <nav class="top-nav">
      <button class="nav-btn" @click="goBack">
        <span class="nav-icon">←</span> 뒤로가기
      </button>
    </nav>

    <header class="profile-header">
      <span class="badge-title">마이페이지</span>
      <h1>내 프로필</h1>
      <p>학습 현황과 내 정보를 확인하세요</p>
    </header>
    
    <div v-if="isLoading" class="loading-spinner">
      데이터를 불러오는 중입니다...
    </div>
    
    <div v-else class="profile-content">
      <section class="card">
        <div class="card-header">
          <h2>기본 정보</h2>
        </div>
        
        <div class="profile-image-section">
          <div class="image-wrapper">
            <img :src="userInfo.profileImageUrl" alt="Profile" class="profile-image" />
            <input 
              type="file" 
              ref="fileInput" 
              @change="handleProfileImageUpload" 
              accept="image/*" 
              style="display: none;" 
            />
          </div>
          <button class="btn btn-outline-primary" @click="triggerImageUpload">사진 변경</button>
        </div>

        <ul class="list-group">
          <li class="list-item">
            <span class="label">이름</span>
            <span class="value">{{ userInfo.name }}</span>
          </li>
          <li class="list-item">
            <span class="label">이메일</span>
            <span class="value">{{ userInfo.email }}</span>
          </li>
          <li class="list-item">
            <span class="label">가입일</span>
            <span class="value">{{ userInfo.joinDate }}</span>
          </li>
          <li class="list-item">
            <span class="label">현재 레벨</span>
            <span class="level-badge">Lv. {{ userInfo.level }}</span>
          </li>
        </ul>
        
        <div class="action-buttons">
          <button class="btn btn-outline" @click="isPasswordModalOpen = true">비밀번호 변경</button>
          <button class="btn btn-danger" @click="isDeleteAccountModalOpen = true">회원 탈퇴</button>
        </div>
      </section>
    </div>

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
        <div class="modal-actions">
          <button class="btn btn-outline" @click="isPasswordModalOpen = false">취소</button>
          <button class="btn btn-primary" @click="changePassword">변경하기</button>
        </div>
      </div>
    </div>

    <div v-if="isDeleteAccountModalOpen" class="modal-overlay" @click.self="isDeleteAccountModalOpen = false">
      <div class="modal-content">
        <h3 class="danger-text">회원 탈퇴</h3>
        <p>정말로 Vibe Voca를 탈퇴하시겠습니까?<br/>학습 기록이 삭제되며 복구할 수 없습니다.</p>
        <div class="modal-actions">
          <button class="btn btn-outline" @click="isDeleteAccountModalOpen = false">취소</button>
          <button class="btn btn-danger" @click="deleteAccount">탈퇴하기</button>
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

/* 네비게이션 바 스타일 (단일 버튼을 위해 정렬 변경) */
.top-nav {
  display: flex;
  justify-content: flex-start; /* 좌측 정렬로 변경 */
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

.nav-btn:hover {
  background-color: #f0f0ff;
  color: #6B4EFF;
}

.nav-icon {
  font-size: 1.1rem;
}

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

.profile-header p {
  color: #666;
  font-size: 1.1rem;
}

.card {
  background-color: #ffffff;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.04);
  border: 1px solid rgba(0, 0, 0, 0.02);
}

.card-header {
  margin-bottom: 30px;
  text-align: left;
}

.card-header h2 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #1a1a1a;
}

.profile-image-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 40px;
}

.image-wrapper {
  margin-bottom: 15px;
}

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

.label {
  width: 120px;
  font-weight: 600;
  color: #6b7280;
}

.value {
  color: #111827;
  font-weight: 600;
  flex: 1;
}

.level-badge {
  background-color: #6B4EFF;
  color: white;
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: bold;
}

.action-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.btn {
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-primary {
  background-color: #6B4EFF;
  color: white;
  border: none;
}
.btn-primary:hover { background-color: #5538e6; }

.btn-outline-primary {
  background-color: #f0f0ff;
  color: #6B4EFF;
  border: none;
}
.btn-outline-primary:hover { background-color: #e0e0ff; }

.btn-outline {
  background-color: white;
  border: 1px solid #d1d5db;
  color: #4b5563;
}
.btn-outline:hover { background-color: #f3f4f6; }

.btn-danger {
  background-color: #ef4444;
  color: white;
  border: none;
}
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
  position: relative;
  z-index: 1001;
}

.modal-content h3 {
  margin-bottom: 25px;
  font-size: 1.3rem;
  font-weight: 700;
  color: #111827;
}

.form-group {
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  color: #4b5563;
}

.form-group input {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input:focus {
  border-color: #6B4EFF;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}

.loading-spinner {
  text-align: center;
  padding: 60px;
  color: #6b7280;
  font-weight: 500;
}
</style>