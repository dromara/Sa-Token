<!-- 项目首页 -->
<template>
  <div>
    <h2> Sa-Token SSO-Client 应用端（前后端分离版-Vue2） </h2>
    <p>当前是否登录：<b>{{isLogin}}</b></p>
    <p>
      <router-link :to="loginUrl">登录</router-link>&nbsp;&nbsp;
      <a :href="logoutUrl">注销</a>
    </p>
  </div>
</template>

<script>
import {baseUrl, ajax} from './method-util.js'

export default {
  name: 'App',
  data() {
    return {
      // 单点登录地址
      loginUrl: '/sso-login?back=' + encodeURIComponent(location.href),
      // 单点注销地址
      logoutUrl: baseUrl + '/sso/logout?satoken=' + localStorage.satoken + '&back=' + encodeURIComponent(location.href),
      // 是否登录
      isLogin: false
    }
  },
  created() {
    // 查询当前会话是否登录
    ajax('/sso/isLogin', {}, function (res) {
      console.log('/isLogin 返回数据：', res);
      this.isLogin = res.data;
    }.bind(this))
  }
}
</script>
