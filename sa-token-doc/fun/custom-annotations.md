# 自定义注解

如果框架内置的注解无法满足你的业务需求，你还可以自定义注解注入到框架中。

---

### 1、自定义注解

假设有以下业务需求

> [!INFO| label:需求场景] 
> 自定义一个注解 `@CheckAccount`，具有 `name`、`pwd` 两个字段，在标注一个方法上时，要求前端必须提交相应的账号密码参数才能访问方法。


#### 1.1、第一步，创建一个注解

``` java
/**
 * 账号校验：在标注一个方法上时，要求前端必须提交相应的账号密码参数才能访问方法。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface CheckAccount {

    /**
     * 需要校验的账号
     */
    String name();

    /**
     * 需要校验的密码
     */
    String pwd();

}
```

#### 1.2、第二步，创建注解处理器 

实现 `SaAnnotationAbstractHandler` 接口，指定泛型为刚才自定义的注解 

``` java
/**
 * 注解 CheckAccount 的处理器
 */
@Component
public class CheckAccountHandler implements SaAnnotationAbstractHandler<CheckAccount> {

    // 指定这个处理器要处理哪个注解
    @Override
    public Class<CheckAccount> getHandlerAnnotationClass() {
        return CheckAccount.class;
    }

    // 每次请求校验注解时，会执行的方法
    @Override
    public void checkMethod(CheckAccount at, AnnotatedElement element) {
        // 获取前端请求提交的参数
        String name = SaHolder.getRequest().getParamNotNull("name");
        String pwd = SaHolder.getRequest().getParamNotNull("pwd");

        // 与注解中指定的值相比较
        if(name.equals(at.name()) && pwd.equals(at.pwd()) ) {
            // 校验通过，什么也不做
        } else {
            // 校验不通过，则抛出异常
            throw new SaTokenException("账号或密码错误，未通过校验");
        }
    }

}
```

参考上述代码，实现类上指定了 `@Component` 注解，使其可以在 ioc 环境下（如 Spring）被自动扫描注册 Sa-Token 中，
如果你的项目属于非 ioc 环境，则需要手动将其注册到 Sa-Token 框架中：
``` java
SaAnnotationStrategy.instance.registerAnnotationHandler(new CheckAccountHandler());
```

#### 1.3、测试自定义的注解

我们在一个请求接口上指定这个注解，来测试一下效果 

``` java
@RestController
@RequestMapping("/test/")
public class TestController {

	@RequestMapping("test")
	@CheckAccount(name = "sa", pwd = "123456")
	public SaResult test() {
		System.out.println("------------进来了");
		return SaResult.ok(); 
	}
	
}
```

启动项目，使用浏览器访问此接口。

先来个错误的账号密码访问测试一下：[http://localhost:8081/test/test?name=sa&pwd=123](http://localhost:8081/test/test?name=sa&pwd=123)

返回结果：

``` js
{
  "code": 500,
  "msg": "账号或密码错误，未通过校验",
  "data": null
}
```

使用正确账号密码测试访问：[http://localhost:8081/test/test?name=sa&pwd=123456](http://localhost:8081/test/test?name=sa&pwd=123456)

返回结果：

``` js
{
  "code": 200,
  "msg": "ok",
  "data": null
}
```



### 2、使用自定义注解优化多账号鉴权

在之前的 [ 多账号鉴权 ] 章节，我们介绍了利用 “spring 注解处理器” 达到注解合并的目的，从而简化多账号体系下的注解鉴权写法。

此种方案比较简单，但是也有一些缺点。
- 1、强依赖 Spring，无法在非 Spring 环境中使用。
- 2、注解递归检查可能会造成一些性能下降。
- 3、扩展性较低，只能略微简化框架内置好的注解写法，无法灵活扩展功能。

此处我们再演示一种方案，使用自定义注解的方式达到相同的目的。


#### 2.1、首先定义注解

``` java
/**
 * 登录认证(User版)：只有登录之后才能进入该方法 
 * <p> 可标注在函数、类上（效果等同于标注在此类的所有方法上） 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE})
public @interface SaUserCheckLogin {
	
}
```

#### 2.2、定义注解处理器
``` java
/**
 * 注解 SaUserCheckLogin 的处理器
 */
@Component
public class SaUserCheckLoginHandler implements SaAnnotationAbstractHandler<SaUserCheckLogin> {

    @Override
    public Class<SaUserCheckLogin> getHandlerAnnotationClass() {
        return SaUserCheckLogin.class;
    }

    @Override
    public void checkMethod(SaUserCheckLogin at, AnnotatedElement element) {
        SaCheckLoginHandler._checkMethod(StpUserUtil.TYPE);
    }

}
```

#### 2.3、使用新注解
接下来就可以使用我们的自定义注解了：

``` java
// 使用 @SaUserCheckLogin 的效果等同于使用：@SaCheckLogin(type = "user")
@SaUserCheckLogin
@RequestMapping("info")
public String info() {
    return "查询用户信息";
}
```

注：其它注解 `@SaCheckRole("xxx")`、`@SaCheckPermission("xxx")` 同理， 完整示例参考 Gitee 代码：
[自定义注解](https://gitee.com/dromara/sa-token/tree/master/sa-token-demo/sa-token-demo-case/src/main/java/com/pj/satoken/custom_annotation)。












