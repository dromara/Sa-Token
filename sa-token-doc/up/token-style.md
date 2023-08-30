# 自定义 Token 风格

本篇介绍token生成的各种风格，以及自定义token生成策略。

--- 


## 内置风格

Sa-Token 默认的 token 生成策略是 uuid 风格，其模样类似于：`623368f0-ae5e-4475-a53f-93e4225f16ae`。<br>
如果你对这种风格不太感冒，还可以将 token 生成设置为其他风格。

怎么设置呢？只需要在yml配置文件里设置 `sa-token.token-style=风格类型` 即可，其有多种取值： 

``` java
// 1. token-style=uuid    —— uuid风格 (默认风格)
"623368f0-ae5e-4475-a53f-93e4225f16ae"

// 2. token-style=simple-uuid    —— 同上，uuid风格, 只不过去掉了中划线
"6fd4221395024b5f87edd34bc3258ee8"

// 3. token-style=random-32    —— 随机32位字符串
"qEjyPsEA1Bkc9dr8YP6okFr5umCZNR6W"

// 4. token-style=random-64    —— 随机64位字符串
"v4ueNLEpPwMtmOPMBtOOeIQsvP8z9gkMgIVibTUVjkrNrlfra5CGwQkViDjO8jcc"

// 5. token-style=random-128    —— 随机128位字符串
"nojYPmcEtrFEaN0Otpssa8I8jpk8FO53UcMZkCP9qyoHaDbKS6dxoRPky9c6QlftQ0pdzxRGXsKZmUSrPeZBOD6kJFfmfgiRyUmYWcj4WU4SSP2ilakWN1HYnIuX0Olj"

// 6. token-style=tik    —— tik风格
"gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__"
```


## 自定义 Token 生成策略

如果你觉着以上风格都不是你喜欢的类型，那么你还可以**自定义token生成策略**，来定制化token生成风格。 <br>

怎么做呢？只需要重写 `SaStrategy` 策略类的 `createToken` 算法即可：


#### 参考步骤如下：
1、在`SaTokenConfigure`配置类中添加代码：
``` java 
@Configuration
public class SaTokenConfigure {
    /**
     * 重写 Sa-Token 框架内部算法策略 
     */
    @Autowired
    public void rewriteSaStrategy() {
    	// 重写 Token 生成策略 
    	SaStrategy.instance.createToken = (loginId, loginType) -> {
    		return SaFoxUtil.getRandomString(60);	// 随机60位长度字符串
    	};
    }
}
```

2、再次调用 `StpUtil.login(10001)`方法进行登录，观察其生成的token样式:
``` java
gfuPSwZsnUhwgz08GTCH4wOgasWtc3odP4HLwXJ7NDGOximTvT4OlW19zeLH
```

!> **更改了 token 生成策略但是不生效？**<br> 把 Redis 中的旧数据清除掉再试试

