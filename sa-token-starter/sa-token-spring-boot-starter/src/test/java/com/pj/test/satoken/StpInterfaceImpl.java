package com.pj.test.satoken;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpInterface;

/**
 * 自定义权限验证接口扩展 
 * 
 * @author Auster
 *
 */
@Component
public class StpInterfaceImpl implements StpInterface {

	/**
	 * 返回一个账号所拥有的权限码集合 
	 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginKey) {
		return Arrays.asList("user*", "art-add", "art-delete", "art-update", "art-get");
	}

	/**
	 * 返回一个账号所拥有的角色标识集合 
	 */
	@Override
	public List<String> getRoleList(Object loginId, String loginKey) {
		return Arrays.asList("admin", "super-admin");
	}

}
