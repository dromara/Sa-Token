package com.pj.satoken;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.action.SaTokenActionDefaultImpl;

/**
 * 继承sa-token行为Bean默认实现, 重写部分逻辑 
 * @author kong
 *
 */
@Component
public class MySaTokenAction extends SaTokenActionDefaultImpl {
	
	// 重写token生成策略 
//	@Override
//	public String createToken(Object loginId, String loginKey) {
//		return SaTokenInsideUtil.getRandomString(60);
//	}

}
