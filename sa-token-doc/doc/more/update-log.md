# 更新日志 


### 2020-5-2 @v1.0.3
- 新增：新增 `StpUtil.checkLogin()` 方法，更符合语义化的鉴权方法
- 新增：注册拦截器时可设置 `StpLogic` ，方便不同模块不同鉴权方式
- 新增：抛出异常时增加 `login_key` 区分，方便多账号体系鉴权处理 
- 修复：修复启动时的版本字符画版本号打印不对的bug  
- 修复：修复文档部分不正确之处
- 新增：新增文档的友情链接

### 2020-3-7 @v1.0.2
- 新增：新增注解式验证，可在路由方法中使用注解进行权限验证
- 参考：[注解式验证](use/at-check)

### 2020-2-12 @v1.0.1
- 修复：修复`StpUtil.getLoginId(T default_value)`取值转换错误的bug

### 2020-2-4 @v1.0.0
- 第一个版本出炉 
- GitHub：[https://github.com/click33/sa-token](https://github.com/click33/sa-token)
- gitee：[https://gitee.com/sz6/sa-token](https://gitee.com/sz6/sa-token)
