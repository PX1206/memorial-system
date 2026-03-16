import { usePermissionStore } from '@/stores/permission'

export function setupPermissionDirective(app) {
  app.directive('permission', {
    mounted(el, binding) {
      const permissionStore = usePermissionStore()
      const value = binding.value
      if (!value) return
      if (!permissionStore.hasPermission(value)) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    }
  })
}
