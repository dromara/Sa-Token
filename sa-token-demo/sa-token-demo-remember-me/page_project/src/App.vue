<template>
    <div class="mainLayoutClass">
      <div class="loginClass">
        <div class="titleClass">
          账户登录
        </div>
        <div>
          <el-input v-model="name" placeholder="账号" />
        </div>
        <div>
          <el-input v-model="passwd" placeholder="密码" />
        </div>
        <div>
          <span>
            <el-switch v-model="rememberMe" />
          </span>
          <span class="tipInfoClass" @click="rememberMe = !rememberMe">记住我</span>
        </div>
        <div>
          <el-button type="primary" style="width: 100%;" @click="loginFun">登录</el-button>
        </div>
      </div>
      <div class="stateClass">
        <div class="titleClass">当前登录状态:</div>
        <div class="titleClass">{{ loginState }}</div>
        <div>
          <el-button type="primary" style="width: 100%;" @click="checkLoginStateFun">刷新登录状态</el-button>
        </div>
        <div>
          <el-button type="danger" style="width: 100%;" @click="logoutFun">退出</el-button>
        </div>
      </div>
    </div>
</template>

<script>

export default {
  mounted () {
    if (localStorage.getItem('rememberMe') === 'true') {
      this.rememberMe = true
    }
    this.checkLoginStateFun()
  },
  data () {
    return {
      name: 'zhang',
      passwd: '123456',
      rememberMe: false,
      loginState: false
    }
  },
  methods: {
    loginFun () {
      this.axios.post('/back/user/login', this.$f({
        name: this.name,
        pwd: this.passwd,
        remember: this.rememberMe
      })).then(res => {
        if (res.status === 200) {
          this.loginState = true
          const { tokenName, tokenValue } = res.data
          localStorage.setItem('tokenName', tokenName)
          if (this.rememberMe) {
            localStorage.setItem('tokenValue', tokenValue)
          } else {
            sessionStorage.setItem('tokenValue', tokenValue)
          }
        } else {
          this.$message.error('网络异常')
        }
      }).catch(() => {
        this.$message.error('无法访问后台服务')
      })
    },
    checkLoginStateFun () {
      let tokenName, tokenValue
      tokenName = localStorage.getItem('tokenName')
      if (this.rememberMe) {
        tokenValue = localStorage.getItem('tokenValue')
      } else {
        tokenValue = sessionStorage.getItem('tokenValue')
      }
      const param = {}
      param[tokenName] = tokenValue
      this.axios.post('/back/user/state', this.$f(param))
      .then(res => {
        if (res.status === 200) {
          this.loginState = res.data.data
        } else {
          this.$message.error('网络异常')
        }
      }).catch(() => {
        this.$message.error('无法访问后台服务')
      })
    },
    logoutFun () {
      // ------------------------------------------------------------------------
      // 重复的部分可以写到外部js统一封装或通过axios的拦截器添加token参数, 这里只做演示
      let tokenName, tokenValue
      tokenName = localStorage.getItem('tokenName')
      if (this.rememberMe) {
        tokenValue = localStorage.getItem('tokenValue')
      } else {
        tokenValue = sessionStorage.getItem('tokenValue')
      }
      const param = {}
      param[tokenName] = tokenValue
      // -------------------------------------------------------------------------
      this.axios.post('/back/user/logout', this.$f(param))
      .then(res => {
        if (res.status === 200) {
          this.loginState = res.data.data
        } else {
          this.$message.error('网络异常')
        }
      }).catch(() => {
        this.$message.error('无法访问后台服务')
      })
    }
  },
  watch: {
    rememberMe (newValue, oldValue) {
      // 打开不同页面时使 记住我 的状态保持一致
      localStorage.setItem('rememberMe', newValue)
    }
  }
}
</script>

<style scoped>
.mainLayoutClass{
  padding: 0 25%;
  padding-top: 20vh;
  display: flex;
  justify-content: space-between;
  user-select: none;
}
.loginClass{
  min-width: 300px;
  height: 210px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
.titleClass{
  font-size: 22px;
  font-weight: bold;
}
.tipInfoClass{
  cursor: pointer;
}
.stateClass{
  min-width: 300px;
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}
</style>