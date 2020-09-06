# 更新日志 

### 2020-9-7 @v1.4.0
- 优化：修改一些函数、变量名称，使其更符合阿里java代码规范
- 优化：`tokenValue`的读取优先级改为：`request` > `body` > `header` > `cookie`
- 新增：新增`isReadCookie`配置，决定是否从`cookie`里读取`token`信息 
- 优化：如果`isReadCookie`配置为`false`，那么在登录时也不会把`cookie`写入`cookie` 
- 新增：新增`getSessionByLoginId(Object loginId, boolean isCreate)`方法
- 修复：修复文档部分错误，修正群号码

### 2020-5-2 @v1.0.3
- 新增：新增 `StpUtil.checkLogin()` 方法，更符合语义化的鉴权方法
- 新增：注册拦截器时可设置 `StpLogic` ，方便不同模块不同鉴权方式
- 新增：抛出异常时增加 `loginKey` 区分，方便多账号体系鉴权处理 
- 修复：修复启动时的版本字符画版本号打印不对的bug  
- 修复：修复文档部分不正确之处
- 新增：新增文档的友情链接

### 2020-3-7 @v1.0.2
- 新增：新增注解式验证，可在路由方法中使用注解进行权限验证
- 参考：[注解式验证](use/at-check)

### 2020-2-12 @v1.0.1
- 修复：修复`StpUtil.getLoginId(T defaultValue)`取值转换错误的bug

### 2020-2-4 @v1.0.0
- 第一个版本出炉 
- GitHub：[https://github.com/click33/sa-token](https://github.com/click33/sa-token)
- gitee：[https://gitee.com/sz6/sa-token](https://gitee.com/sz6/sa-token)
