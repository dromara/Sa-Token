# 更新日志 

### 2020-12-16 @v1.5.1
- 新增：细化未登录异常类型，提供五种场景值：未提供token、token无效、token已过期 、token已被顶下线、token已被踢下线 **[重要]**
- 修复：修复`StpUtil.getSessionByLoginId(String loginId)`方法转换key出错的bug，感谢群友 @(＃°Д°)、@一米阳光 发现的bug 
- 优化：修改方法`StpUtil.getSessionByLoginId(Object loginId)`的isCreate值默认为true 
- 修改：`方法delSaSession`修改为`deleteSaSession`，更加语义化的函数名称 
- 新增：新增`StpUtil.getTokenName()`方法，更语义化的获取tokenName 
- 新增：新增`SaTokenAction`框架行为Bean，方便重写逻辑 
- 优化：`Cookie操作`改为接口代理模式，使其可以被重写 
- 优化：文档里集成redis部分增加redis的pom依赖示例
- 修复：登录验证-> `StpUtil.getLoginId_defaultNull()` 修复方法名错误的问题 
- 优化：优化`readme.md` 
- 升级：开源协议修改为`MIT`

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
