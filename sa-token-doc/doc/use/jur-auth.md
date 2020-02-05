# 权限验证
--- 


## 核心思想

- 所谓权限验证，验证的核心就是当前账号是否拥有一个权限码
- 有：就让你通过、没有：那么禁止访问
- 再往底了说，就是每个账号都会拥有一个权限码集合，我来验证这个集合中是否包括我需要检测的那个权限码
- 例如：当前账号拥有权限码集合：`[101, 102, "user-add", "user-get"]`，这时候我去验证权限码：`201`，则结果就是验证失败，禁止访问
- 所以现在问题的核心就是，1、如何获取一个账号所拥有的的权限码集合，2、本次操作要验证的权限码是哪个 

## 获取当前账号权限码集合
因为每个项目的需求不同，其权限设计也千变万化，所以【获取当前账号权限码集合】这一操作不可能内置到框架中，
所以`sa-token`将此操作以接口的方式暴露给你，以方便的你根据自己的业务逻辑进行重写

- 你需要做的就是新建一个类，重写`StpInterface`接口，例如以下代码：

``` java 
package com.pj.satoken;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import cn.dev33.satoken.stp.StpInterface;

/**
 *    自定义权限验证接口扩展 
 */
@Component	// 保证此类被springboot扫描，完成sa-token的自定义权限验证扩展 
public class StpCustom implements StpInterface {

	// 返回一个账号所拥有的权限码集合 
	@Override
	public List<Object> getPermissionCodeList(Object login_id, String login_key) {
		List<Object> list = new ArrayList<Object>();	// 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
		list.add("101");
		list.add("user-add");
		list.add("user-delete");
		list.add("user-update");
		list.add("user-get");
		list.add("article-get");
		return list;
	}

}
```


- 可参考代码：[码云：StpCustom.java](https://gitee.com/sz6/sa-token/blob/master/sa-token-demo-springboot/src/main/java/com/pj/satoken/StpCustom.java)



## 验证是否包含指定权限码 
然后就可以用以下api来鉴权了

#### StpUtil.hasPermission(Object pcode)
- 查询当前账号是否含有指定权限，返回true或false 

#### StpUtil.checkPermission(Object pcode)
- 检测当前账号是否含有指定权限，如果有则安全通过，如果没有则抛出异常：`NotPermissionException`

#### StpUtil.checkPermissionAnd(Object... pcode)
- 检测当前账号是否含有指定权限【指定多个，必须全都有，否则抛出异常】

#### StpUtil.checkPermissionOr(Object... pcode)
- 检测当前账号是否含有指定权限【指定多个，有一个就可以了，全都没有才会抛出异常】




## 拦截全局异常
- 有同学要问，鉴权失败，抛出异常，然后呢？要把异常显示给用户看吗？
- 当然不能把异常抛给用户看，你可以创建一个全局异常拦截器，统一返回给前端的格式，例如以下示例：
- 参考：[码云：TopController.java](https://gitee.com/sz6/sa-token/blob/master/sa-token-demo-springboot/src/main/java/com/pj/test/TopController.java)