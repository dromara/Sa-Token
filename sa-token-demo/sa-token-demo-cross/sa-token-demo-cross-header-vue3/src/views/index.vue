<!-- 项目首页 -->
<template>
  <div style="text-align: center; padding-top: 200px;">
	 <h2> Sa-Token 跨域测试-header参数版，vue3 页面 </h2>
	 <p>当前是否登录：<b>{{ state.isLogin }}</b></p>
	 <p>
	   <a href="javascript:;" @click="doLogin">登录</a>&nbsp;&nbsp;
	   <a href="javascript:;" @click="doLogout">注销</a>
	 </p> 
  </div>
</template>

<script setup>
import { onMounted, reactive } from 'vue'
import { ajax } from './method-util.js'

// 是否登录
const state = reactive({
	isLogin: false
})

onMounted(function(){
	isLogin();
})

// 查询当前会话是否登录
const isLogin = function() {
	ajax('/acc/isLogin', {}, function (res) {
		state.isLogin = res.data;
	})
}

// 去登录 
const doLogin = function() {
	const param = {
		name: "zhang",
		pwd: "123456"
	}
	ajax('/acc/doLogin', param, function (res) {
		if(res.code === 200) {
			// 保存 token 
			localStorage.satoken = res.token; 
			state.isLogin = true;
			alert('登录成功，token是：' + res.token);
		} else {
			alert(res.msg);
		}
	})
}

// 去注销 
const doLogout = function() {
	ajax('/acc/logout', {}, function (res) {
		if(res.code === 200) {
			// 清除 token 
			localStorage.removeItem('satoken');
			state.isLogin = false;
			alert('注销成功');
		} else {
			alert(res.msg);
		}
	})
}

</script>
