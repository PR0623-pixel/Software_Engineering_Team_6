<template>
  <div class="words-page">
    <NavBar />
    <div class="words-content">
      <header class="words-header">
        <h1 class="page-title">오답노트</h1>
        <button v-if="errorNotes.length > 0" class="btn-add" @click="goToReTest">
          재테스트 보기
        </button>
      </header>

      <div v-if="loading" class="status-msg">불러오는 중...</div>
      <div v-else-if="errorNotes.length === 0" class="status-msg">오답노트가 비어있습니다. 아주 잘하고 계시네요! 🎉</div>

      <ul v-else class="word-list">
        <li v-for="note in errorNotes" :key="note.errorNoteId" class="word-card">
          
          <div class="word-main">
            <span class="word-en">{{ note.englishWord }}</span>
            <span class="word-pos wrong-answer">내 답: <span>{{ note.submittedAnswer }}</span></span>
          </div>
          
          <div class="word-sub">
            <span class="word-ko">{{ note.koreanMeaning }}</span>
            <button class="btn-delete-sm" @click="deleteNote(note.errorNoteId)">삭제</button>
          </div>
          
          <div class="word-memo" v-if="note.memo">
            <span class="memo-label">메모</span> {{ note.memo }}
          </div>

        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from '@/api/axios'; 
import NavBar from '../components/NavBar.vue'; // 단어장과 동일하게 NavBar 추가

const router = useRouter();
const errorNotes = ref([]);
const loading = ref(true);

// ✅ 이전에 고쳤던 동적 유저 ID 로직 유지
const userId = ref(null); 

const fetchErrorNotes = async () => {
  if (!userId.value) return; 

  try {
    loading.value = true;
    const response = await axios.get('/api/error-notes', {
      params: { userId: userId.value }
    });
    errorNotes.value = response.data;
  } catch (error) {
    console.error('오답노트를 불러오는 데 실패했습니다.', error);
    alert('오답노트를 불러올 수 없습니다. 서버 상태를 확인해주세요.');
  } finally {
    loading.value = false;
  }
};

const deleteNote = async (id) => {
  if (!confirm('이 오답노트를 삭제하시겠습니까?')) return;
  
  try {
    await axios.delete(`/api/error-notes/${id}`);
    errorNotes.value = errorNotes.value.filter(note => note.errorNoteId !== id);
  } catch (error) {
    console.error('삭제 실패:', error);
    alert('삭제 중 오류가 발생했습니다.');
  }
};

const goToReTest = () => {
  // mode 쿼리를 포함하여 이동시킵니다!
  router.push('/quiz/error-note?mode=errorNote'); 
};
onMounted(async () => {
  try {
    const res = await axios.get('/auth/me'); 
    userId.value = res.data.id;
    await fetchErrorNotes(); 
  } catch (error) {
    console.error('유저 정보를 가져오지 못했습니다. 로그인이 필요합니다.', error);
    loading.value = false;
  }
});
</script>

<style scoped>
/* WordsView와 완전히 동일한 레이아웃 및 애니메이션 적용 */
.words-page {
  min-height: 100vh;
  background: var(--bg);
}

.words-content {
  width: 100%;
  max-width: 640px;
  margin: 0 auto;
  padding: 40px 24px;
  animation: fadeUp 0.3s ease;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.words-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 28px;
}

.page-title {
  font-family: 'Syne', sans-serif;
  font-size: 22px;
  font-weight: 800;
  color: var(--text);
}

.btn-add {
  padding: 7px 16px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-add:hover { background: var(--accent-hover); }

.status-msg {
  text-align: center;
  color: var(--muted);
  font-size: 14px;
  padding: 40px 0;
}

.word-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 0;
  margin: 0;
}

.word-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 16px 20px;
  transition: border-color 0.15s, transform 0.1s;
}
.word-card:hover {
  border-color: var(--accent);
  transform: translateY(-1px);
}

.word-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.word-en {font-size: 18px; font-weight: 700; color: var(--text); }
.word-pos.wrong-answer { font-size: 12px; color: #E24B4A; font-weight: 500;}

.word-sub {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.word-ko { font-size: 14px; color: var(--muted); }

.btn-delete-sm {
  padding: 4px 12px;
  border: 1px solid var(--border);
  border-radius: 12px;
  background: #fff;
  font-family: 'DM Sans', sans-serif;
  font-size: 11px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.15s;
}
.btn-delete-sm:hover {
  border-color: #E24B4A;
  color: #E24B4A;
}

.word-memo {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px dashed var(--border);
  font-size: 13px;
  color: var(--text);
  line-height: 1.4;
}
.memo-label {
  font-size: 10px;
  font-weight: 600;
  color: var(--muted);
  background: #f5f5f5;
  padding: 3px 6px;
  border-radius: 4px;
  margin-right: 6px;
  vertical-align: middle;
}
</style>