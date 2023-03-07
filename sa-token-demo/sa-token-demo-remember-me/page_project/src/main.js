import { createApp } from 'vue'
import App from './App.vue'
import axios from 'axios' // 请求发送接收工具
import VueAxios from 'vue-axios' // vue封装axios
import qs from 'qs' // axios请求参数类型封装
import ElementPlus from 'element-plus' // elementUI for vue3
import 'element-plus/dist/index.css' // 加载elementUI样式
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 引入中文本地化组件


const app = createApp(App)

// vue组件内通过 this.$f() 来调用
app.config.globalProperties.$f = (params) => {
  return qs.stringify(params)
}

app.use(VueAxios, axios)
.use(ElementPlus, { locale: zhCn })
.mount('#app')
