# 花式token

本篇介绍token生成的各种风格，以及自定义token生成策略

--- 


## 内置风格

- sa-token默认的token生成策略是uuid风格, 其模样类似于：`623368f0-ae5e-4475-a53f-93e4225f16ae`<br>
- 如果你对这种风格不太感冒，还可以将token生成设置为其他风格，
- 怎么设置呢？只需要在yml配置文件里设置 `spring.sa-token.token-style=风格类型` 即可，其有多种取值： <br>


1. token-style=`uuid`，uuid风格 **(默认风格)**
``` html
	623368f0-ae5e-4475-a53f-93e4225f16ae
```

2. token-style=`simple-uuid`，同上，uuid风格, 只不过去掉了中划线：
``` html
	6fd4221395024b5f87edd34bc3258ee8
```

3. token-style=`random-32`，随机32位字符串：
``` html
	qEjyPsEA1Bkc9dr8YP6okFr5umCZNR6W
```

4. token-style=`random-64`，随机64位字符串：
``` html
	v4ueNLEpPwMtmOPMBtOOeIQsvP8z9gkMgIVibTUVjkrNrlfra5CGwQkViDjO8jcc
```

5. token-style=`random-128`，随机128位字符串：
``` html
	nojYPmcEtrFEaN0Otpssa8I8jpk8FO53UcMZkCP9qyoHaDbKS6dxoRPky9c6QlftQ0pdzxRGXsKZmUSrPeZBOD6kJFfmfgiRyUmYWcj4WU4SSP2ilakWN1HYnIuX0Olj
```

6. token-style=`tik`，tik风格：
``` html
	gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__
```


## 自定义token生成策略
- 如果你觉着以上风格都不是你喜欢的类型，那么你还可以**自定义token生成策略**，来定制化token生成风格 <br>
- 怎么做呢？只需要重写`SaTokenAction`接口的`createToken`方法即可


#### 参考步骤如下：
1. 新建文件`MySaTokenAction.java`，继承`SaTokenActionDefaultImpl`默认实现类, 并添加上注解`@Component`，保证此类被`springboot`扫描到
``` java 
	package com.pj.satoken;

	import org.springframework.stereotype.Component;
	import cn.dev33.satoken.action.SaTokenActionDefaultImpl;

	/**
	 * 继承sa-token行为Bean默认实现, 重写部分逻辑 
	 */
	@Component
	public class MySaTokenAction extends SaTokenActionDefaultImpl {
		// 重写token生成策略 
		@Override
		public String createToken(Object loginId, String loginKey) {
			return SaTokenInsideUtil.getRandomString(60);	// 随机60位字符串
		}
	}
```

2. 再次调用 `StpUtil.setLoginId(10001)`方法进行登录，观察其生成的token样式
``` html
	gfuPSwZsnUhwgz08GTCH4wOgasWtc3odP4HLwXJ7NDGOximTvT4OlW19zeLH
```



## 以雪花算法生成token
在此再举一个例子，以`自定义token生成策略`的方式集成`雪花算法`来生成token

1. 首先我们需要找一个合适的类库，帮助我们生成雪花算法唯一id，在此推荐 [Hutool](https://hutool.cn/docs/#/) ，在`pom.xml`里添加依赖：
``` xml
	<dependency>
	    <groupId>cn.hutool</groupId>
	    <artifactId>hutool-all</artifactId>
	    <version>5.5.4</version>
	</dependency>
```

2. 同上，我们需要新建文件`MySaTokenAction.java`，继承`SaTokenActionDefaultImpl`默认实现类, 并添加上注解`@Component`，保证此类被`springboot`扫描到
``` java 
	package com.pj.satoken;

	import org.springframework.stereotype.Component;
	import cn.dev33.satoken.action.SaTokenActionDefaultImpl;
	import cn.hutool.core.util.IdUtil;

	/**
	 * 继承sa-token行为Bean默认实现, 重写部分逻辑 
	 */
	@Component
	public class MySaTokenAction extends SaTokenActionDefaultImpl {
		// 重写token生成策略 
		@Override
		public String createToken(Object loginId, String loginKey) {
			return IdUtil.getSnowflake(1, 1).nextIdStr();	// 以雪花算法生成token 
		}
	}
```

3. 再次调用 `StpUtil.setLoginId(10001)`方法进行登录，观察其生成的token样式: 
``` html
	1339604338175250432
```