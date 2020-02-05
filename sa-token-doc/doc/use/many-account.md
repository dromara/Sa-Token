# 多账号验证
--- 

## 问题
- 有的时候在一个项目中，我们会设计两套账号体系，比如一个商城的user表和admin表 
- 这时候，我们就需要将两套账号的权限认证分开，防止冲突 


## 核心思想
- sa-token在设计时充分考虑了多账号体系时的各种逻辑
- 以上几篇介绍的api，都是经过 `StpUtil`类的各种静态方法进行各种验证，而如果你深入它的源码，[点此阅览](https://gitee.com/sz6/sa-token/blob/master/sa-token-dev/src/main/java/cn/dev33/satoken/stp/StpUtil.java)
- 就会发现，此类并没有任何代码逻辑，唯一做的事就是对成员变量`stpLogic`的各个API进行包装一下进行转发
- 这样做有两个优点
	- `StpLogic`类的所有函数都可以被重写，按需扩展
	- 在构造方法时随意传入一个不同的 `login_key`，就可以再造一套账号登录体系 

## 操作示例
1. 新建一个新的权限验证类，比如： `StpAdminUtil.java`
2. 将`StpUtil.java`类的全部代码复制粘贴到 `StpAdminUtil.java`里
3. 更改一下其 `login_key`， 比如：
```
	// 底层的 StpLogic 对象  
	public static StpLogic stpLogic = new StpLogic("admin");	// login_key改为admin 
```
4. 接下来就可以像调用`StpUtil.java`一样调用 `StpAdminUtil.java`了，这两套账号认证的逻辑是完全隔离的



