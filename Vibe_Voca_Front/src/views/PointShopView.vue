<template>
  <div class="shop-page">
    <NavBar />
    <div class="shop-content">
      <header class="shop-header">
        <h1 class="page-title">포인트 샵</h1>
      </header>

      <!-- 탭 -->
      <div class="tab-bar">
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'items' }"
          @click="activeTab = 'items'"
        >아이템 목록</button>
        <button
          class="tab-btn"
          :class="{ active: activeTab === 'history' }"
          @click="activeTab = 'history'"
        >내 구매 내역</button>
        <button
          v-if="isAdmin"
          class="tab-btn"
          :class="{ active: activeTab === 'admin' }"
          @click="activeTab = 'admin'"
        >관리</button>
      </div>

      <!-- 아이템 목록 탭 -->
      <div v-if="activeTab === 'items'">
        <div v-if="itemsLoading" class="status-msg">불러오는 중...</div>
        <div v-else-if="items.length === 0" class="status-msg">등록된 아이템이 없습니다.</div>
        <div v-else class="item-grid">
          <div v-for="item in items" :key="item.id" class="item-card">
            <div class="item-img-placeholder">🎁</div>
            <div class="item-info">
              <p class="item-name">{{ item.name }}</p>
              <p class="item-desc">{{ item.description }}</p>
              <p class="item-price">{{ item.price }} P</p>
            </div>
            <button
              v-if="ownedItemIds.has(item.id)"
              class="btn-owned"
              disabled
            >보유 중</button>
            <button
              v-else
              class="btn-buy"
              :disabled="buyingId === item.id"
              @click="purchaseItem(item)"
            >{{ buyingId === item.id ? '처리 중...' : '구매하기' }}</button>
            <p v-if="buyError[item.id]" class="buy-error">{{ buyError[item.id] }}</p>
          </div>
        </div>
      </div>

      <!-- 내 구매 내역 탭 -->
      <div v-if="activeTab === 'history'">
        <div v-if="historyLoading" class="status-msg">불러오는 중...</div>
        <div v-else-if="purchases.length === 0" class="status-msg">구매 내역이 없습니다.</div>
        <ul v-else class="history-list">
          <li v-for="p in purchases" :key="p.id" class="history-item">
            <span class="history-name">{{ p.itemName }}</span>
            <span class="history-price">{{ p.pricePaid }} P</span>
            <span class="history-date">{{ formatDate(p.purchasedAt) }}</span>
          </li>
        </ul>
      </div>

      <!-- 관리 탭 (관리자 전용) -->
      <div v-if="activeTab === 'admin' && isAdmin">

        <!-- 아이템 관리 -->
        <section class="admin-section">
          <div class="admin-section-header">
            <h2 class="section-title">아이템 관리</h2>
            <button class="btn-add" @click="openCreateModal">+ 아이템 추가</button>
          </div>
          <div v-if="adminItems.length === 0" class="status-msg">아이템이 없습니다.</div>
          <ul v-else class="admin-item-list">
            <li v-for="item in adminItems" :key="item.id" class="admin-item-row">
              <span class="admin-item-name" :class="{ deleted: item.deleted }">
                {{ item.name }}
                <span v-if="item.deleted" class="deleted-badge">삭제됨</span>
              </span>
              <span class="admin-item-price">{{ item.price }} P</span>
              <div class="admin-item-actions">
                <button class="btn-edit" @click="openEditModal(item)">수정</button>
                <button
                  v-if="!item.deleted"
                  class="btn-delete"
                  @click="deleteItem(item.id)"
                >삭제</button>
              </div>
            </li>
          </ul>
        </section>

        <!-- 포인트 정책 관리 -->
        <section class="admin-section">
          <h2 class="section-title">포인트 지급 기준</h2>
          <div v-if="policies.length === 0" class="status-msg">정책이 없습니다.</div>
          <ul v-else class="policy-list">
            <li v-for="policy in policies" :key="policy.id" class="policy-row">
              <div class="policy-info">
                <span class="policy-key">{{ policyLabel(policy.policyKey) }}</span>
                <span class="policy-desc">{{ policy.description }}</span>
              </div>
              <div class="policy-edit">
                <input
                  type="number"
                  class="policy-input"
                  v-model.number="policy.points"
                  min="0"
                />
                <span class="policy-unit">P</span>
                <button class="btn-save-policy" @click="savePolicy(policy)">저장</button>
              </div>
            </li>
          </ul>
        </section>
      </div>
    </div>

    <!-- 아이템 추가/수정 모달 -->
    <div v-if="isFormModalOpen" class="modal-overlay" @click.self="isFormModalOpen = false">
      <div class="modal form-modal">
        <button class="modal-close" @click="isFormModalOpen = false">✕</button>
        <h3 class="form-title">{{ formMode === 'create' ? '아이템 추가' : '아이템 수정' }}</h3>

        <div class="form-group">
          <label>아이템 이름</label>
          <input type="text" v-model="itemForm.name" placeholder="예: 골든 배지" />
        </div>
        <div class="form-group">
          <label>설명</label>
          <input type="text" v-model="itemForm.description" placeholder="아이템 설명" />
        </div>
        <div class="form-group">
          <label>가격 (P)</label>
          <input type="number" v-model.number="itemForm.price" min="0" placeholder="100" />
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import NavBar from '../components/NavBar.vue'
import api from '../api/axios'
import { isAdmin, setPoints } from '../composables/useAuth'

const activeTab = ref('items')

// 아이템 목록
const items = ref([])
const itemsLoading = ref(false)
const buyingId = ref(null)
const buyError = ref({})

// 구매 내역
const purchases = ref([])
const historyLoading = ref(false)

// 관리자 - 아이템 전체
const adminItems = ref([])

// 관리자 - 정책
const policies = ref([])

// 아이템 폼
const isFormModalOpen = ref(false)
const formMode = ref('create')
const editingId = ref(null)
const itemForm = ref({ name: '', description: '', price: 0, imageUrl: '' })
const formError = ref('')
const formLoading = ref(false)

const ownedItemIds = computed(() => new Set(purchases.value.map(p => p.shopItemId)))

const policyLabel = (key) => {
  if (key === 'QUIZ_COMPLETE') return '퀴즈 완료'
  if (key === 'DAILY_BONUS') return '하루 첫 퀴즈 보너스'
  return key
}

const formatDate = (dt) => {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}

const fetchItems = async () => {
  itemsLoading.value = true
  try {
    const { data } = await api.get('/api/shop/items')
    items.value = data
  } catch {
    items.value = []
  } finally {
    itemsLoading.value = false
  }
}

const fetchHistory = async () => {
  historyLoading.value = true
  try {
    const { data } = await api.get('/api/shop/purchases')
    purchases.value = data
  } catch {
    purchases.value = []
  } finally {
    historyLoading.value = false
  }
}

const fetchAdminItems = async () => {
  try {
    const { data } = await api.get('/api/shop/admin/items')
    adminItems.value = data
  } catch {
    adminItems.value = []
  }
}

const fetchPolicies = async () => {
  try {
    const { data } = await api.get('/api/shop/admin/policies')
    policies.value = data
  } catch {
    policies.value = []
  }
}

const purchaseItem = async (item) => {
  buyingId.value = item.id
  buyError.value[item.id] = ''
  try {
    await api.post(`/api/shop/items/${item.id}/purchase`)
    const { data } = await api.get('/auth/me')
    setPoints(data.points)
    await fetchHistory()
  } catch (e) {
    buyError.value[item.id] = e.response?.data?.error ?? '구매에 실패했습니다.'
  } finally {
    buyingId.value = null
  }
}

const openCreateModal = () => {
  formMode.value = 'create'
  editingId.value = null
  itemForm.value = { name: '', description: '', price: 0, imageUrl: '' }
  formError.value = ''
  isFormModalOpen.value = true
}

const openEditModal = (item) => {
  formMode.value = 'edit'
  editingId.value = item.id
  itemForm.value = { name: item.name, description: item.description ?? '', price: item.price, imageUrl: item.imageUrl ?? '' }
  formError.value = ''
  isFormModalOpen.value = true
}

const submitForm = async () => {
  if (!itemForm.value.name.trim()) {
    formError.value = '아이템 이름은 필수입니다.'
    return
  }
  if (itemForm.value.price < 0) {
    formError.value = '가격은 0 이상이어야 합니다.'
    return
  }
  formLoading.value = true
  formError.value = ''
  try {
    if (formMode.value === 'create') {
      await api.post('/api/shop/admin/items', itemForm.value)
    } else {
      await api.put(`/api/shop/admin/items/${editingId.value}`, itemForm.value)
    }
    isFormModalOpen.value = false
    await fetchAdminItems()
    await fetchItems()
  } catch (e) {
    formError.value = e.response?.data?.error ?? '처리에 실패했습니다.'
  } finally {
    formLoading.value = false
  }
}

const deleteItem = async (itemId) => {
  if (!confirm('이 아이템을 삭제하시겠습니까?')) return
  try {
    await api.delete(`/api/shop/admin/items/${itemId}`)
    await fetchAdminItems()
    await fetchItems()
  } catch (e) {
    alert(e.response?.data?.error ?? '삭제에 실패했습니다.')
  }
}

const savePolicy = async (policy) => {
  try {
    await api.put(`/api/shop/admin/policies/${policy.id}`, { points: policy.points })
  } catch (e) {
    alert(e.response?.data?.error ?? '저장에 실패했습니다.')
  }
}

onMounted(async () => {
  await fetchItems()
  await fetchHistory()
  if (isAdmin.value) {
    await fetchAdminItems()
    await fetchPolicies()
  }
})
</script>

<style scoped>
.shop-page {
  min-height: 100vh;
  background: var(--bg);
}

.shop-content {
  width: 100%;
  max-width: 720px;
  margin: 0 auto;
  padding: 40px 24px;
  animation: fadeUp 0.3s ease;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.shop-header {
  margin-bottom: 24px;
}

.page-title {
  font-family: 'Syne', sans-serif;
  font-size: 22px;
  font-weight: 800;
  color: var(--text);
}

/* 탭 */
.tab-bar {
  display: flex;
  gap: 4px;
  border-bottom: 2px solid var(--border);
  margin-bottom: 28px;
}

.tab-btn {
  padding: 8px 18px;
  border: none;
  background: none;
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  font-weight: 500;
  color: var(--muted);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.15s;
}

.tab-btn.active {
  color: var(--accent);
  border-bottom-color: var(--accent);
}

.tab-btn:hover:not(.active) {
  color: var(--text);
}

/* 상태 메시지 */
.status-msg {
  text-align: center;
  color: var(--muted);
  font-size: 14px;
  padding: 40px 0;
}

/* 아이템 그리드 */
.item-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.item-card {
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  text-align: center;
  transition: border-color 0.15s, transform 0.1s;
}

.item-card:hover {
  border-color: var(--accent);
  transform: translateY(-2px);
}

.item-img-placeholder {
  font-size: 36px;
}

.item-info { width: 100%; }

.item-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text);
  margin-bottom: 4px;
}

.item-desc {
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 8px;
}

.item-price {
  font-size: 16px;
  font-weight: 700;
  color: var(--accent);
}

.btn-buy {
  width: 100%;
  padding: 8px 0;
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

.btn-buy:hover:not(:disabled) { background: var(--accent-hover); }
.btn-buy:disabled { background: var(--border); color: var(--muted); cursor: not-allowed; }

.btn-owned {
  width: 100%;
  padding: 8px 0;
  background: #f0f0ff;
  color: var(--accent);
  border: 1px solid var(--accent);
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 13px;
  font-weight: 500;
  cursor: not-allowed;
}

.buy-error {
  font-size: 11px;
  color: #E24B4A;
  width: 100%;
  text-align: center;
}

/* 구매 내역 */
.history-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 12px 16px;
  gap: 12px;
}

.history-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--text);
  flex: 1;
}

.history-price {
  font-size: 14px;
  font-weight: 700;
  color: var(--accent);
  white-space: nowrap;
}

.history-date {
  font-size: 12px;
  color: var(--muted);
  white-space: nowrap;
}

/* 관리 섹션 */
.admin-section {
  margin-bottom: 40px;
}

.admin-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.section-title {
  font-family: 'Syne', sans-serif;
  font-size: 16px;
  font-weight: 700;
  color: var(--text);
  margin-bottom: 16px;
}

.admin-section-header .section-title {
  margin-bottom: 0;
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

.admin-item-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.admin-item-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 10px 16px;
  gap: 12px;
}

.admin-item-name {
  font-size: 14px;
  color: var(--text);
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
}

.admin-item-name.deleted {
  color: var(--muted);
  text-decoration: line-through;
}

.deleted-badge {
  font-size: 10px;
  background: #fce8e8;
  color: #E24B4A;
  padding: 2px 6px;
  border-radius: 8px;
  font-weight: 500;
  text-decoration: none;
}

.admin-item-price {
  font-size: 13px;
  font-weight: 600;
  color: var(--accent);
  white-space: nowrap;
}

.admin-item-actions {
  display: flex;
  gap: 6px;
}

.btn-edit {
  padding: 5px 12px;
  border: 1px solid var(--accent);
  border-radius: var(--radius-sm);
  background: #fff;
  color: var(--accent);
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-edit:hover { background: var(--accent); color: #fff; }

.btn-delete {
  padding: 5px 12px;
  border: 1px solid #E24B4A;
  border-radius: var(--radius-sm);
  background: #fff;
  color: #E24B4A;
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.15s;
}
.btn-delete:hover { background: #E24B4A; color: #fff; }

/* 정책 */
.policy-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.policy-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  padding: 12px 16px;
  gap: 16px;
}

.policy-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.policy-key {
  font-size: 14px;
  font-weight: 600;
  color: var(--text);
}

.policy-desc {
  font-size: 11px;
  color: var(--muted);
}

.policy-edit {
  display: flex;
  align-items: center;
  gap: 6px;
}

.policy-input {
  width: 72px;
  padding: 6px 10px;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 14px;
  color: var(--text);
  outline: none;
  text-align: right;
  transition: border-color 0.15s;
}
.policy-input:focus { border-color: var(--accent); }

.policy-unit {
  font-size: 13px;
  font-weight: 600;
  color: var(--accent);
}

.btn-save-policy {
  padding: 6px 14px;
  background: var(--accent);
  color: #fff;
  border: none;
  border-radius: var(--radius-sm);
  font-family: 'DM Sans', sans-serif;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-save-policy:hover { background: var(--accent-hover); }

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
  max-width: 420px;
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

.form-group input {
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
.form-group input:focus { border-color: var(--accent); }

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
</style>
