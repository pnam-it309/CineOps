import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './routes'
// Import Consolidated Global Styles
import './assets/main.css'

// Import Bootstrap JS behavior (not CSS)
import 'bootstrap/dist/js/bootstrap.bundle.min.js'
import App from './App.vue'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')
