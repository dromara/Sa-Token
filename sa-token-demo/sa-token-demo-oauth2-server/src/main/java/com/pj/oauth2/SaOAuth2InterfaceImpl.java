package com.pj.oauth2;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.oauth2.logic.SaOAuth2Interface;

/**
 * 使用oauth2.0 所必须的一些自定义实现 
 * @author kong
 */
@Component
public class SaOAuth2InterfaceImpl implements SaOAuth2Interface {
	

	/*
	 * ------ 注意： 以下代码均为示例，真实环境需要根据数据库查询相关信息 
	 */

	// 返回此平台所有权限集合 
	@Override
	public List<String> getAppScopeList() {
		return Arrays.asList("userinfo");
	}

	// 返回指定Client签约的所有Scope集合 
	@Override
	public List<String> getClientScopeList(String clientId) {
		return Arrays.asList("userinfo");
	}

	// 获取指定 LoginId 对指定 Client 已经授权过的所有 Scope 
	@Override
	public List<String> getGrantScopeList(Object loginId, String clientId) {
		return Arrays.asList();
	}

	// 返回指定Client允许的回调域名, 多个用逗号隔开, *代表不限制 
	@Override
	public String getClientDomain(String clientId) {
		return "*";
	}

	// 返回指定ClientId的ClientSecret 
	@Override
	public String getClientSecret(String clientId) {
		return "aaaa-bbbb-cccc-dddd-eeee";
	}

	// 根据ClientId和LoginId返回openid 
	@Override
	public String getOpenid(String clientId, Object loginId) {
		return "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__";
	}

	// 根据ClientId和openid返回LoginId 
	@Override
	public Object getLoginId(String clientId, String openid) {
		return 10001;
	}

	

	/*
	 * 以上函数为开发时必须重写实现，其余函数可以按需重写 
	 */
	
	
	
}
