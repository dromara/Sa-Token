# 延迟初始化兼容

Sa-Token 已兼容 Spring Boot 的延迟初始化特性（`spring.main.lazy-initialization=true`），开启该特性后无需额外配置即可正常工作。


### 一、开启延迟初始化

在 `application.yml` 中开启 Spring Boot 的延迟初始化：

``` yaml
spring:
  main:
    lazy-initialization: true
```

开启后，Spring 容器中的 Bean 默认都会延迟到首次使用时才创建。


### 二、Sa-Token 的处理机制

Sa-Token 的部分内部 Bean（如全局过滤器、上下文持有者、防火墙、配置加载器等）需要在应用启动时立即初始化，否则可能导致拦截链失效、上下文获取异常等问题。

为此，Sa-Token 在自动装配阶段通过注册 `LazyInitializationExcludeFilter`，将这些必须立即初始化的内部 Bean 自动排除在延迟初始化之外，使用者无需手动处理。


### 三、自定义 Bean 的处理

如果你自定义了 Sa-Token 相关 Bean（例如 `StpInterface`、`SaTokenListener`、`SaFirewallCheckHandler` 等），并希望它们也在启动时立即初始化，可参考以下两种方式之一：

#### 方式一：使用 `@Lazy(false)` 注解

``` java
@Component
@Lazy(false)
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // ...
        return new ArrayList<>();
    }
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // ...
        return new ArrayList<>();
    }
}
```

#### 方式二：注册 `LazyInitializationExcludeFilter`

``` java
@Configuration
public class MyLazyInitConfig {
    @Bean
    static LazyInitializationExcludeFilter customStpInterfaceExcludeFilter() {
        return LazyInitializationExcludeFilter.forBeanTypes(StpInterface.class);
    }
}
```


### 四、注意事项

- Sa-Token 内部关键 Bean 已自动排除，无需用户介入。
- 仅当自定义 Bean 需要在启动阶段就立即生效时（例如启动时即注册监听器），才建议使用上述方式开启即时初始化。
- 未做特殊处理的自定义 Bean 仍遵循 Spring 的延迟初始化策略。
