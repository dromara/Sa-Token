# Java 权限认证框架功能 测试 / 对比 / 迁移。

对比以下框架的常见功能，为项目技术栈迁移提供代码示例

- Sa-Token
- Apache Shiro
- Spring Security
- JWT


> [!TIP| label:注意事项] 
> - 因个人精力&能力有限，本篇只展示部分常见功能的对比，也欢迎大家一起贡献案例，提交pr。
> - 代码案例仓库：[https://gitee.com/sa-tokens/auth-framework-function-test](https://gitee.com/sa-tokens/auth-framework-function-test)
> - 注：本篇主要展示一些常见功能不同框架的实现差异，而非每个框架的所含功能点对比。
 

--- 


### 依赖引入

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

``` xml
<!-- Sa-Token 权限认证, 在线文档：https://sa-token.cc/ -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-boot3-starter</artifactId>
	<version>1.38.0</version>
</dependency>
```

<!------------- tab:Shiro ------------->
``` xml
<!-- Shiro 安全控制 -->
<dependency>
	<groupId>org.apache.shiro</groupId>
	<artifactId>shiro-spring-boot-web-starter</artifactId>
	<version>1.13.0</version>
</dependency>
```

<!------------- tab:SpringSecurity ------------->
``` xml
<!-- SpringSecurity -->
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
	<version>3.3.2</version>
</dependency>
```
SpringBoot 项目下一般不用特别指定 SpringSecurity 版本号

<!------------- tab:JWT ------------->
``` xml
<!-- Hutool 工具类框架，其中包含 jwt 实现 -->
<dependency>
	<groupId>cn.hutool</groupId>
	<artifactId>hutool-all</artifactId>
	<version>5.8.29</version>
</dependency>
```


<!---------------------------- tabs:end ------------------------------>



### 会话登录 & 会话状态查询

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

测试 Controller 
``` java 
@RestController
@RequestMapping("/acc/")
public class LoginController {
	
	@Autowired
	SysUserDao sysUserDao;
		
	// 测试登录 
	@RequestMapping("doLogin")
	public AjaxJson doLogin(String username, String password) {
		// 校验
		SysUser user = sysUserDao.findByUsername(username);
		if(user == null) {
			return AjaxJson.getError("用户不存在");
		}
		if(!user.getPassword().equals(password)) {
			return AjaxJson.getError("密码错误");
		}
		// 登录
		StpUtil.login(user.getId());
		StpUtil.getSession().set("user", user);
		return AjaxJson.getSuccess("登录成功");
	}
	
	// 查询登录状态 
	@RequestMapping("isLogin")
	public AjaxJson isLogin() {
		if(StpUtil.isLogin()) {
			return AjaxJson.getSuccess("已登录，账号id：" + StpUtil.getLoginId());
		}
		return AjaxJson.getError("未登录");
	}

}
```

<!------------- tab:Shiro ------------->
自定义 Realm 
``` java
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private SysUserDao sysUserDao;

    // 加载用户信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        String username = (String)token.getPrincipal();
        SysUser sysUser = sysUserDao.findByUsername(username);
        if(sysUser == null){
            return null;
        }
        return new SimpleAuthenticationInfo(
                sysUser,
                sysUser.getPassword(),
                getName()
        );
    }
	
    // 加载权限信息
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

}
```

Shiro 配置类
``` java
@Configuration
public class ShiroConfigure {

    @Bean
    public MyRealm myRealm() {
        return new MyRealm();
    }

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myRealm());
        return manager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager());
        return bean;
    }

}
```

测试 Controller 
``` java
@RestController
@RequestMapping("/acc/")
public class LoginController {

	// 测试登录  ---- http://localhost:8082/acc/doLogin?username=zhang&password=123456
	@RequestMapping("doLogin")
	public AjaxJson doLogin(String username, String password) {
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(new UsernamePasswordToken(username, password));
			return AjaxJson.getSuccess("登录成功!");
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return AjaxJson.getError(e.getMessage());
		}
	}

	// 查询登录状态  ---- http://localhost:8082/acc/isLogin
	@RequestMapping("isLogin")
	public AjaxJson isLogin() {
		Subject subject = SecurityUtils.getSubject();
		if(subject.isAuthenticated()) {
			SysUser sysUser = (SysUser)subject.getPrincipal();
			return AjaxJson.getSuccess("已登录，账号id：" + sysUser.getId());
		}
		return AjaxJson.getError("未登录");
	}

}
```

<!------------- tab:SpringSecurity ------------->

定义 SpringSecurity 配置类
``` java
@Configuration
public class SpringSecurityConfigure {

    /**
     * Spring Security的核心过滤器链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // 定义安全请求拦截规则
        httpSecurity.authorizeHttpRequests(router -> {
            router
                    // 放行接口
                    .requestMatchers("/acc/doLogin", "/acc/isLogin").permitAll()

					// 所有请求都需要认证
                    .anyRequest().authenticated(); 
                    ;
                });

        // 默认的表单登录
        httpSecurity.formLogin(withDefaults());

        // 是否启用 csrf 防御
        httpSecurity.csrf( csrf -> csrf.disable() );

        // 一些安全相关的全局响应头
        httpSecurity.headers(httpSecurityHeadersConfigurer -> {
            httpSecurityHeadersConfigurer.cacheControl(HeadersConfigurer.CacheControlConfig::disable);
            httpSecurityHeadersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable);
        });
		
        return httpSecurity.build();
    }

    /**
     * Spring Security 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
```

定义 SpringSecurity UserDetails 管理器
``` java
/**
 * 自定义 SpringSecurity UserDetails 管理器
 *
 * @author click33
 * @since 2024/8/8
 */
@Component
public class CustomUserDetailsManager implements UserDetailsManager {

    @Autowired
    SysUserDao sysUserDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserDao.findByUsername(username);
        if(sysUser == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        return User.withUsername(sysUser.getUsername())
                .password("{noop}" + sysUser.getPassword())
                .build();
    }

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

}
```

测试 Controller 

``` java
@RestController
@RequestMapping("/acc/")
public class LoginController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	SysUserDao sysUserDao;

	// 测试登录  ---- http://localhost:8083/acc/doLogin?username=zhang&password=123456
	@RequestMapping("doLogin")
	public AjaxJson doLogin(String username, String password, HttpServletRequest request) {
		try {
			// 验证账号密码
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
			usernamePasswordAuthenticationToken.setDetails(sysUserDao.findByUsername(username));
			Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
			// 存入上下文
			SecurityContextHolder.getContext().setAuthentication(authentication);
			request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
			// 返回
			return AjaxJson.getSuccess("登录成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxJson.getError(e.getMessage());
		}
	}

	// 查询登录状态  ---- http://localhost:8083/acc/isLogin
	@RequestMapping("isLogin")
	public AjaxJson isLogin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return AjaxJson.getSuccess("是否登录：" + !(authentication instanceof AnonymousAuthenticationToken))
				.set("principal", authentication.getPrincipal())
				.set("details", authentication.getDetails());
	}

}

```

<!------------- tab:JWT ------------->

测试 Controller 
``` java
@RestController
@RequestMapping("/acc/")
public class LoginController {

	@Autowired
	SysUserDao sysUserDao;

	// 测试登录 
	@RequestMapping("doLogin")
	public AjaxJson doLogin(String username, String password) {
		// 校验
		SysUser user = sysUserDao.findByUsername(username);
		if(user == null) {
			return AjaxJson.getError("用户不存在");
		}
		if(!user.getPassword().equals(password)) {
			return AjaxJson.getError("密码错误");
		}
		// 登录
		String token = JwtUtil.createToken(user.getId(), user, 60 * 60 * 2);
		return AjaxJson.getSuccess("登录成功").set("token", token);
	}

	// 查询登录状态 
	@RequestMapping("isLogin")
	public AjaxJson isLogin(HttpServletRequest request) {
		try{
			String token = request.getHeader("token");
			JWT jwt = JwtUtil.parseToken(token);
			return AjaxJson.getSuccess("已登录")
					.set("id", jwt.getPayload("userId"))
					.set("user", jwt.getPayload("user"));
		} catch (Exception e) {
			e.printStackTrace();
			return AjaxJson.getError("未登录");
		}
	}

}
```


<!---------------------------- tabs:end ------------------------------>



### 会话注销

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

``` java 
@RequestMapping("logout")
public AjaxJson logout() {
	StpUtil.logout();
	return AjaxJson.getSuccess("注销成功");
}
```

<!------------- tab:Shiro ------------->

``` java
@RequestMapping("logout")
public AjaxJson logout() {
	SecurityUtils.getSubject().logout();
	return AjaxJson.getSuccess("注销成功");
}
```

<!------------- tab:SpringSecurity ------------->
``` java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
	
	// 其它配置 ...
	
	// 注销相关配置
	httpSecurity.logout(logout -> {
		logout.logoutUrl("/acc/logout");
		logout.logoutSuccessHandler((request, response, authentication) -> {
			response.setStatus(200);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json; charset=utf-8");
			String jsonStr = new ObjectMapper().writeValueAsString(AjaxJson.getSuccess("注销成功!"));
			response.getWriter().write(jsonStr);
		});
	});

	return httpSecurity.build();
}
```

<!------------- tab:JWT ------------->
JWT 无法注销已经颁发的 token 。

<!---------------------------- tabs:end ------------------------------>



### 账号密码登录（MD5 加 salt）

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

测试 Controller 
``` java 
@RequestMapping("doLogin")
public AjaxJson doLogin(String username, String password) {
	// 校验
	SysUser user = sysUserDao.findByUsername(username);
	if(user == null) {
		return AjaxJson.getError("用户不存在");
	}
	String salt = "abc";
	if(!user.getPassword().equals(SaSecureUtil.md5(salt + password))) {
		return AjaxJson.getError("密码错误");
	}
	// 登录
	StpUtil.login(user.getId());
	StpUtil.getSession().set("user", user);
	return AjaxJson.getSuccess("登录成功");
}
```

<!------------- tab:Shiro ------------->
自定义 Realm Bean 设定密码凭证器
``` java
@Bean
public MyRealm myRealm() {
	MyRealm realm = new MyRealm();
	// 设定凭证匹配器
	HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
	credentialsMatcher.setHashAlgorithmName("md5");
	realm.setCredentialsMatcher(credentialsMatcher);
	// 返回
	return realm;
}
```

自定义 Realm 实现类 doGetAuthenticationInfo 方法返回 slat 信息
``` java
// 加载用户信息
@Override
protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
	String username = (String)token.getPrincipal();
	SysUser sysUser = sysUserDao.findByUsername(username);
	if(sysUser == null){
		return null;
	}

	return new SimpleAuthenticationInfo(
			sysUser,
			sysUser.getPassword(),
			ByteSource.Util.bytes("abc"), // 指定 slat 信息 
			getName()
	);
}
```

登录代码照旧 

<!------------- tab:SpringSecurity ------------->

CustomUserDetailsManager 的 loadUserByUsername 指定 MD5 算法

``` java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	SysUser sysUser = sysUserDao.findByUsername(username);
	if(sysUser == null){
		throw new UsernameNotFoundException("用户不存在");
	}
	return User.withUsername(sysUser.getUsername())
			.password("{MD5}" + sysUser.getPassword())
			.build();
}
```

登录时指定 salt 

``` java
@RequestMapping("doLogin")
public AjaxJson doLogin(String username, String password, HttpServletRequest request) {
	try {
		// 验证账号密码
		String salt = "abc";
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, salt + password);
		// 其它代码照旧 ... 
		
		// 返回
		return AjaxJson.getSuccess("登录成功!");
	} catch (Exception e) {
		e.printStackTrace();
		return AjaxJson.getError(e.getMessage());
	}
}
```


<!------------- tab:JWT ------------->
测试 Controller 
``` java 
@RequestMapping("doLogin")
public AjaxJson doLogin(String username, String password) {
	// 校验
	SysUser user = sysUserDao.findByUsername(username);
	if(user == null) {
		return AjaxJson.getError("用户不存在");
	}
	String salt = "abc";
	if(!user.getPassword().equals(SecureUtil.md5(salt + password))) {
		return AjaxJson.getError("密码错误");
	}
	// 登录
	String token = JwtUtil.createToken(user.getId(), user, 60 * 60 * 2);
	return AjaxJson.getSuccess("登录成功").set("token", token);
}
```

<!---------------------------- tabs:end ------------------------------>



### 从上下文获取当前登录 User 信息
<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->
``` java
// 从上下文获取当前登录 User 信息 
@RequestMapping("getCurrUser")
public AjaxJson getCurrUser() {
	return AjaxJson.getSuccess()
			.set("id", StpUtil.getLoginId())
			.set("user", StpUtil.getSession().get("user"));
}
```

<!------------- tab:Shiro ------------->

``` java
// 从上下文获取当前登录 User 信息 
@RequestMapping("getCurrUser")
public AjaxJson getCurrUser() {
	Subject subject = SecurityUtils.getSubject();
	SysUser sysUser = (SysUser)subject.getPrincipal();
	return AjaxJson.getSuccess()
			.set("id", sysUser.getId())
			.set("user", sysUser);
}
```

<!------------- tab:SpringSecurity ------------->
``` java
// 从上下文获取当前登录 User 信息 
@RequestMapping("getCurrUser")
public AjaxJson getCurrUser() {
	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	if(!(authentication instanceof AnonymousAuthenticationToken)) {
		SysUser sysUser = (SysUser)authentication.getDetails();
		return AjaxJson.getSuccess()
						.set("id", sysUser.getId())
						.set("user", sysUser);
	}
	return AjaxJson.getError("未登录");
}
```

<!------------- tab:JWT ------------->
``` java
// 从上下文获取当前登录 User 信息 
@RequestMapping("getCurrUser")
public AjaxJson getCurrUser(HttpServletRequest request) {
	try{
		String token = request.getHeader("token");
		JWT jwt = JwtUtil.parseToken(token);
		SysUser sysUser = jwt.getPayloads().get("user", SysUser.class);
		return AjaxJson.getSuccessData(sysUser);
	} catch (Exception e) {
		e.printStackTrace();
		return AjaxJson.getError("未登录");
	}
}
```

<!---------------------------- tabs:end ------------------------------>



### 从会话上下文上存取值
<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->
``` java
// 测试从从会话上下文存取值 
@RequestMapping("testSession")
public AjaxJson test() {
	SaSession session = StpUtil.getSession();

	System.out.println("从 session 上取值：" + session.get("name"));
	session.set("name", "zhang");
	System.out.println("从 session 上取值：" + session.get("name"));

	return AjaxJson.getSuccess();
}
```

<!------------- tab:Shiro ------------->

``` java
// 测试从从会话上下文存取值
@RequestMapping("testSession")
public AjaxJson test() {
	Subject subject = SecurityUtils.getSubject();
	Session session = subject.getSession();

	System.out.println("从 session 上取值：" + session.getAttribute("name"));
	session.setAttribute("name", "zhang");
	System.out.println("从 session 上取值：" + session.getAttribute("name"));

	return AjaxJson.getSuccess();
}
```

<!------------- tab:SpringSecurity ------------->

``` java
// 测试从从会话上下文存取值
@RequestMapping("testSession")
public AjaxJson testSession(HttpServletRequest request) {
	HttpSession session = request.getSession();

	System.out.println("从 session 上取值：" + session.getAttribute("name"));
	session.setAttribute("name", "zhang");
	System.out.println("从 session 上取值：" + session.getAttribute("name"));
	return AjaxJson.getSuccess();
}
```

<!------------- tab:JWT ------------->
无


<!---------------------------- tabs:end ------------------------------>






### 角色认证 & 权限认证

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

自定义 StpInterface 实现类 

``` java
@Component
public class StpInterfaceImpl implements StpInterface {

	// 加载角色信息
	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		return Arrays.asList("admin", "super-admin", "ceo");
	}

	// 加载权限信息
	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		return Arrays.asList("user:add", "user:delete", "user:update");
	}

}
```

测试 Controller 
``` java
@RestController
@RequestMapping("/jur/")
public class JurController {

	// 角色判断  ---- http://localhost:8082/jur/assertRole
	@RequestMapping("assertRole")
	public AjaxJson assertRole() {
		// is 模式，返回 true 或 false
		System.out.println("单个角色判断：" + StpUtil.hasRole("admin"));
		System.out.println("多个角色判断(and)：" + StpUtil.hasRoleAnd("admin", "dev-admin"));
		System.out.println("多个角色判断(or)：" + StpUtil.hasRoleOr("admin", "dev-admin"));

		// check 模式，无角色时抛出异常
		StpUtil.checkRole("admin");  // 单个 check
		StpUtil.checkRoleAnd("admin", "dev-admin"); // 多个 check (and)
		StpUtil.checkRoleOr("admin", "dev-admin"); // 多个 check (or)

		return AjaxJson.getSuccess();
	}

	// 权限判断  ---- http://localhost:8082/jur/assertPermission
	@RequestMapping("assertPermission")
	public AjaxJson assertPermission() {
		// is 模式，返回 true 或 false
		System.out.println("单个权限判断：" + StpUtil.hasPermission("user:add"));
		System.out.println("多个权限判断(and)：" + StpUtil.hasPermissionAnd("user:add", "user:delete22"));
		System.out.println("多个权限判断(or)：" + StpUtil.hasPermissionOr("user:add", "user:delete22"));

		// check 模式，无权限时抛出异常
		StpUtil.checkPermission("user:add");  // 单个 check
		StpUtil.checkPermissionAnd("user:add", "user:delete22"); // 多个 check (and)
		StpUtil.checkPermissionOr("user:add", "user:delete22"); // 多个 check (or)

		return AjaxJson.getSuccess();
	}

}
```


<!------------- tab:Shiro ------------->

自定义 Realm 里重写方法 doGetAuthorizationInfo

``` java
@Override
protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
	SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
	// 加载角色信息
	authorizationInfo.addRoles(Arrays.asList("admin", "super-admin", "ceo"));
	// 加载权限信息
	authorizationInfo.addStringPermissions(Arrays.asList("user:add", "user:delete", "user:update"));
	return authorizationInfo;
}
```

测试 Controller 
``` java
@RestController
@RequestMapping("/jur/")
public class JurController {

	// 角色判断 
	@RequestMapping("assertRole")
	public AjaxJson assertRole() {
		Subject subject = SecurityUtils.getSubject();

		// is 模式，返回 true 或 false
		System.out.println("单个角色判断：" + subject.hasRole("admin"));
		System.out.println("多个角色判断(and)：" + subject.hasAllRoles(Arrays.asList("admin", "dev-admin")));
		System.out.println("多个角色判断(or)：" + (subject.hasRole("admin") || subject.hasRole("dev-admin")));

		// check 模式，无角色时抛出异常
		subject.checkRole("admin");  // 单个 check
		subject.checkRoles("admin", "dev-admin"); // 多个 check (and)

		return AjaxJson.getSuccess();
	}

	// 权限判断 
	@RequestMapping("assertPermission")
	public AjaxJson assertPermission() {
		Subject subject = SecurityUtils.getSubject();

		// is 模式，返回 true 或 false
		System.out.println("单个权限判断：" + subject.isPermitted("user:add"));
		System.out.println("多个权限判断(and)：" + subject.isPermittedAll("user:add", "user:delete22"));
		System.out.println("多个权限判断(or)：" + (subject.isPermitted("user:add") || subject.isPermitted("user:delete22")));

		// check 模式，无权限时抛出异常
		subject.checkPermission("user:add");  // 单个 check
		subject.checkPermissions("user:add", "user:delete22"); // 多个 check (and)

		return AjaxJson.getSuccess();
	}

}
```

<!------------- tab:SpringSecurity ------------->

CustomUserDetailsManager 的 loadUserByUsername 里返回用户的 角色 或 权限 信息
``` java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	SysUser sysUser = sysUserDao.findByUsername(username);
	if(sysUser == null){
		throw new UsernameNotFoundException("用户不存在");
	}

    // 不可以同时返回 roles 和 authorities，因为会相互覆盖，SpringSecurity 源码有bug
	return User.withUsername(sysUser.getUsername())
			.password("{noop}" + sysUser.getPassword())
			// .roles("admin", "super-admin", "ceo")
			.authorities("user:add", "user:delete", "user:update")
			.build();
}
```

测试 Controller 
``` java
@RestController
@RequestMapping("/jur/")
public class JurController {

	// 角色判断  
	@RequestMapping("assertRole")
	public AjaxJson assertRole() {
		SecurityExpressionRoot securityExpressionRoot = new SecurityExpressionRoot(SecurityContextHolder.getContext().getAuthentication()) {};

		System.out.println("单个角色判断：" + securityExpressionRoot.hasRole("admin"));
		System.out.println("多个角色判断(and)：" + (securityExpressionRoot.hasRole("admin") && securityExpressionRoot.hasRole("dev-admin")));
		System.out.println("多个角色判断(or)：" + securityExpressionRoot.hasAnyRole("admin", "dev-admin"));

		return AjaxJson.getSuccess();
	}

	// 权限判断 
	@RequestMapping("assertPermission")
	public AjaxJson assertPermission() {
		SecurityExpressionRoot securityExpressionRoot = new SecurityExpressionRoot(SecurityContextHolder.getContext().getAuthentication()) {};

		System.out.println("单个权限判断：" + securityExpressionRoot.hasAuthority("user:add"));
		System.out.println("多个权限判断(and)：" + (securityExpressionRoot.hasAuthority("user:add") && securityExpressionRoot.hasAuthority("user:delete2")));
		System.out.println("多个权限判断(or)：" + securityExpressionRoot.hasAnyAuthority("user:add", "user:delete2"));

		return AjaxJson.getSuccess();
	}

}
```

<!------------- tab:JWT ------------->
无

<!---------------------------- tabs:end ------------------------------>




### 注解鉴权

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

SaTokenConfigure 配置注解拦截器
``` java
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
	}
}
```

测试 Controller 
``` java
@RestController
@RequestMapping("/at-check/")
public class AtCheckController {

    // 登录校验 
    @SaCheckLogin
    @RequestMapping("checkLogin")
    public AjaxJson checkLogin() {
        return AjaxJson.getSuccess();
    }

    // 角色校验 
    @SaCheckRole("admin")
    @RequestMapping("checkRole")
    public AjaxJson checkRole() {
        return AjaxJson.getSuccess();
    }

    // 权限校验 
    @SaCheckPermission("user:add")
    @RequestMapping("checkPermission")
    public AjaxJson checkPermission() {
        return AjaxJson.getSuccess();
    }

    // 忽略认证校验 
    @SaIgnore
    @SaCheckLogin
    @RequestMapping("ignoreCheck")
    public AjaxJson ignoreCheck() {
        return AjaxJson.getSuccess();
    }

}
```


<!------------- tab:Shiro ------------->

测试 Controller 
``` java
@RestController
@RequestMapping("/at-check/")
public class AtCheckController {

    // 登录校验 
    @RequiresAuthentication
    @RequestMapping("checkLogin")
    public AjaxJson checkLogin() {
        return AjaxJson.getSuccess();
    }

    // 角色校验  
    @RequiresRoles("admin")
    @RequestMapping("checkRole")
    public AjaxJson checkRole() {
        return AjaxJson.getSuccess();
    }

    // 权限校验 
    @RequiresPermissions("user:add")
    @RequestMapping("checkPermission")
    public AjaxJson checkPermission() {
        return AjaxJson.getSuccess();
    }

}
```

<!------------- tab:SpringSecurity ------------->

`SpringSecurityConfigure` 配置类加上 `@EnableMethodSecurity` 注解
``` java
@Configuration
@EnableMethodSecurity
public class SpringSecurityConfigure {
	// ...
}
```

测试 Controller 
``` java
@RestController
@RequestMapping("/at-check/")
public class AtCheckController {

    // 登录校验  
    @PreAuthorize("isAuthenticated()")
    @RequestMapping("checkLogin")
    public AjaxJson checkLogin() {
        return AjaxJson.getSuccess();
    }

    // 角色校验  
    @PreAuthorize("hasRole('admin')")
    @RequestMapping("checkRole")
    public AjaxJson checkRole() {
        return AjaxJson.getSuccess();
    }

    // 权限校验 
    @PreAuthorize("hasAuthority('user:add')")
    @RequestMapping("checkPermission")
    public AjaxJson checkPermission() {
        return AjaxJson.getSuccess();
    }

}
```

<!------------- tab:JWT ------------->
无


<!---------------------------- tabs:end ------------------------------>



### 路由拦截鉴权
<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

SaTokenConfigure 配置 
``` java
@Override
public void addInterceptors(InterceptorRegistry registry) {
	// 注册 Sa-Token 拦截器打开注解鉴权功能 
	registry.addInterceptor(new SaInterceptor(handle -> {
		SaRouter.match("/route-check/getInfo1").stop(); // 不拦截
		SaRouter.match("/route-check/getInfo2").check(r -> StpUtil.checkLogin()); // 需要登录
		SaRouter.match("/route-check/getInfo3").check(r -> StpUtil.checkRole("admin2")); // 需要角色
		SaRouter.match("/route-check/getInfo4").check(r -> StpUtil.checkPermission("user:add3")); // 需要权限
	})).addPathPatterns("/**");
}
```

<!------------- tab:Shiro ------------->

过滤器配置 
``` java
@Bean
public ShiroFilterFactoryBean shiroFilterFactoryBean() {
	ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
	bean.setSecurityManager(securityManager());

	// 路由拦截鉴权
	Map<String,String> filterMap = new LinkedHashMap<>();
	filterMap.put("/route-check/getInfo", "anon"); // 不拦截
	filterMap.put("/route-check/getInfo2", "authc"); // 需要登录
	filterMap.put("/route-check/getInfo3", "perms[admin2]"); // 需要角色
	filterMap.put("/route-check/getInfo4", "perms[user:add3]"); // 需要权限
	bean.setFilterChainDefinitionMap(filterMap);
	bean.setLoginUrl("/401");  // 未登录时跳转的 url
	bean.setUnauthorizedUrl("/403");  // 未授权时跳转的 url

	return bean;
}
```

<!------------- tab:SpringSecurity ------------->
SpringSecurityConfigure 配置 

``` java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
	// 定义安全请求拦截规则
	httpSecurity.authorizeHttpRequests(router -> {
		router
			.requestMatchers("/route-check/getInfo1").permitAll()    // 不拦截
			.requestMatchers("/route-check/getInfo2").authenticated()    // 需要登录
			.requestMatchers("/route-check/getInfo3").hasRole("admin")    // 需要 admin 角色
			.requestMatchers("/route-check/getInfo4").hasAuthority("user:add")    // 需要 user:add 权限
			.anyRequest().permitAll(); // 所有请求都放行
		});

	return httpSecurity.build();
}
```

<!------------- tab:JWT ------------->
无

<!---------------------------- tabs:end ------------------------------>



### 鉴权未通过的处理方案
<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

定义全局异常处理类 
``` java
@RestControllerAdvice
public class GlobalException {

	@ExceptionHandler(NotLoginException.class)
	public AjaxJson handlerException(NotLoginException e) {
		return AjaxJson.get(401, "未登录");
	}

	@ExceptionHandler(NotRoleException.class)
	public AjaxJson handlerException(NotRoleException e) {
		return AjaxJson.get(403, "缺少角色：" + e.getRole());
	}

	@ExceptionHandler(NotPermissionException.class)
	public AjaxJson handlerException(NotPermissionException e) {
		return AjaxJson.get(403, "缺少权限：" + e.getPermission());
	}

}
```

<!------------- tab:Shiro ------------->

过滤器配置 
``` java
@Bean
public ShiroFilterFactoryBean shiroFilterFactoryBean() {
	ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
	
	// ... 
	
	bean.setLoginUrl("/401");  // 未登录时跳转的 url
	bean.setUnauthorizedUrl("/403");  // 未授权时跳转的 url

	return bean;
}
```

定义路由 
``` java
@RestController
public class ShiroErrorController {

    @RequestMapping("/401")
    public Object error401(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(200);
        return AjaxJson.get(401, "not login");
    }

    @RequestMapping("/403")
    public Object error403(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(200);
        return AjaxJson.get(403, "鉴权未通过");
    }

}
```

<!------------- tab:SpringSecurity ------------->

实现 `AccessDeniedHandler`, `AuthenticationEntryPoint` 接口

``` java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler, AuthenticationEntryPoint, Serializable {

    // 未登录异常
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        //验证为未登陆状态会进入此方法，认证错误
        response.setStatus(401);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = "请先进行登录";
        printWriter.write(body);
        printWriter.flush();
    }

    // 权限不足
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 登陆状态下，权限不足执行该方法
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = "权限不足";
        printWriter.write(body);
        printWriter.flush();
    }
}
```

注入 `SecurityFilterChain`

``` java
// 未登录处理逻辑、权限不足处理逻辑
@Autowired
private CustomAccessDeniedHandler accessDeniedHandler;

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
	
	// 异常处理
	httpSecurity.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
		// 权限不足处理方案
		httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler);
		// 未登录 处理逻辑
		httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(accessDeniedHandler);
	});

	return httpSecurity.build();
}
```

<!------------- tab:JWT ------------->
使用 `try-catch` 捕获，或定义全局异常处理
``` java
@RestControllerAdvice
public class GlobalException {
	// 全局异常拦截（拦截项目中的所有异常）
	@ExceptionHandler
	public AjaxJson handlerException(Exception e, HttpServletRequest request, HttpServletResponse response) {

		// 打印堆栈，以供调试
		System.out.println("全局异常---------------");
		e.printStackTrace();

		// 返回给前端
		return AjaxJson.getError(e.getMessage());
	}
}
```



<!---------------------------- tabs:end ------------------------------>



### 和 Thymeleaf 集成

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

`pom.xml` 依赖 
``` xml
<!-- 在 thymeleaf 标签中使用 Sa-Token -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-dialect-thymeleaf</artifactId>
	<version>${sa-token.version}</version>
</dependency>
```

`SaTokenConfigure` 增加配置 `Sa-Token` 标签方言对象 

``` java
// Sa-Token 标签方言 (Thymeleaf版)
@Bean
public SaTokenDialect getSaTokenDialect() {
	return new SaTokenDialect();
}
```

新建 `ThymeleafConfigure` 注入全局变量
``` java
@Configuration
public class ThymeleafConfigure {
    // 为 Thymeleaf 注入全局变量，以便在页面中调用 Sa-Token 的方法
    @Autowired
    public void configureThymeleafStaticVars(ThymeleafViewResolver viewResolver) {
        viewResolver.addStaticVariable("stp", StpUtil.stpLogic);
    }
}
```

新建 `Controller` 
``` java
@Controller
public class HomeController {
    @RequestMapping("/")
    public Object index(HttpServletRequest request) {
        request.setAttribute("isLogin", StpUtil.isLogin());
        return new ModelAndView("index.html");
    }
}
```
	
新建 `templates/index.html`
``` html
<!DOCTYPE html>
<html lang="zh" xmlns:sa="http://www.thymeleaf.org/extras/sa-token">
<head>
    <title>Sa-Token 集成 Thymeleaf 标签方言</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<body>
<div class="view-box" style="padding: 30px;">
    <h2>Sa-Token 集成 Thymeleaf 标签方言 —— 测试页面</h2>
    <p>当前是否登录：<span th:text="${stp.isLogin()}"></span></p>
    <p>
        <a href="login" target="_blank">登录</a>
        <a href="logout" target="_blank">注销</a>
    </p>

    <p>登录之后才能显示：<span sa:login>value</span></p>
    <p>不登录才能显示：<span sa:notLogin>value</span></p>

    <p>具有角色 admin 才能显示：<span sa:hasRole="admin">value</span></p>
    <p>同时具备多个角色才能显示：<span sa:hasRoleAnd="admin, ceo, cto">value</span></p>
    <p>只要具有其中一个角色就能显示：<span sa:hasRoleOr="admin, ceo, cto">value</span></p>
    <p>不具有角色 admin 才能显示：<span sa:notRole="admin">value</span></p>

    <p>具有权限 user-add 才能显示：<span sa:hasPermission="user-add">value</span></p>
    <p>同时具备多个权限才能显示：<span sa:hasPermissionAnd="user-add, user-delete, user-get">value</span></p>
    <p>只要具有其中一个权限就能显示：<span sa:hasPermissionOr="user-add, user-delete, user-get">value</span></p>
    <p>不具有权限 user-add 才能显示：<span sa:notPermission="user-add">value</span></p>

    <p th:if="${stp.isLogin()}">
        从SaSession中取值：
        <span th:text="${stp.getSession().get('name', '')}"></span>
    </p>

</div>
</body>
</html>
```


<!------------- tab:Shiro ------------->

`pom.xml` 依赖 
``` xml
<!-- Shiro 整合 Thymeleaf 依赖 -->
<dependency>
	<groupId>com.github.theborakompanioni</groupId>
	<artifactId>thymeleaf-extras-shiro</artifactId>
	<version>2.1.0</version>
</dependency>
```

`ShiroConfigure` 增加配置 `Shiro` 方言对象 
``` java
@Bean
public ShiroDialect shiroDialect() {
	return new ShiroDialect();
}
```

新建 `Controller` 
``` java
@Controller
public class HomeController {
    @RequestMapping("/")
    public Object index(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        request.setAttribute("isLogin", subject.isAuthenticated());
        return new ModelAndView("index.html");
    }
}
```

新建 `templates/index.html`

``` html
<!DOCTYPE html>
<html lang="zh" xmlns:shiro="http://www.pollix.at/thymeleaf/shiro">
<head>
    <title>Shiro 集成 Thymeleaf 标签方言</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<body>
<div class="view-box" style="padding: 30px;">
    <h2>Shiro 集成 Thymeleaf 标签方言 —— 测试页面</h2>
    <p>当前是否登录：<span th:text="${isLogin}"></span></p>
    <p>
        <a href="/acc/doLogin?username=zhang&password=123456" target="_blank">登录</a>
        <a href="/acc/logout" target="_blank">注销</a>
    </p>
    <p>登录之后才能显示：<span shiro:authenticated>value</span></p>
    <p>不登录才能显示：<span shiro:guest >value</span></p>

    <p>具有角色 admin 才能显示：<span shiro:hasRole="admin">value</span></p>
    <p>同时具备多个角色才能显示：<span shiro:hasAllRoles="admin, ceo, cto">value</span></p>
    <p>只要具有其中一个角色就能显示：<span shiro:hasAnyRoles="admin, ceo, cto">value</span></p>
    <p>不具有角色 admin 才能显示：<span shiro:lacksRole="admin">value</span></p>

    <p>具有权限 user-add 才能显示：<span shiro:hasPermission="user-add">value</span></p>
    <p>同时具备多个权限才能显示：<span shiro:hasAllPermissions="user-add, user-delete, user-get">value</span></p>
    <p>只要具有其中一个权限就能显示：<span shiro:hasAnyPermissions="user-add, user-delete, user-get">value</span></p>
    <p>不具有权限 user-add 才能显示：<span shiro:lacksPermission="user-add">value</span></p>

    <p shiro:authenticated>
      当前登录账号：<span shiro:principal></span>
    </p>

</div>
</body>
</html>
```

<!------------- tab:SpringSecurity ------------->

`pom.xml` 引入依赖 
``` xml
<!-- SpringSecurity 整合 Thymeleaf 依赖 -->
<dependency>
	<groupId>org.thymeleaf.extras</groupId>
	<artifactId>thymeleaf-extras-springsecurity6</artifactId>
	<version>3.1.2.RELEASE</version>
</dependency>
```
		
新建 `Controller` 
``` java
@RestController
public class HomeController {
    // 首页  
    @RequestMapping("/")
    public Object index(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        request.setAttribute("isLogin", !(authentication instanceof AnonymousAuthenticationToken));
        return new ModelAndView("index.html");
    }
}
```

新建 `templates/index.html`

``` html
<!DOCTYPE html>
<html lang="zh" xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity6">
<head>
    <title>Shiro 集成 Thymeleaf 标签方言</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
</head>
<body>
<div class="view-box" style="padding: 30px;">
    <h2>Shiro 集成 Thymeleaf 标签方言 —— 测试页面</h2>
    <p>当前是否登录：<span th:text="${isLogin}"></span></p>

    <p>
        <a href="/acc/doLogin?username=zhang&password=123456" target="_blank">登录</a>
        <a href="/acc/logout" target="_blank">注销</a>
    </p>
    <p>登录之后才能显示：<span sec:authorize="isAuthenticated()">value</span></p>
    <p>不登录才能显示：<span sec:authorize="!isAuthenticated()" >value</span></p>

    <p>具有角色 admin 才能显示：<span sec:authorize="hasRole('admin')">value</span></p>
    <p>同时具备多个角色才能显示：<span sec:authorize="hasRole('admin') && hasRole('ceo') && hasRole('cto')">value</span></p>
    <p>只要具有其中一个角色就能显示：<span sec:authorize="hasAnyRole('admin', 'ceo', 'cto')">value</span></p>
    <p>不具有角色 admin 才能显示：<span sec:authorize="!hasRole('admin')">value</span></p>

    <p>具有权限 user-add 才能显示：<span sec:authorize="hasAuthority('user-add')">value</span></p>
    <p>同时具备多个权限才能显示：<span sec:authorize="hasAuthority('user-add') && hasAuthority('user-delete') && hasAuthority('user-get')">value</span></p>
    <p>只要具有其中一个权限就能显示：<span sec:authorize="hasAnyAuthority('user-add', 'user-delete', 'user-get')">value</span></p>
    <p>不具有权限 user-add 才能显示：<span sec:authorize="!hasAuthority('user-add')">value</span></p>

    <p sec:authorize="isAuthenticated()">
        当前登录账号：<span sec:authentication="details"></span>
    </p>

</div>
</body>
</html>
```

<!------------- tab:JWT ------------->
无


<!---------------------------- tabs:end ------------------------------>



### 前后端分离
<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->
1、在登录时，将 token 信息返回到前端 
``` java
// 测试登录 
@RequestMapping("doLogin")
public AjaxJson doLogin(String username, String password) {
	// 校验
	SysUser user = sysUserDao.findByUsername(username);
	// user 信息校验代码不再赘述 ... 
	
	// 登录
	StpUtil.login(user.getId());
	StpUtil.getSession().set("user", user);
	return AjaxJson.getSuccess("登录成功").set("satoken", StpUtil.getTokenValue());  // 关键代码 
}
```

2、前端改造
- 1、在登录请求时，将返回的 token 保存到本地 `localStorage.setItem('satoken', res.satoken)`。
- 2、在后续每次请求中，读取本地保存的 satoken 塞到请求 header 中

``` js
const header = {};
if(localStorage.satoken) {
	header.satoken = localStorage.satoken;
}
// 后续提交请求...
```


<!------------- tab:Shiro ------------->

1、自定义 SessionManager，从请求 header 里读取前端提交的 token
``` java
public class MySessionManager extends DefaultWebSessionManager {

    private static final String TOKEN = "token";

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    public MySessionManager() {
        super();
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        String id = WebUtils.toHttp(request).getHeader(TOKEN);
        // 如果请求头中有 token 则其值为sessionId
        if (!StringUtils.isEmpty(id)) {
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        } else {
            //否则按默认规则从cookie取sessionId
            return super.getSessionId(request, response);
        }
    }
}
```

2、注入到 SecurityManager 中 
``` java
@Configuration
public class ShiroConfigure {

	// 省略其它次要代码 ... 

    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myRealm());
        manager.setSessionManager(sessionManager());
        return manager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager());
        return bean;
    }

    // 自定义sessionManager
    @Bean
    public SessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();
        return mySessionManager;
    }
}
```

3、测试 Controller，登录时将 token 信息返回到前端 
``` java
// 测试登录 
@RequestMapping("doLogin")
public AjaxJson doLogin(String username, String password) {
	Subject subject = SecurityUtils.getSubject();
	try {
		subject.login(new UsernamePasswordToken(username, password));
		String token = subject.getSession().getId().toString();    // 关键代码
		return AjaxJson.getSuccess("登录成功!").set("token", token);    // 关键代码
	} catch (AuthenticationException e) {
		e.printStackTrace();
		return AjaxJson.getError(e.getMessage());
	}
}
```

4、前端改造
- 1、在登录请求时，将返回的 token 保存到本地 `localStorage.setItem('token', res.token)`。
- 2、在后续每次请求中，读取本地保存的 token 塞到请求 header 中

``` js
const header = {};
if(localStorage.token) {
	header.token = localStorage.token;
}
// 后续提交请求...
```


<!------------- tab:SpringSecurity ------------->
见下方 “集成 Redis” 部分，同时做到：集成 Redis + 前后端分离。


<!------------- tab:JWT ------------->
`JWT` 不依赖 `Cookie` 保存/传输 token，因此无需特殊定制即可原生支持前后端分离模式。


<!---------------------------- tabs:end ------------------------------>



### 集成 Redis

<!------------------------------ tabs:start ------------------------------>
<!------------- tab:Sa-Token ------------->

pom.xml 引入依赖
``` xml
<!-- Sa-Token整合 Redis (使用jackson序列化方式) -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-redis-jackson</artifactId>
	<version>${sa-token.version}</version>
</dependency>

<!-- 提供Redis连接池 -->
<dependency>
	<groupId>org.apache.commons</groupId>
	<artifactId>commons-pool2</artifactId>
</dependency>
```

application.yml 新增连接配置 
``` yaml
spring:
    data:
        # redis配置
        redis:
            # Redis数据库索引（默认为0）
            database: 1
            # Redis服务器地址
            host: 127.0.0.1
            # Redis服务器连接端口
            port: 6379
            # Redis服务器连接密码（默认为空）
            password:
            # 连接超时时间
            timeout: 10s
            lettuce:
                pool:
                    # 连接池最大连接数
                    max-active: 200
                    # 连接池最大阻塞等待时间（使用负值表示没有限制）
                    max-wait: -1ms
                    # 连接池中的最大空闲连接
                    max-idle: 10
                    # 连接池中的最小空闲连接
                    min-idle: 0
```

其它代码照旧


<!------------- tab:Shiro ------------->
pom.xml 引入依赖 

``` xml
<!-- Shiro 集成 Redis -->
<dependency>
	<groupId>org.crazycake</groupId>
	<artifactId>shiro-redis</artifactId>
	<version>3.3.1</version>
</dependency>
```

application.yml 新增连接配置 
``` yaml

spring:
    redis:
        shiro:
            # Redis服务器地址
            host: 127.0.0.1:6379
            # Redis服务器连接密码（默认为空）
            password:
            # Redis数据库索引（默认为0）
            database: 2
            # 连接超时时间
            timeout: 1800
        
```


ShiroConfigure 注入相关 Bean 
``` java
@Configuration
public class ShiroConfigure {

    // 自定义 securityManager
    @Bean
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        // manager.setRealm(myRealm());

        // 自定义session管理 使用redis
        manager.setSessionManager(sessionManager());
        // 自定义缓存实现 使用redis
        manager.setCacheManager(cacheManager());

        return manager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean() {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager());
        return bean;
    }

    // -------- 以下为 shiro redis 相关 --------

    // Shiro redis 连接信息
    @Value("${spring.redis.shiro.host}")
    private String host;
    @Value("${spring.redis.shiro.database}")
    private int database;
    @Value("${spring.redis.shiro.timeout}")
    private int timeout;
    @Value("${spring.redis.shiro.password}")
    private String password;

    /**
     * 配置shiro redisManager
     */
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        if(StringUtils.hasText(password)){
            redisManager.setPassword(password);
        }
        redisManager.setDatabase(database);
        redisManager.setTimeout(timeout);
        return redisManager;
    }

    /**
     * cacheManager 缓存 redis 实现
     */
    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }

    /**
     * RedisSessionDAO redis 实现
     */
    @Bean
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    // 自定义sessionManager
    @Bean
    public SessionManager sessionManager() {
        MySessionManager mySessionManager = new MySessionManager();
        mySessionManager.setSessionDAO(redisSessionDAO());
        return mySessionManager;
    }

}
```

SysUser 实体类要实现 Serializable 接口
``` java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysUser implements Serializable {
	// ... 
}
```

其它代码照旧 


<!------------- tab:SpringSecurity ------------->

（结合上部分，同时做到集成 Redis + 前后端分离）

1、`pom.xml` 引入依赖 
``` xml
<!-- HttpSession 存储到 Redis -->
<dependency>
	<groupId>org.springframework.session</groupId>
	<artifactId>spring-session-data-redis</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

2、`yml` 增加配置
``` yml
spring:
    session:
        store-type: redis
        timeout: 8H
        redis:
            namespace: spring:session

    data:
        # redis配置
        redis:
            # Redis数据库索引（默认为0）
            database: 3
            # Redis服务器地址
            host: 127.0.0.1
            # Redis服务器连接端口
            port: 6379
            # Redis服务器连接密码（默认为空）
            password:
            # 连接超时时间
            timeout: 10s
            lettuce:
                pool:
                    # 连接池最大连接数
                    max-active: 200
                    # 连接池最大阻塞等待时间（使用负值表示没有限制）
                    max-wait: -1ms
                    # 连接池中的最大空闲连接
                    max-idle: 10
                    # 连接池中的最小空闲连接
                    min-idle: 0
```

3、在 `CustomAccessDeniedHandler` 自定义认证异常处理类中，返回 `json` 格式数据

``` java
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler, AuthenticationEntryPoint, Serializable {

    // 未登录异常
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        //验证为未登陆状态会进入此方法，认证错误
        response.setStatus(401);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = new ObjectMapper().writeValueAsString(AjaxJson.get(401, "请先进行登录"));
        printWriter.write(body);
        printWriter.flush();
    }

    // 权限不足
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 登陆状态下，权限不足执行该方法
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter printWriter = response.getWriter();
        String body = new ObjectMapper().writeValueAsString(AjaxJson.get(403, "权限不足"));
        printWriter.write(body);
        printWriter.flush();
    }
    
}
```

4、别忘了注入到 `SecurityFilterChain` 过滤器链 
``` java
 // 异常处理
httpSecurity.exceptionHandling(httpSecurityExceptionHandlingConfigurer -> {
	// 权限不足处理方案
	httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(accessDeniedHandler);
	// 未登录 处理逻辑
	httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(accessDeniedHandler);
});
```

5、在登录时，返回对应 token 信息
``` java
// 测试登录 
@RequestMapping("doLogin")
public AjaxJson doLogin(String username, String password, HttpServletRequest request) {
	try {
		// 验证账号密码
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
		usernamePasswordAuthenticationToken.setDetails(sysUserDao.findByUsername(username));
		Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
		// 存入上下文
		SecurityContextHolder.getContext().setAuthentication(authentication);
		request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
		// 返回
		String token = request.getSession().getId();
		return AjaxJson.getSuccess("登录成功!").set("token", token);
	} catch (Exception e) {
		e.printStackTrace();
		return AjaxJson.getError(e.getMessage());
	}
}
```

6、前端改造
- 1、在登录请求时，将返回的 token 保存到本地 `localStorage.setItem('token', res.token)`。
- 2、在后续每次请求中，读取本地保存的 token 塞到请求 header 中

``` js
const header = {};
if(localStorage.token) {
	header.token = localStorage.token;
}
// 后续提交请求...
```

7、新建 `HttpSessionConfigure` 配置重写 `HttpSessionId` 读取策略，改为从 `header` 头读取 `token` 参数作为 `SessionId`
``` java
@Configuration
public class HttpSessionConfigure {
    // HttpSession 读取策略，从 header 头读取 token 参数作为 session id
    @Bean
    public HeaderHttpSessionIdResolver httpSessionStrategy() {
        System.out.println("----------------- 自定义 HttpSession Id 读取方式");
        return new HeaderHttpSessionIdResolver("token");
    }
}
```


<!------------- tab:JWT ------------->
无

<!---------------------------- tabs:end ------------------------------>











