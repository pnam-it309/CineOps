import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './routes'
// Import Element Plus styles
import 'element-plus/dist/index.css'
// Import Bootstrap JS
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
// Import Bootstrap Icons
import 'bootstrap-icons/font/bootstrap-icons.css'
// Import Global Styles (Bootstrap SCSS)
import './assets/scss/styles.scss'
// Import Theme Variables
import './theme/variables.css'
// Import Dark Theme
import './theme/theme-dark.css'
import App from './App.vue'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
