import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/**
 * 路由表
 */
export const routes = [
    // 首页
    {
        name: 'index',
        path: "/index",
        component: () => import('../views/sso-index.vue')
    },
    // SSO-登录页
    {
        name: 'sso-login',
        path: '/sso-login',
        component: () => import('../views/sso-login.vue')
    },

    // 访问 / 时自动重定向到 /index
    {
        path: '/',
        redirect: '/index'
    }
]

const router = new Router({
    routes: routes
})

export default router
