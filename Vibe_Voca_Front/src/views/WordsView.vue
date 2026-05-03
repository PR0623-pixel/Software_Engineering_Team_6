<template>
  <div class="words-page">
    <NavBar />
    <div class="words-content">
      <header class="words-header">
        <h1 class="page-title">단어장</h1>
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
            <span v-if="word.partOfSpeech" class="word-pos">{{ word.partOfSpeech }}</span>
          </div>
          <div class="word-sub">
            <span class="word-ko">{{ word.koreanMeaning }}</span>
            <span v-if="word.difficulty" class="difficulty-badge" :class="'diff-' + word.difficulty">
              {{ difficultyLabel(word.difficulty) }}
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
            <span v-if="selectedWord.partOfSpeech" class="modal-pos">{{ selectedWord.partOfSpeech }}</span>
            <span v-if="selectedWord.pronunciation" class="modal-pron">{{ selectedWord.pronunciation }}</span>
          </div>
          <p class="modal-meaning">{{ selectedWord.koreanMeaning }}</p>
          <div v-if="selectedWord.etymology" class="modal-section">
            <p class="modal-label">어원</p>
            <p class="modal-text">{{ selectedWord.etymology }}</p>
          </div>
          <div v-if="selectedWord.examples?.length" class="modal-section">
            <p class="modal-label">예문</p>
            <div v-for="(ex, i) in selectedWord.examples" :key="i" class="example">
              <p class="example-en">{{ ex.exampleEn }}</p>
              <p class="example-ko">{{ ex.exampleKo }}</p>
            </div>
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

const levels = [
  { value: 0, label: '전체' },
  { value: 1, label: 'NEWBIE' },
  { value: 2, label: 'BEGINNER' },
  { value: 3, label: 'INTERMEDIATE' },
  { value: 4, label: 'ADVANCED' },
  { value: 5, label: 'HIGHLEVEL' },
];

const words = ref([]);
const loading = ref(false);
const selectedDifficulty = ref(0);
const searchQuery = ref('');
const selectedWord = ref(null);

const difficultyLabel = (d) => levels.find(l => l.value === d)?.label ?? d;

const filteredWords = computed(() => {
  const q = searchQuery.value.toLowerCase();
  return words.value.filter(w => {
    const matchSearch = !q || w.englishWord?.toLowerCase().includes(q) || w.koreanMeaning?.includes(q);
    const matchLevel = selectedDifficulty.value === 0 || w.difficulty === selectedDifficulty.value;
    return matchSearch && matchLevel;
  });
});

const fetchWords = async () => {
  loading.value = true;
  try {
    const params = {};
    if (selectedDifficulty.value !== 0) params.difficulty = selectedDifficulty.value;
    const { data } = await api.get('/api/words', { params });
    words.value = Array.isArray(data) ? data : data.content ?? [];
  } catch (e) {
    words.value = [];
  } finally {
    loading.value = false;
  }
};

const setDifficulty = (d) => {
  selectedDifficulty.value = d;
};

const openModal = (word) => { selectedWord.value = word; };
const closeModal = () => { selectedWord.value = null; };

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

.words-header { margin-bottom: 28px; }

.page-title {
  font-family: 'Syne', sans-serif;
  font-size: 22px;
  font-weight: 800;
  color: var(--text);
}

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
.diff-1 { background: #EAF3DE; color: #3B6D11; }
.diff-2 { background: #E6F1FB; color: #185FA5; }
.diff-3 { background: #FAEEDA; color: #854F0B; }
.diff-4 { background: #FCEBEB; color: #A32D2D; }
.diff-5 { background: #2e1a3a; color: #e0aaff; }

/* 모달 */
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
.modal-pron { font-size: 13px; color: var(--accent); }

.modal-meaning {
  font-size: 18px;
  font-weight: 500;
  color: var(--text);
  margin-bottom: 20px;
}

.modal-section { margin-bottom: 16px; }
.modal-label {
  font-size: 10px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  color: var(--muted);
  margin-bottom: 6px;
}
.modal-text { font-size: 13px; color: var(--text); }

.example { margin-bottom: 10px; }
.example-en { font-size: 13px; color: var(--text); line-height: 1.5; }
.example-ko { font-size: 12px; color: var(--muted); margin-top: 2px; }
</style>
