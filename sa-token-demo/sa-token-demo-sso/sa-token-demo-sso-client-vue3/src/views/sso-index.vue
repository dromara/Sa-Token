<!-- 项目首页 -->
<template>
  <div>
    <h2> Sa-Token SSO-Client 应用端（前后端分离版-Vue3） </h2>
    <p>当前是否登录：<b>{{ state.isLogin }} ({{ state.loginId }})</b></p>
    <p>
      <a href="javascript: null;" @click="login()" >登录</a> -
      <a href="javascript: null;" @click="doLogoutByAlone()" >单应用注销</a> -
      <a href="javascript: null;" @click="doLogoutBySingleDeviceId();">单浏览器注销</a> -
      <a href="javascript: null;" @click="doLogout();">全端注销</a> -
      <a href="javascript: null;" @click="doMyInfo();">账号资料</a>
    </p>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { ajax } from './sso-common.js'
import router from "../router/index.js";

// 数据
const state = reactive({
  isLogin: false,
  loginId: '',
})

// 登录
function login() {
  router.push('/sso-login?back=' + encodeURIComponent(location.href));
}

// 单应用注销
function doLogoutByAlone() {
  ajax('/sso/logoutByAlone', {}, function(res){
    doIsLogin();
  })
}

// 单浏览器注销
function doLogoutBySingleDeviceId() {
  ajax('/sso/logout', { singleDeviceIdLogout: true }, function(res){
    doIsLogin();
  })
}

// 全端注销
function doLogout() {
  ajax('/sso/logout', {  }, function(res){
    doIsLogin();
  })
}

// 账号资料
function doMyInfo() {
  ajax('/sso/myInfo', {  }, function(res){
    alert(JSON.stringify(res));
  })
}

// 判断是否登录
function doIsLogin() {
  ajax('/sso/isLogin', {}, function(res){
    state.isLogin = res.data;
    state.loginId = res.loginId;
  })
}
doIsLogin();


</script>
