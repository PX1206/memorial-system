import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './styles/index.css'

import { createPinia } from 'pinia'
import { setupPermissionDirective } from './directives/permission'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

setupPermissionDirective(app)

app.mount('#app')
