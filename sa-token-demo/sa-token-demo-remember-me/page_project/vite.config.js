import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 开启代理服务
// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    host: true,
    proxy: {
      '^/back/.*$': {
        target: 'http://localhost:80'
      }
    }
  }
})
