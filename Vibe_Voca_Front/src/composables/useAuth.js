import { ref } from 'vue'

const isAdmin = ref(false)

function setRole(role) {
  isAdmin.value = role === 'ADMIN'
}

function clearRole() {
  isAdmin.value = false
}

export { isAdmin, setRole, clearRole }
