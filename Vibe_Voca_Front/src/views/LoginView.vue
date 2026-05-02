<template>
  <div class="auth-container">
    <button class="back-btn" @click="$router.push('/')">← 뒤로</button>
    <p class="form-sub">처음이신가요? <a href="#" @click.prevent="$router.push('/register')">회원가입하기</a></p>
    
    <form @submit.prevent="handleLogin">
      <div class="field">
        <label>이메일</label>
        <input type="email" v-model="loginState.email" placeholder="hello@vibevoca.com" autocomplete="email" required />
      </div>
      <div class="field">
        <label>비밀번호</label>
        <input type="password" v-model="loginState.password" placeholder="••••••••" autocomplete="current-password" required />
      </div>
      <br />
      <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
      <button type="submit" class="btn btn-purple">로그인</button>
    </form>

    <div class="dots">
      <div class="dot"></div>
      <div class="dot on"></div>
      <div class="dot"></div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api/axios';

const router = useRouter();
const loginState = reactive({ email: '', password: '' });
const errorMsg = ref('');

const handleLogin = async () => {
  errorMsg.value = '';
  try {
    await api.post('/auth/login', {
      email: loginState.email,
      password: loginState.password,
    });
    router.push('/');
  } catch (e) {
    errorMsg.value = e.response?.data?.message || '이메일 또는 비밀번호를 확인해주세요.';
  }
};
</script>

<style scoped>
@import '../assets/auth.css';

/* [캡슐화] LoginView만의 고유 스타일 */
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

.btn-purple { background: var(--accent); color: #fff; }
.btn-purple:hover { background: var(--accent-hover); }

.dot.on { background: var(--accent); width: 16px; border-radius: 3px; }
.error-msg { font-size: 12px; color: #E24B4A; margin-bottom: 10px; }
</style>