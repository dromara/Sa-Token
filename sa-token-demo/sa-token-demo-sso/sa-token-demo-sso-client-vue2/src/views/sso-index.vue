<!-- 项目首页 -->
<template>
  <div>
    <h2> Sa-Token SSO-Client 应用端（前后端分离版-Vue2） </h2>
    <p>当前是否登录：<b>{{isLogin}} ({{ loginId }})</b></p>
    <p>
      <a href="javascript: null;" @click="login()" >登录</a> -
      <a href="javascript: null;" @click="doLogoutByAlone()" >单应用注销</a> -
      <a href="javascript: null;" @click="doLogoutBySingleDeviceId();">单浏览器注销</a> -
      <a href="javascript: null;" @click="doLogout();">全端注销</a> -
      <a href="javascript: null;" @click="doMyInfo();">账号资料</a>
    </p>
  </div>
</template>

<script>
import {ajax} from './sso-common.js'
import router from "@/router";

export default {
  name: 'App',
  data() {
    return {
      // 是否登录
      isLogin: false,
      // 登录账号
      loginId: ''
    }
  },
  methods: {

    // 登录
    login: function() {
      router.push('/sso-login?back=' + encodeURIComponent(location.href));
    },

    // 单应用注销
    doLogoutByAlone: function() {
      ajax('/sso/logoutByAlone', {}, function(){
        this.doIsLogin();
      }.bind(this))
    },

    // 单浏览器注销
    doLogoutBySingleDeviceId: function() {
      ajax('/sso/logout', { singleDeviceIdLogout: true }, function(){
        this.doIsLogin();
      }.bind(this))
    },

    // 全端注销
    doLogout: function () {
      ajax('/sso/logout', {  }, function(){
        this.doIsLogin();
      }.bind(this))
    },

    // 账号资料
    doMyInfo: function() {
      ajax('/sso/myInfo', {  }, function(res){
        alert(JSON.stringify(res));
      })
    },

    // 判断是否登录
    doIsLogin: function() {
      ajax('/sso/isLogin', {}, function(res){
        this.isLogin = res.data;
        this.loginId = res.loginId;
      }.bind(this))
    }
  },
  created() {
    this.doIsLogin();
  }
}
</script>
