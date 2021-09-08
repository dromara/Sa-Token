# 参考：将权限数据放在缓存里
前面我们讲解了如何通过`StpInterface`接口注入权限数据，框架默认是不提供缓存能力的，如果你想减小数据库的访问压力，则需要将权限数据放到缓存中

--- 

参考示例：
``` java
/**
 * 返回一个账号所拥有的权限码集合 
 */
@Override
public List<String> getPermissionList(Object loginId, String loginType) {
	
	// 1. 获取这个账号所属角色id 
	long roleId = StpUtil.getSessionByLoginId(loginId).get("Role_Id", () -> {
		return ...;	 // 从数据库查询这个账号所属的角色id 
	});
	
	// 2. 获取这个角色id拥有的权限列表  
	SaSession roleSession = SaSessionCustomUtil.getSessionById("role-" + roleId);
	List<String> list = roleSession.get("Permission_List", () -> {
		return ...;  // 从数据库查询这个角色id拥有的权限列表 
	});
	
	// 3. 返回
	return list;
}
```

##### 疑问：为什么不直接缓存 `[账号id->权限列表]`的关系，而是 `[账号id -> 角色id -> 权限列表]`？

<!-- ``` java
// 在一个账号登录时写入其权限数据
RedisUtil.setValue("账号id", <权限列表>);

// 然后在`StpInterface`接口中，如下方式获取
List<String> list = RedisUtil.getValue("账号id");
``` -->

答：`[账号id->权限列表]`的缓存方式虽然更加直接粗暴，却有一个严重的问题：

- 通常我们系统的权限架构是RBAC模型：权限与用户没有直接的关系，而是：用户拥有指定的角色，角色再拥有指定的权限
- 而这种'拥有关系'是动态的，是可以随时修改的，一旦我们修改了它们的对应关系，便要同步修改或清除对应的缓存数据 

现在假设如下业务场景：我们系统中有十万个账号属于同一个角色，当我们变动这个角色的权限时，难道我们要同时清除这十万个账号的缓存信息吗？
这显然是一个不合理的操作，同一时间缓存大量清除容易引起Redis的缓存雪崩

而当我们采用 `[账号id -> 角色id -> 权限列表]` 的缓存模型时，则只需要清除或修改 `[角色id -> 权限列表]` 一条缓存即可 

一言以蔽之：权限的缓存模型需要跟着权限模型走，角色缓存亦然 


