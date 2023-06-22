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
            component: () => import('../views/index.vue'),
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
