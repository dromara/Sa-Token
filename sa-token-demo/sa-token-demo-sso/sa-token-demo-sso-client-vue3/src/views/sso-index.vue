<!-- 项目首页 -->
<template>
  <h2> Sa-Token SSO-Client 应用端（前后端分离版-Vue3） </h2>
  <p>当前是否登录：<b>{{isLogin}}</b></p>
  <p>
    <router-link :to="loginUrl">登录</router-link>&nbsp;&nbsp;
    <a :href="logoutUrl">注销</a>
  </p>
</template>

<script setup>
import { ref } from 'vue'
import {baseUrl, ajax} from './method-util.js'

// 单点登录地址
const loginUrl =  '/sso-login?back=' + encodeURIComponent(location.href);
// 单点注销地址
const logoutUrl = baseUrl + '/sso/logout?satoken=' + localStorage.satoken + '&back=' + encodeURIComponent(location.href);

// 是否登录
const isLogin = ref(false);

// 查询当前会话是否登录
ajax('/sso/isLogin', {}, function (res) {
  console.log('/isLogin 返回数据：', res);
  isLogin.value = res.data;
})

</script>
