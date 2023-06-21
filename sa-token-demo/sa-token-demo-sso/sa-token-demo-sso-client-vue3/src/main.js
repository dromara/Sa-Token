import { createApp } from 'vue'
import App from './App.vue'

// createApp
const app = createApp(App);

// 安装 vue-router
import router from './router';
app.use(router);



// 绑定dom
app.mount('#app');
