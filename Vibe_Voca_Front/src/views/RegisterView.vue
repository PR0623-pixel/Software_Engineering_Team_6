<template>
  <div class="auth-container">
    <button class="back-btn" @click="$router.push('/')">← 뒤로</button>
    <p class="form-sub">이미 계정이 있으신가요? <a href="#" @click.prevent="$router.push('/login')">로그인</a></p>
    
    <form @submit.prevent="handleRegister">
      <div class="field">
        <label>닉네임</label>
        <input type="text" v-model="regState.nickname" placeholder="나만의 이름" autocomplete="nickname" required />
      </div>
      <div class="field">
        <label>이메일</label>
        <input type="email" v-model="regState.email" placeholder="hello@vibevoca.com" autocomplete="email" required />
      </div>
      <div class="field">
        <label>비밀번호</label>
        <input type="password" v-model="regState.password" placeholder="8자 이상 입력" autocomplete="new-password" required />
        
        <div class="strength-bar">
          <div class="strength-fill" :style="{ width: pwScore + '%', background: pwColor }"></div>
        </div>
        <p class="strength-tip">{{ pwTip }}</p>
      </div>
      <br />
      <button type="submit" class="btn btn-dark">회원가입</button>
    </form>

    <div class="dots">
      <div class="dot"></div>
      <div class="dot"></div>
      <div class="dot on"></div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue';

/* [캡슐화] 폼 데이터 상태 관리 */
const regState = reactive({ nickname: '', email: '', password: '' });

/* [다형성/반응형] 비밀번호 입력값에 따라 점수, 색상, 텍스트가 동적으로 변환 (기존 JS 로직 대체) */
const pwScore = computed(() => {
  let s = 0;
  const v = regState.password;
  if (!v) return 0;
  if (v.length >= 8) s += 30;
  if (/[A-Z]/.test(v)) s += 25;
  if (/[0-9]/.test(v)) s += 25;
  if (/[^A-Za-z0-9]/.test(v)) s += 20;
  return Math.min(s, 100);
});

const pwColor = computed(() => {
  if (pwScore.value === 0) return 'transparent';
  if (pwScore.value < 40) return '#E24B4A'; // 빨강
  if (pwScore.value < 70) return '#EF9F27'; // 주황
  return '#00B894'; // 초록
});

const pwTip = computed(() => {
  if (pwScore.value === 0) return '영문 + 숫자 조합을 사용하면 더 안전해요';
  if (pwScore.value < 40) return '너무 짧아요. 8자 이상으로 늘려보세요.';
  if (pwScore.value < 70) return '조금 더 복잡하게 만들어보세요!';
  return '훌륭한 비밀번호예요!';
});

const handleRegister = () => {
  console.log('회원가입 시도:', regState);
};
</script>

<style scoped>
@import '../assets/auth.css';

/* [캡슐화] RegisterView만의 고유 스타일 */
.back-btn { background: none; border: none; font-family: 'DM Sans', sans-serif; font-size: 13px; color: var(--muted); cursor: pointer; padding: 0; margin-bottom: 32px; display: inline-flex; align-items: center; gap: 4px; transition: color 0.15s; }
.back-btn:hover { color: var(--text); }
.form-sub { font-size: 13px; color: var(--muted); margin-bottom: 24px; }
.form-sub a { color: var(--accent); text-decoration: none; }
.form-sub a:hover { text-decoration: underline; }

.field { margin-bottom: 14px; }
.field label { display: block; font-size: 11px; font-weight: 500; color: var(--muted); text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 6px; }
.field input { width: 100%; background: var(--input-bg); border: 1px solid var(--border); border-radius: var(--radius-sm); padding: 11px 13px; font-family: 'DM Sans', sans-serif; font-size: 14px; color: var(--text); outline: none; transition: border-color 0.2s, background 0.2s; }
.field input::placeholder { color: #C8C7D4; }
.field input:focus { border-color: var(--accent); background: #fff; }

.strength-bar { height: 3px; background: var(--border); border-radius: 2px; margin-top: 6px; overflow: hidden; }
.strength-fill { height: 100%; width: 0%; border-radius: 2px; transition: width 0.3s, background 0.3s; }
.strength-tip { font-size: 11px; color: var(--muted); margin-top: 4px; }

.btn-dark { background: var(--dark); color: #fff; }
.btn-dark:hover { background: var(--dark-hover); }

.dot.on { background: var(--accent); width: 16px; border-radius: 3px; }
</style>