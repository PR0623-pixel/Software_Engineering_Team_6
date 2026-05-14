<template>
  <div class="auth-container">
    <div class="logo">vibe<span>voca</span></div>
    
    <button class="btn btn-purple" @click="$router.push('/login')">로그인</button>
    <button class="btn btn-dark" @click="$router.push('/register')">회원가입</button>
    
    <div class="dots">
      <div class="dot on"></div>
      <div class="dot"></div>
      <div class="dot"></div>
    </div>
  </div>
</template>

<style scoped>
@import '../assets/auth.css';

/* [캡슐화] HomeView만의 고유 스타일 */
.logo { font-family: 'Syne', sans-serif; font-size: 52px; font-weight: 800; letter-spacing: -3px; color: var(--text); text-align: center; margin-bottom: 48px; display: block; transform: scaleX(0.72); transform-origin: center; }
.logo span { color: var(--accent); }

.btn-purple { background: var(--accent); color: #fff; margin-bottom: 10px; }
.btn-purple:hover { background: var(--accent-hover); }
.btn-dark { background: var(--dark); color: #fff; }
.btn-dark:hover { background: var(--dark-hover); }

.dot.on { background: var(--accent); width: 16px; border-radius: 3px; }
</style>