// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import MainView from '../views/MainView.vue'
import WordsView from '../views/WordsView.vue'
import QuizView from '../views/QuizView.vue'
import ProfileView from '../views/ProfileView.vue'
import LevelTestView from '../views/LevelTestView.vue'

const routes = [
  { path: '/',            name: 'home',       component: HomeView },
  { path: '/login',       name: 'login',      component: LoginView },
  { path: '/register',    name: 'register',   component: RegisterView },
  { path: '/main',        name: 'main',       component: MainView },
  { path: '/words',       name: 'words',      component: WordsView },
  { path: '/quiz',        name: 'quiz',       component: QuizView },
  { path: '/level-test',  name: 'levelTest',  component: LevelTestView },
  { path: '/me',          name: 'profile',    component: ProfileView }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router