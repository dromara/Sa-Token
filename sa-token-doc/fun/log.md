# 参考：全局 Log 输出

--- 

### 打开全局日志输出

以下配置可以打开全局日志输出：

<!---------------------------- tabs:start ---------------------------->

<!------------- tab:yaml 风格  ------------->
``` yaml
sa-token: 
	# 是否输出操作日志 
	is-log: true
```

<!------------- tab:properties 风格  ------------->
``` properties
# 是否输出操作日志 
sa-token.is-log=true
```
<!---------------------------- tabs:end ---------------------------->


此配置项打开之后，框架将会在账号登录、注销、二级认证 等关键性步骤打印日志，以方便项目开发调试。

框架默认将日志信息打印到控制台，如果需要将日志输出到其它地方，你可以重写 SaLog 对象，例如以下代码将会把日志转接到 Slf4j 下：

``` java
/**
 * 将 Sa-Token log 信息转接到 Slf4j 
 */
@Component
public class SaLogForSlf4j implements SaLog {
	Logger log = LoggerFactory.getLogger(SaLogForSlf4j.class);
	
	@Override
	public void trace(String str, Object... args) {
		log.trace(str, args);
	}
	@Override
	public void debug(String str, Object... args) {
		log.debug(str, args);
	}
	@Override
	public void info(String str, Object... args) {
		log.info(str, args);
	}
	@Override
	public void warn(String str, Object... args) {
		log.warn(str, args);
	}
	@Override
	public void error(String str, Object... args) {
		log.error(str, args);
	}
	@Override
	public void fatal(String str, Object... args) {
		log.error(str, args);
	}
}
```

重新启动项目，观察日志打印变化。

### 增加API访问日志

手动增加 API 请求日志信息，这将非常有助于你调试代码，例如：

``` java
@Bean
public SaServletFilter getSaServletFilter() {
	return new SaServletFilter()
			.addInclude("/**")
			.addExclude("/favicon.ico")
			.setAuth(obj -> {
				// 输出 API 请求日志，方便调试代码 
				SaManager.getLog().debug("----- 请求path={}  提交token={}", SaHolder.getRequest().getRequestPath(), StpUtil.getTokenValue());
				// 其它校验代码... 
			})
			// 异常处理函数：每次认证函数发生异常时执行此函数 
			.setError(e -> {
				System.out.println("---------- sa全局异常 ");
				return SaResult.error(e.getMessage());
			})
			;
}
```