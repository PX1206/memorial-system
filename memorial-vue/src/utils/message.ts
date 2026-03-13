import { ElMessage } from 'element-plus'

const showMessage = (message: string, type: any, duration: number) => {
  ElMessage.closeAll()

  ElMessage({
    message,
    type,
    duration,
    showClose: false
  })
}

export const msgSuccess = (message: string) => {
  showMessage(message, 'success', 1000)
}

export const msgError = (message: string) => {
  showMessage(message, 'error', 3000)
}

export const msgWarning = (message: string) => {
  showMessage(message, 'warning', 3000)
}

export const msgInfo = (message: string) => {
  showMessage(message, 'info', 2000)
}
