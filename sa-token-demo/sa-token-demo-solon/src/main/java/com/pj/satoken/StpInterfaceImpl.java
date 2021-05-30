package com.pj.satoken;

import java.util.ArrayList;
import java.util.List;


import cn.dev33.satoken.stp.StpInterface;
import org.noear.solon.annotation.Component;

/**
 * 自定义权限验证接口扩展 
 */
@Component    // 打开此注解，保证此类被springboot扫描，即可完成sa-token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

	/**
	 * 返回一个账号所拥有的权限码集合 
	 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginKey) {
		// 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
		List<String> list = new ArrayList<String>();	
		list.add("101");
		list.add("user-add");
		list.add("user-delete");
		list.add("user-update");
		list.add("user-get");
		list.add("article-get");
		return list;
	}

	/**
	 * 返回一个账号所拥有的角色标识集合 
	 */
	@Override
	public List<String> getRoleList(Object loginId, String loginKey) {
		// 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
		List<String> list = new ArrayList<String>();	
		list.add("admin");
		list.add("super-admin");
		return list;
	}

}
