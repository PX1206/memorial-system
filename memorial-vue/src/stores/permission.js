import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePermissionStore = defineStore('permission', () => {
  const permissions = ref([])
  const menuTree = ref([])

  function setPermissions(perms) {
    permissions.value = perms || []
  }

  function setMenuTree(tree) {
    menuTree.value = tree || []
  }

  function hasPermission(code) {
    if (!code) return true
    if (Array.isArray(code)) {
      return code.some(c => permissions.value.includes(c))
    }
    return permissions.value.includes(code)
  }

  function clear() {
    permissions.value = []
    menuTree.value = []
  }

  return { permissions, menuTree, setPermissions, setMenuTree, hasPermission, clear }
})
