import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './routes'
// Import Bootstrap JS (only behavior scripts, not full CSS)
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
// Import Bootstrap Icons CSS (icons only, ~100KB)
import 'bootstrap-icons/font/bootstrap-icons.css'
// Import Global Styles
import './assets/scss/styles.scss'
// Import Theme Variables
import './assets/theme/variables.css'
import './assets/css/admin-premium.css'
import './assets/css/admin-tables.css' // Shared admin table typography
import './assets/scss/branding.scss'   // CineOps Brand Identity & UI Overrides
import './assets/scss/buttons.scss'    // Premium Unified Button System
import './assets/scss/badges.scss'     // Premium Unified Badge System
import App from './App.vue'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
