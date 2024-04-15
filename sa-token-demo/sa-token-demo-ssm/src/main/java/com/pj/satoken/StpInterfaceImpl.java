package com.pj.satoken;

import cn.dev33.satoken.stp.StpInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展 
 */
public class StpInterfaceImpl implements StpInterface {

	/**
	 * 返回一个账号所拥有的权限码集合 
	 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
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
	public List<String> getRoleList(Object loginId, String loginType) {
		// 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
		List<String> list = new ArrayList<String>();	
		list.add("admin");
		list.add("super-admin");
		return list;
	}

}
