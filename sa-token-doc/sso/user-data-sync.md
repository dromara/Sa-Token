# 用户数据同步 / 迁移

本篇文章仅提供架构设计的略微参考，真实场景中每个公司的架构设计都是千差万别的，一套设计理论未必能够适应所有公司的项目。
所以如果你觉着本篇文章的设计理念不能契合你公司的需求，请以你公司的原设计为准。

--- 

### 数据同步需求

在前面的不同架构 SSO 对接示例中，我们均假设了一个前提：

-- _所有的 sso-client 只负责业务操作，不存储 user 数据，user 数据全部来源于 sso-server，包括登录认证也都是基于 sso-server 里的 user 账号进行校验操作。_

这种架构比较简洁、清晰，是一种理想化的 SSO 架构模型。

然而更多时候，我们遇到的实际情况是：

-- _公司已经有了 N 多个系统，每个系统都有自己独立的一套账号认证体系，现在老板要让这 N 个毫无关系的系统集成单点登录。_

要完成这种需求，首先你得考虑两个问题：
1. 问题一：sso-client 需不需要保留 user 数据。
	- sso-client 不涉及 user 信息连表查的业务，就可以不保留 user 信息。
	- sso-client 涉及 user 信息连表查业务，就需要在 sso-client 保留 user 数据。
2. 问题二：如果保留的话，是和 sso-server 强同步，还是弱同步。
	- 强同步就是指 sso-client 的 user 数据和 sso-server 的 user 数据，字段值必须保持一致。比如说：一个用户在server端昵称修改为“张三”，那么在 client 端也要实时同步修改。
	- 弱同步就是指两边可以各改各的。比如说：一个用户 server 端修改了昵称为 “张三”，他在 client 端依然可以昵称为 “李四”。


由此可大致分为三种设计方案：

| 方案序号	| 方案名称	| 简单说明															|  适用系统									|
| :--------	| :--------	| :--------															| :--------									|
| 方案一	 	| 统一迁移	| 统一把用户数据迁移到 sso-server 认证中心再进行对接					| 比较简单的系统，业务上不需要 user 信息连表查	|
| 方案二	 	| 实时同步	| 按照一定的规则，使 sso-client 和 sso-server 保持 user 信息实时同步 	| 一般业务上需要 user 信息连表查的系统			|
| 方案三	 	| 字段关联	| 不同步，但找一个关键字段，将 sso-client 和 sso-server 的 user 账号进行关联起来	| sso-client 不打算过分依赖 sso-server 的 user 数据，只是想借助 sso-server 完成一下统一登录		|

下面逐一拆解三种方案具体实现。


### 1、方案一：统一迁移

对接工作开发前，sso-client 的 user 数据完全迁移到 sso-server 中，且自身不再保留 user 数据，只进行业务数据处理操作。

这种方案其实不必过多讲解，因为数据完成迁移后整个架构就转化为了上述的“理想化SSO模型”，后续对接也比较方便。
迁移方式可以选择数据库同步工具，或者手写代码从 sso-client 库读取数据然后 insert 到 sso-server 库中。
这并非此文探讨的重点，因此不再过多赘述了。

- 方案优点：架构简洁明了，SSO 登录、注销对接起来非常方便
- 方案缺点：sso-client 不存储 user 信息，因此业务上需要连表查询 user 信息的地方会比较麻烦（例如：拉取帖子列表时需要附加显示用户头像和昵称信息）

方案适用范围：适合业务比较简单，不涉及 user资料连表查业务 的子系统。


### 2、方案二：实时同步

首先，对接前，数据还是要迁移的，只不过迁移后 sso-client 的 user 数据不删除掉，依然保留。

然后在项目运行阶段，每当 sso-server 的 user 数据发生变动时（增删改），逐一向每个 sso-client 推送变化信息。使 sso-client 与 sso-server 的 user 数据保持强同步。

你可能会有疑问，那 sso-client 的 user 数据发生变动时，要不要向 sso-server 推送信息，我的建议是：尽量不要让 sso-client 的 user 信息主动发生变化。

举个例子：

> 公司有电商、论坛、短视频 3 个子系统 + 1 个 sso-server 认证中心，无论用户从哪个子系统点击 “修改我的资料” 按钮时，都应该统一跳转到 sso-server 认证中心进行修改，
> 修改完毕后再由 sso-server 将 user 信息推送至 3 个子系统。以此来保证 4 个系统间的 user 信息同步。

- 方案优点：sso-client 存储了 user 信息，可以比较方便的进行 user 连表查操作。
- 方案缺点：sso-server 与 sso-client 的 user 数据同步功能不算简单，开发起来可能要耗费一段不小的工期。

方案适用范围：一般业务上需要 user 信息连表查的子系统都适合。


### 3、方案三：字段关联

如果子系统不需要和 sso-server 做到信息强同步，可以使用字段关联法做到账户关联进行登录。

举个例子：公司有三个子系统，电商、论坛、短视频。同一个用户可以在这三个子系统以及 sso-server 认证中心拥有不同的昵称、头像等信息，互不干扰。

例如，在 sso-server 认证中心里，张三的数据库信息为：

| id		| username	| avatar 	| password 		| age 			| email				|
| :--------	| :--------	| :--------	| :--------		| :--------		| :--------			|
| 10001		| ...		| ...		| ...			| ...			| ...				|
| 10002		| 小明		| cat.jpg	| 123456		| 18 			| `23397@xx.com`		|
| 10003		| ...		| ...		| ...			| ...			| ...				|

在电商系统里中，张三的数据库信息为：

| id			| name		| avatar 		| money			| email				|
| :--------		| :--------	| :--------		| :--------		| :--------			|
| 100334		| ...		| ...			| ...			| ...				|
| 100335		| 二明		| dog.jpg		| 1000 			| `23397@xx.com`		|
| 100336		| ...		| ...			| ...			| ...				|

这里的关键点在于，虽然用户 “张三” 在每个系统里的资料都是不同的，但是程序要想办法将它们识别为同一个用户，
要做到这一点，就需要我们准备一个关键字段将信息打通串联起来。例如表中的 “邮箱” 信息可以作为这个“关联字段”。

（注：此处仅展示使用邮箱作为关联字段的操作，实际上除了邮箱以外，手机号、身份证号等具有唯一性的信息都可以作为关联字段）

首先，在 sso-server 端，我们需要重写一下 `checkTicketAppendData` 函数，使其在 “校验 ticket 返回 loginId” 时，追加返回 email 字段。

```  java 
// 配置SSO相关参数 
@Autowired
private void configSso(SaSsoServerConfig ssoServer) {
	
	// 其它配置 ...
	
	// 配置：Ticket校验函数
	ssoServer.checkTicketAppendData = (loginId, result) -> {
		System.out.println("-------- 追加返回信息到 sso-client --------");

		// 在校验 ticket 后，给 sso-client 端追加返回信息的函数
		SysUser user = sysUserMapper.getById(loginId);
		result.set("email", user.getEmail());
		// result.set("user", user);  // 你也可以将整个user 对象的信息都返回到 sso-client，自由决定

		return result;
	};
	
}
```

在 sso-client 端，重写 ticketResultHandle 函数，根据 sso-server 返回的信息查询本地 user 信息并登录：

``` java
// 配置SSO相关参数 
@Autowired
private void configSso(SaSsoClientConfig ssoClient) {

	// 其它配置 ... 

	// 自定义校验 ticket 返回值的处理逻辑 （每次从认证中心获取校验 ticket 的结果后调用）
	ssoClient.ticketResultHandle = (ctr, back) -> {
		System.out.println("--------- 自定义 ticket 校验结果处理函数 ---------");
		System.out.println("此账号在 sso-server 的 userId：" + ctr.loginId);
		System.out.println("此账号在 sso-server 会话剩余有效期：" + ctr.remainSessionTimeout + " 秒");
		System.out.println("此账号返回的 email 信息：" + ctr.result.get("email"));

		// 模拟代码：
		// 根据 email 字段找到此账号在本系统对应的 user 信息
		String email = (String) ctr.result.get("email");
		SysUser user = sysUserMapper.getByEmail(email);

		// 如果找不到，说明是首次登录本系统的新用户，需要自动注册一个新账号给他
		if(user == null) {
			// 涉及到数据库操作，此处仅做模拟代码
			// 1、构建 user 信息
			// 2、插入到数据库
			// 3、查询出最新刚插入的这条 user 信息
			user = sysUserMapper.getByEmail(email);
		}

		// 进行登录
		StpUtil.login(user.getId(), ctr.remainSessionTimeout);
		StpUtil.getSession().set("user", user);

		// 一切工作完毕，重定向回 back 页面
		return SaHolder.getResponse().redirect(back);
	};

}
```

至此完毕。

- 方案优点：
	- 1、sso-client 不需要和 sso-server 保持信息强同步，实现起来不复杂，架构也比较清晰易维护。
	- 2、同一个用户的信息，sso-client 可以和 sso-client 保持不同，各自维护各自的，互不干扰。
- 方案缺点：好像没啥缺点，除非你觉着上述的第2条优点属于缺点。

方案适用范围：在 user 信息方面不打算过分依赖 sso-server 的系统，希望自己维护自己的 user 信息，只是想借助 sso-server 完成一下统一登录。


### 4、扩展：没有关联字段

如果我们的子系统 user 表没有邮箱、手机号等唯一性字段和 sso-server 的 user 表进行关联，该怎么办呢？

没有字段，那就创造个字段，例如：

| id			| name		| avatar 		| age			| center_id			|
| :--------		| :--------	| :--------		| :--------		| :--------			|
| 205421		| ...		| ...			| ...			| ...				|
| 205422		| 小风筝		| dog.jpg		| 21 			| 10002				|
| 205423		| ...		| ...			| ...			| ...				|

如上表所示，我们可以在子系统的 user 表新增一列 `center_id`，记录这个用户在认证中心所属的账号id。然后在登录时根据这个 `center_id` 来查找相应的用户。 

由于 sso-server 端默认就是会返回 loginId 参数的，因此在 sso-server 端不必再重写一下 `checkTicketAppendData` 函数来追加返回信息了，
我们只需要重写 sso-client 端的 `ticketResultHandle` 函数即可：

``` java
// 配置SSO相关参数 
@Autowired
private void configSso(SaSsoClientConfig ssoClient) {

	// 其它配置 ... 

	// 自定义校验 ticket 返回值的处理逻辑 （每次从认证中心获取校验 ticket 的结果后调用）
	ssoClient.ticketResultHandle = (ctr, back) -> {
		System.out.println("--------- 自定义 ticket 校验结果处理函数 ---------");
		System.out.println("此账号在 sso-server 的 userId：" + ctr.loginId);
		System.out.println("此账号在 sso-server 会话剩余有效期：" + ctr.remainSessionTimeout + " 秒");

		// 模拟代码：
		// 根据 center_id 字段找到此账号在本系统对应的 user 信息
		long centerId = SaFoxUtil.getValueByType(ctr.loginId, long.class);
		SysUser user = sysUserMapper.getByCenterId(centerId);

		// 如果找不到，说明是首次登录本系统的新用户，需要自动注册一个新账号给他
		if(user == null) {
			// 涉及到数据库操作，此处仅做模拟
			// 1、构建 user 信息
			// 2、插入到数据库
			// 3、查询出最新刚插入的这条 user 信息
			user = sysUserMapper.getByCenterId(userId);
		}

		// 进行登录
		//     注意此处需要使用 centerId 进行登录，否则该账号将无法正常完成单点注销功能 
		StpUtil.login(centerId, ctr.remainSessionTimeout);
		StpUtil.getSession().set("user", user);

		// 一切工作完毕，重定向回 back 页面
		return SaHolder.getResponse().redirect(back);
	};

}
```

至此完毕。


> [!INFO| label:提问：按照方案三，一个用户登录过程中，sso-server 和 sso-client 对这个用户账号的完整处理步骤是怎样的？] 
> 1. 用户进入 sso-client 登录页面，点击上面的 [ 使用 xx 认证中心快捷登录 ] 按钮，浏览器跳转至 sso-server 认证中心。
> 2. 如果用户在 sso-server 有账号，则直接登录，如果没有，则注册账号并登录。
> 3. sso-server 重定向回 sso-client 端，并携带 ticket 参数。
> 4. sso-client 获取 ticket 参数，并解析出 center_id 值。
> 5. 根据 center_id 从 user 表查数据：
> 	- 5.1 查的到，证明有账号，直接登录。
> 	- 5.2 查不到，证明无账号，程序自动给他添加一条 user 账号，并登录。
> 6. 登录完成。
