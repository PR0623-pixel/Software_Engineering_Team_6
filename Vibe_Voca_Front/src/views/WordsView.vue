<template>
  <div class="words-page">
    <NavBar />
    <div class="words-content">
      <header class="words-header">
        <h1 class="page-title">단어장</h1>
        <button v-if="isAdmin" class="btn-add" @click="openCreateModal">+ 단어 추가</button>
      </header>

      <div class="controls">
        <input
          v-model="searchQuery"
          class="search-input"
          type="text"
          placeholder="단어 검색..."
        />
        <div class="difficulty-filter">
          <button
            v-for="level in levels"
            :key="level.value"
            class="filter-btn"
            :class="{ active: selectedDifficulty === level.value }"
            @click="setDifficulty(level.value)"
          >
            {{ level.label }}
          </button>
        </div>
      </div>

      <div v-if="loading" class="status-msg">불러오는 중...</div>
      <div v-else-if="filteredWords.length === 0" class="status-msg">단어가 없습니다.</div>

      <ul v-else class="word-list">
        <li
          v-for="word in filteredWords"
          :key="word.id"
          class="word-card"
          @click="openModal(word)"
        >
          <div class="word-main">
            <span class="word-en">{{ word.englishWord }}</span>
            <span v-if="word.partOfSpeech" class="word-pos">{{ posLabel(word.partOfSpeech) }}</span>
          </div>
          <div class="word-sub">
            <span class="word-ko">{{ word.koreanMeaning }}</span>
            <span v-if="word.level" class="difficulty-badge" :class="'diff-' + word.level">
              {{ difficultyLabel(word.level) }}
            </span>
          </div>
        </li>
      </ul>

      <!-- 단어 상세 모달 -->
      <div v-if="selectedWord" class="modal-overlay" @click.self="closeModal">
        <div class="modal">
          <button class="modal-close" @click="closeModal">✕</button>
          <div class="modal-header">
            <span class="modal-word">{{ selectedWord.englishWord }}</span>
            <span v-if="selectedWord.partOfSpeech" class="modal-pos">{{ posLabel(selectedWord.partOfSpeech) }}</span>
          </div>
          <p class="modal-meaning">{{ selectedWord.koreanMeaning }}</p>
          <div class="modal-level">
            <span class="difficulty-badge" :class="'diff-' + selectedWord.level">
              {{ difficultyLabel(selectedWord.level) }}
            </span>
          </div>
          <div v-if="isAdmin" class="modal-actions-row">
            <button class="btn-edit" @click="openEditModal(selectedWord)">수정</button>
            <button class="btn-delete" @click="openDeleteConfirm(selectedWord)">삭제</button>
          </div>
        </div>
      </div>

      <!-- 단어 추가 / 수정 모달 -->
      <div v-if="isFormModalOpen" class="modal-overlay" @click.self="isFormModalOpen = false">
        <div class="modal form-modal">
          <button class="modal-close" @click="isFormModalOpen = false">✕</button>
          <h3 class="form-title">{{ formMode === 'create' ? '단어 추가' : '단어 수정' }}</h3>

          <div class="form-group">
            <label>영어 단어</label>
            <input type="text" v-model="wordForm.englishWord" placeholder="예: abandon" />
          </div>
          <div class="form-group">
            <label>한국어 뜻</label>
            <input type="text" v-model="wordForm.koreanMeaning" placeholder="예: 버리다, 포기하다" />
          </div>
          <div class="form-group">
            <label>품사</label>
            <select v-model="wordForm.partOfSpeech">
              <option v-for="pos in partOfSpeechOptions" :key="pos.value" :value="pos.value">
                {{ pos.label }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>난이도</label>
            <select v-model="wordForm.level">
              <option v-for="lv in levels.filter(l => l.value)" :key="lv.value" :value="lv.value">
                {{ lv.label }}
              </option>
            </select>
          </div>

          <p v-if="formError" class="form-error">{{ formError }}</p>

          <div class="form-footer">
            <button class="btn-cancel" @click="isFormModalOpen = false">취소</button>
            <button class="btn-submit" :disabled="formLoading" @click="submitForm">
              {{ formLoading ? '처리 중...' : (formMode === 'create' ? '추가하기' : '저장하기') }}
            </button>
          </div>
        </div>
      </div>

      <!-- 삭제 확인 모달 -->
      <div v-if="isDeleteConfirmOpen" class="modal-overlay" @click.self="isDeleteConfirmOpen = false">
        <div class="modal delete-modal">
          <h3 class="delete-title">단어 삭제</h3>
          <p><strong>{{ deletingWord?.englishWord }}</strong>을(를) 삭제하시겠습니까?</p>
          <p class="delete-sub">삭제 후 복구할 수 없습니다.</p>
          <div class="form-footer">
            <button class="btn-cancel" @click="isDeleteConfirmOpen = false">취소</button>
            <button class="btn-delete-confirm" :disabled="formLoading" @click="deleteWord">
              {{ formLoading ? '삭제 중...' : '삭제하기' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import NavBar from '../components/NavBar.vue';
import api from '../api/axios';
import { isAdmin } from '../composables/useAuth';

const levels = [
  { value: '', label: '전체' },
  { value: 'NEWBIE', label: 'NEWBIE' },
  { value: 'BEGINNER', label: 'BEGINNER' },
  { value: 'INTERMEDIATE', label: 'INTERMEDIATE' },
  { value: 'ADVANCED', label: 'ADVANCED' },
  { value: 'HIGHLEVEL', label: 'HIGHLEVEL' },
];

const partOfSpeechOptions = [
  { value: 'NOUN', label: '명사 (NOUN)' },
  { value: 'VERB', label: '동사 (VERB)' },
  { value: 'ADJECTIVE', label: '형용사 (ADJECTIVE)' },
  { value: 'ADVERB', label: '부사 (ADVERB)' },
  { value: 'PRONOUN', label: '대명사 (PRONOUN)' },
  { value: 'PREPOSITION', label: '전치사 (PREPOSITION)' },
  { value: 'CONJUNCTION', label: '접속사 (CONJUNCTION)' },
  { value: 'INTERJECTION', label: '감탄사 (INTERJECTION)' },
];

const words = ref([]);
const loading = ref(false);
const selectedDifficulty = ref('');
const searchQuery = ref('');
const selectedWord = ref(null);

// CRUD 상태
const isFormModalOpen = ref(false);
const isDeleteConfirmOpen = ref(false);
const formMode = ref('create'); // 'create' | 'edit'
const editingId = ref(null);
const deletingWord = ref(null);
const wordForm = ref({ englishWord: '', koreanMeaning: '', partOfSpeech: 'NOUN', level: 'NEWBIE' });
const formError = ref('');
const formLoading = ref(false);

const difficultyLabel = (d) => levels.find(l => l.value === d)?.label ?? d;
const posLabel = (p) => partOfSpeechOptions.find(o => o.value === p)?.label.split(' ')[0] ?? p;

const filteredWords = computed(() => {
  const q = searchQuery.value.toLowerCase();
  return words.value.filter(w => {
    const matchSearch = !q || w.englishWord?.toLowerCase().includes(q) || w.koreanMeaning?.includes(q);
    const matchLevel = !selectedDifficulty.value || w.level === selectedDifficulty.value;
    return matchSearch && matchLevel;
  });
});

const fetchWords = async () => {
  loading.value = true;
  try {
    const { data } = await api.get('/api/words');
    words.value = Array.isArray(data) ? data : data.content ?? [];
  } catch {
    words.value = [];
  } finally {
    loading.value = false;
  }
};

const setDifficulty = (d) => { selectedDifficulty.value = d; };
const openModal = (word) => { selectedWord.value = word; };
const closeModal = () => { selectedWord.value = null; };

// 추가
const openCreateModal = () => {
  formMode.value = 'create';
  editingId.value = null;
  wordForm.value = { englishWord: '', koreanMeaning: '', partOfSpeech: 'NOUN', level: 'NEWBIE' };
  formError.value = '';
  isFormModalOpen.value = true;
};

// 수정
const openEditModal = (word) => {
  formMode.value = 'edit';
  editingId.value = word.id;
  wordForm.value = {
    englishWord: word.englishWord,
    koreanMeaning: word.koreanMeaning,
    partOfSpeech: word.partOfSpeech,
    level: word.level,
  };
  formError.value = '';
  selectedWord.value = null;
  isFormModalOpen.value = true;
};

// 추가/수정 공통 제출
const submitForm = async () => {
  if (!wordForm.value.englishWord.trim() || !wordForm.value.koreanMeaning.trim()) {
    formError.value = '영어 단어와 한국어 뜻은 필수입니다.';
    return;
  }
  formLoading.value = true;
  formError.value = '';
  try {
    if (formMode.value === 'create') {
      const { data } = await api.post('/api/words', wordForm.value);
      words.value.push(data);
    } else {
      const { data } = await api.put(`/api/words/${editingId.value}`, wordForm.value);
      const idx = words.value.findIndex(w => w.id === editingId.value);
      if (idx !== -1) words.value[idx] = data;
    }
    isFormModalOpen.value = false;
  } catch (e) {
    formError.value = e.response?.data?.message ?? '처리에 실패했습니다.';
  } finally {
    formLoading.value = false;
  }
};

// 삭제
const openDeleteConfirm = (word) => {
  deletingWord.value = word;
  selectedWord.value = null;
  isDeleteConfirmOpen.value = true;
};

const deleteWord = async () => {
  formLoading.value = true;
  try {
    await api.delete(`/api/words/${deletingWord.value.id}`);
    words.value = words.value.filter(w => w.id !== deletingWord.value.id);
    isDeleteConfirmOpen.value = false;
    deletingWord.value = null;
  } catch {
    // 삭제 실패 시 모달 유지
  } finally {
    formLoading.value = false;
  }
};

onMounted(fetchWords);
</script>

<style scoped>
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

.controls { margin-bottom: 20px; }

.search-input {
  width: 100%;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 11px 14px;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--text);
  outline: none;
  margin-bottom: 12px;
  transition: border-color 0.2s;
  box-sizing: border-box;
}
.search-input:focus { border-color: var(--accent); }
.search-input::placeholder { color: #C8C7D4; }

.difficulty-filter {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 5px 12px;
  border: 1px solid var(--border);
  border-radius: 20px;
  background: #fff;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.15s;
}
.filter-btn.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #fff;
}

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
}

.word-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 14px 16px;
  cursor: pointer;
  transition: border-color 0.15s, transform 0.1s;
}
.word-card:hover {
  border-color: var(--accent);
  transform: translateY(-1px);
}

.word-main {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 4px;
}
.word-en { font-size: 16px; font-weight: 500; color: var(--text); }
.word-pos { font-size: 11px; color: var(--muted); font-style: italic; }

.word-sub {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.word-ko { font-size: 13px; color: var(--muted); }

.difficulty-badge {
  font-size: 10px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 10px;
}
.diff-NEWBIE       { background: #EAF3DE; color: #3B6D11; }
.diff-BEGINNER     { background: #E6F1FB; color: #185FA5; }
.diff-INTERMEDIATE { background: #FAEEDA; color: #854F0B; }
.diff-ADVANCED     { background: #FCEBEB; color: #A32D2D; }
.diff-HIGHLEVEL    { background: #2e1a3a; color: #e0aaff; }

/* 모달 공통 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(26, 26, 46, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 24px;
}
.modal {
  background: #fff;
  border-radius: var(--radius);
  padding: 28px;
  width: 100%;
  max-width: 480px;
  max-height: 80vh;
  overflow-y: auto;
  position: relative;
  animation: fadeUp 0.2s ease;
}
.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 16px;
  color: var(--muted);
  cursor: pointer;
}
.modal-close:hover { color: var(--text); }

/* 상세 모달 */
.modal-header {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}
.modal-word {
  font-family: 'Syne', sans-serif;
  font-size: 26px;
  font-weight: 800;
  color: var(--text);
}
.modal-pos { font-size: 12px; color: var(--muted); font-style: italic; }

.modal-meaning {
  font-size: 18px;
  font-weight: 500;
  color: var(--text);
  margin-bottom: 12px;
}
.modal-level { margin-bottom: 20px; }

.modal-actions-row {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  border-top: 1px solid var(--border);
  padding-top: 16px;
  margin-top: 8px;
}

.btn-edit {
  padding: 7px 18px;
  border: 1px solid var(--accent);
  border-radius: var(--radius-sm);
  background: #fff;
  color: var(--accent);
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-edit:hover { background: var(--accent); color: #fff; }

.btn-delete {
  padding: 7px 18px;
  border: 1px solid #E24B4A;
  border-radius: var(--radius-sm);
  background: #fff;
  color: #E24B4A;
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-delete:hover { background: #E24B4A; color: #fff; }

/* 폼 모달 */
.form-modal { max-width: 420px; }
.form-title {
  font-family: 'Syne', sans-serif;
  font-size: 18px;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 14px;
}
.form-group label {
  font-size: 11px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.4px;
  color: var(--muted);
}
.form-group input,
.form-group select {
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--text);
  outline: none;
  transition: border-color 0.15s;
  background: #fff;
}
.form-group input:focus,
.form-group select:focus { border-color: var(--accent); }

.form-error {
  font-size: 12px;
  color: #E24B4A;
  margin: -4px 0 8px;
}

.form-footer {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-cancel {
  padding: 9px 18px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  background: #fff;
  color: var(--muted);
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-cancel:hover { background: #f5f5f5; }

.btn-submit {
  padding: 9px 20px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-submit:hover { background: var(--accent-hover); }
.btn-submit:disabled { background: var(--border); color: var(--muted); cursor: not-allowed; }

/* 삭제 확인 모달 */
.delete-modal { max-width: 360px; }
.delete-title {
  font-size: 16px;
  font-weight: 700;
  color: #E24B4A;
  margin-bottom: 12px;
}
.delete-sub { font-size: 12px; color: var(--muted); margin-top: 4px; }

.btn-delete-confirm {
  padding: 9px 20px;
  background: #E24B4A;
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-delete-confirm:hover { background: #c73b3a; }
.btn-delete-confirm:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
