package com.pj.test.satoken;

import java.util.Arrays;
import java.util.List;

import cn.dev33.satoken.stp.StpInterface;

import java.util.Arrays;

/**
 * 自定义权限验证接口扩展（JWT 测试专用，勿注册为 Spring Bean，避免污染其它集成测试上下文）
 * 
 * @author Auster
 *
 */
public class StpInterfaceImpl implements StpInterface {

	/**
	 * 返回一个账号所拥有的权限码集合 
	 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		return Arrays.asList("user*", "art-add", "art-delete", "art-update", "art-get");
	}

	/**
	 * 返回一个账号所拥有的角色标识集合 
	 */
	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		return Arrays.asList("admin", "super-admin");
	}

}
