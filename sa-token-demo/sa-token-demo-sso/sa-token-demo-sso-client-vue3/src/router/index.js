import { createRouter, createWebHashHistory } from 'vue-router';

/**
 * 创建 vue-router 实例
 */
const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        // 首页
        {
            name: 'index',
            path: "/index",
            component: () => import('../views/sso-index.vue'),
        },
        // SSO-登录页
        {
            name: 'sso-login',
            path: '/sso-login',
            component: () => import('../views/sso-login.vue'),
        },

        // 访问 / 时自动重定向到 /index
        {
            path: "/",
            redirect: '/index'
        }
    ],
});

// 导出
export default router;
