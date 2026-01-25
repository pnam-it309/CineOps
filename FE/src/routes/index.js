import { createRouter, createWebHistory } from 'vue-router';

// Lazy load components
const Home = () => import('@/pages/HomePage.vue');
const Login = () => import('@/pages/LoginPage.vue');

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home,
    },
    {
        path: '/login',
        name: 'Login',
        component: Login,
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;
