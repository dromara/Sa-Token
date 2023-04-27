<!-- Sa-Token-SSO-Client端-登录页 -->
<template>
  <div></div>
</template>

<script>
import {ajax, getParam} from './method-util.js';
import router from '../router';


export default {
  name: 'App',
  data() {
    return {
      back: getParam('back') || router.currentRoute.query.back,
      ticket: getParam('ticket') || router.currentRoute.query.ticket
    }
  },
  // 页面加载后触发
  created() {
    console.log('获取 back 参数：', this.back)
    console.log('获取 ticket 参数：', this.ticket)

    if(this.ticket) {
      this.doLoginByTicket(this.ticket);
    } else {
      this.goSsoAuthUrl();
    }
  },
  methods: {
    // 重定向至认证中心
    goSsoAuthUrl: function() {
      ajax('/sso/getSsoAuthUrl', {clientLoginUrl: location.href}, function(res) {
        console.log('/sso/getSsoAuthUrl 返回数据', res);
        location.href = res.data;
      })
    },
    // 根据ticket值登录
    doLoginByTicket: function(ticket) {
      ajax('/sso/doLoginByTicket', {ticket: ticket}, function(res) {
        console.log('/sso/doLoginByTicket 返回数据', res);
        if(res.code === 200) {
          localStorage.setItem('satoken', res.data);
          location.href = decodeURIComponent(this.back);
        } else {
          alert(res.msg);
        }
      }.bind(this))
    }
  }

}

</script>
