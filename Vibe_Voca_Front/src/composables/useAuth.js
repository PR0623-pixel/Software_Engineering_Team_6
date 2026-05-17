import { ref } from 'vue'

const isAdmin = ref(false)
const userPoints = ref(0)

function setRole(role) {
  isAdmin.value = role === 'ADMIN'
}

function setPoints(pts) {
  userPoints.value = pts
}

function clearRole() {
  isAdmin.value = false
  userPoints.value = 0
}

export { isAdmin, userPoints, setRole, setPoints, clearRole }
