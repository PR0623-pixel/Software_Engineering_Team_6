<template>
  <div class="error-note-container">
    <h2>나의 오답노트</h2>
    
    <div v-if="loading" class="loading">
      데이터를 불러오는 중입니다...
    </div>
    
    <div v-else-if="errorNotes.length === 0" class="empty-state">
      오답노트가 비어있습니다. 아주 잘하고 계시네요! 🎉
    </div>
    
    <div v-else class="note-list">
      <div v-for="note in errorNotes" :key="note.errorNoteId" class="error-note-card">
        
        <div class="word-info">
          <h3 class="word-title">{{ note.englishWord }}</h3>
          <p class="word-meaning">{{ note.koreanMeaning }}</p>
        </div>
        
        <div class="user-info">
          <p class="user-answer">
            <strong>내가 적은 답:</strong> 
            <span class="incorrect-text">{{ note.submittedAnswer }}</span>
          </p>
          <p class="memo">
            <strong>메모:</strong> {{ note.memo || '메모가 없습니다.' }}
          </p>
        </div>
        
        <div class="actions">
          <button @click="deleteNote(note.errorNoteId)" class="delete-btn">삭제</button>
        </div>

      </div>
    </div>
    
    <div class="test-action" v-if="errorNotes.length > 0">
      <button @click="goToReTest" class="retest-btn">오답노트 재테스트 보기</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
// [정보은닉] 외부 API 호출 로직은 axios 인스턴스에 위임하여 처리합니다.
import axios from '@/api/axios'; 

const router = useRouter();
// [캡슐화] 컴포넌트 내부에서만 관리되는 상태값들입니다.
const errorNotes = ref([]);
const loading = ref(true);

// 💡 임시 유저 ID (실제 환경에서는 로그인 상태 관리를 통해 동적으로 가져와야 합니다)
const userId = 1; 

/**
 * [모듈화] 오답노트 목록 데이터 패칭
 */
const fetchErrorNotes = async () => {
  try {
    loading.value = true;
    const response = await axios.get('/api/error-notes', {
      params: { userId: userId }
    });
    // 백엔드에서 전달된 평탄화된 배열(List<ErrorNoteListResponseDto>)을 할당합니다.
    errorNotes.value = response.data;
  } catch (error) {
    console.error('오답노트를 불러오는 데 실패했습니다.', error);
    alert('오답노트를 불러올 수 없습니다. 서버 상태를 확인해주세요.');
  } finally {
    loading.value = false;
  }
};

/**
 * [모듈화] 개별 오답노트 삭제
 */
const deleteNote = async (id) => {
  if (!confirm('이 오답노트를 삭제하시겠습니까?')) return;
  
  try {
    await axios.delete(`/api/error-notes/${id}`);
    // 삭제 성공 시 프론트엔드 상태(배열)에서도 즉시 필터링하여 리렌더링
    errorNotes.value = errorNotes.value.filter(note => note.errorNoteId !== id);
    alert('삭제되었습니다.');
  } catch (error) {
    console.error('삭제 실패:', error);
    alert('삭제 중 오류가 발생했습니다.');
  }
};

/**
 * 재테스트 화면으로 라우팅
 */
const goToReTest = () => {
  router.push('/quiz/error-note');
};

// 라이프사이클 훅: 컴포넌트 마운트 시 최초 1회 데이터 조회
onMounted(() => {
  fetchErrorNotes();
});
</script>

<style scoped>
/* [캡슐화] 해당 컴포넌트 내에서만 적용되는 독립적인 스타일입니다. */
.error-note-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.note-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.error-note-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  background-color: white;
}
.word-info {
  flex: 1;
}
.user-info {
  flex: 1.5;
  padding: 0 15px;
  border-left: 2px solid #f0f0f0;
}
.word-title {
  margin: 0 0 5px 0;
  font-size: 1.2rem;
  color: #2c3e50;
}
.word-meaning {
  margin: 0;
  color: #7f8c8d;
}
.incorrect-text {
  color: #e74c3c;
  font-weight: 600;
  text-decoration: line-through;
}
.memo {
  font-size: 0.9rem;
  color: #34495e;
}
.actions button {
  padding: 8px 16px;
  background-color: #fff;
  color: #e74c3c;
  border: 1px solid #e74c3c;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}
.actions button:hover {
  background-color: #e74c3c;
  color: white;
}
.test-action {
  margin-top: 30px;
  text-align: center;
}
.retest-btn {
  padding: 12px 24px;
  background-color: #42b883; /* Vue 특유의 Green 컬러 사용 */
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}
.retest-btn:hover {
  background-color: #33a06f;
}
.empty-state {
  text-align: center;
  padding: 40px;
  color: #7f8c8d;
  background-color: #f9f9f9;
  border-radius: 8px;
}
</style>