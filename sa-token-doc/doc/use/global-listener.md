# 全局侦听器

接口`SaTokenListener`是Sa-Token的全局侦听器，通过实现此接口，你可以在用户登陆、退出、被踢下线等关键性操作时进行一些AOP操作 

框架对此侦听器的默认实现是log日志输出，你可以通过配置`sa-token.is-log=true`开启

下面我们演示一下如何自定义侦听器的实现：

--- 


### 自定义侦听器实现

新建`MySaTokenListener.java`，继承`SaTokenListener`接口, 并添加上注解`@Component`，保证此类被`SpringBoot`扫描到
``` java
/**
 * 自定义侦听器的实现 
 */
@Component
public class MySaTokenListener implements SaTokenListener {

	/** 每次登录时触发 */
	@Override
	public void doLogin(String loginType, Object loginId, SaLoginModel loginModel) {
		// ... 
	}

	/** 每次注销时触发 */
	@Override
	public void doLogout(String loginType, Object loginId, String tokenValue) {
		// ... 
	}

	/** 每次被踢下线时触发 */
	@Override
	public void doLogoutByLoginId(String loginType, Object loginId, String tokenValue, String device) {
		// ... 
	}

	/** 每次被顶下线时触发 */
	@Override
	public void doReplaced(String loginType, Object loginId, String tokenValue, String device) {
		// ... 
	}

	/** 每次被封禁时触发 */
	@Override
	public void doDisable(String loginType, Object loginId, long disableTime) {
		// ... 
	}

	/** 每次被解封时触发 */
	@Override
	public void doUntieDisable(String loginType, Object loginId) {
		// ... 
	}

	/** 每次创建Session时触发 */
	@Override
	public void doCreateSession(String id) {
		// ... 
	}

	/** 每次注销Session时触发 */
	@Override
	public void doLogoutSession(String id) {
		// ... 
	}

}
```

