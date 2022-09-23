# 参考：把路由拦截鉴权动态化

框架提供的示例是硬代码写死的，不过稍微做一下更改，你就可以让他动态化

--- 

参考如下：

``` java
@Override
public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(new SaInterceptor(handle -> {
		SaRouter
			.match("/**")
			.notMatch(excludePaths())
			.check(r -> StpUtil.checkLogin());
	})).addPathPatterns("/**");
}

// 动态获取哪些 path 可以忽略鉴权 
public List<String> excludePaths() {
	// 此处仅为示例，实际项目你可以写任意代码来查询这些path
	return Arrays.asList("/path1", "/path2", "/path3");
}
```

如果不仅仅是登录校验，还需要鉴权，那也很简单：
``` java
@Override
public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(new SaInterceptor(handle -> {
		// 遍历校验规则，依次鉴权 
		Map<String, String> rules = getAuthRules();
		for (String path : rules.keySet()) {
			SaRouter.match(path, () -> StpUtil.checkPermission(rules.get(path)));
		}
	})).addPathPatterns("/**");
}

// 动态获取鉴权规则 
public Map<String, String> getAuthRules() {
	// key 代表要拦截的 path，value 代表需要校验的权限 
	Map<String, String> authMap = new LinkedHashMap<>();
	authMap.put("/user/**", "user");
	authMap.put("/admin/**", "admin");
	authMap.put("/article/**", "article");
	// 更多规则 ... 
	return authMap;
}
```








