<!-- Sa-Token-SSO-Client端-登录页 -->
<template>
  <div>加载中...</div>
</template>

<script setup>
import {onMounted} from "vue";
import {ajax, getParam} from './sso-common.js';
import router from '../router';

// 获取参数
const back = getParam('back') || router.currentRoute.value.query.back;
const ticket = getParam('ticket') || router.currentRoute.value.query.ticket;

console.log('获取 back 参数：', back)
console.log('获取 ticket 参数：', ticket)

// 页面加载后触发
onMounted(() => {
  if(ticket) {
    doLoginByTicket(ticket);
  } else {
    goSsoAuthUrl();
  }
})

// 重定向至认证中心
function goSsoAuthUrl() {
  ajax('/sso/getSsoAuthUrl', {clientLoginUrl: location.href}, function(res) {
    location.href = res.data;
  })
}

// 根据ticket值登录
function doLoginByTicket(ticket) {
  ajax('/sso/doLoginByTicket', {ticket: ticket}, function(res) {
    if(res.code === 200) {
      localStorage.setItem('satoken', res.data);
      location.href = decodeURIComponent(back);
    } else {
      alert(res.msg);
    }
  })
}

</script>

<style scoped>

</style>
