# AOP注解鉴权
--- 

在 [注解式鉴权](/use/at-check) 章节，我们非常轻松的实现了注解鉴权，
但是默认的拦截器模式却有一个缺点，那就是无法在`Controller层`以外的代码使用进行校验

因此Sa-Token提供AOP插件，你只需在`pom.xml`里添加如下依赖，便可以在任意层级使用注解鉴权

``` xml 
<!-- sa-token整合SpringAOP实现注解鉴权 -->
<dependency>
	<groupId>cn.dev33</groupId>
	<artifactId>sa-token-spring-aop</artifactId>
	<version>1.19.0</version>
</dependency>
```


#### 注意点：
- 使用拦截器模式，只能把注解写在`Controller层`，使用AOP模式，可以将注解写在任意层级 <br>
- **拦截器模式和AOP模式不可同时集成**，否则会在`Controller层`发生一个注解校验两次的bug









