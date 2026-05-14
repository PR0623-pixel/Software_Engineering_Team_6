import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/main.css' /* [모듈화] 프로젝트 전체 공통 스타일 적용 */
import './assets/auth.css'

const app = createApp(App)
app.use(router)
app.mount('#app')