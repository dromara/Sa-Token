package cn.dev33.satoken.action;

import java.util.UUID;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.util.SaTokenInsideUtil;

/**
 * 对 SaTokenAction 接口的默认实现 
 * @author kong
 *
 */
public class SaTokenActionDefaultImpl implements SaTokenAction {

	
	/**
	 * 生成一个token 
	 */
	@Override
	public String createToken(Object loginId, String loginKey) {
		// 根据配置的tokenStyle生成不同风格的token 
		String tokenStyle = SaTokenManager.getConfig().getTokenStyle();
		// uuid 
		if("uuid".equals(tokenStyle)) {
			return UUID.randomUUID().toString();
		}
		// 简单uuid (不带下划线)
		 if("simple-uuid".equals(tokenStyle)) {
			return UUID.randomUUID().toString().replaceAll("-", "");
		}
		// 32位随机字符串
		 if("random-32".equals(tokenStyle)) {
			return SaTokenInsideUtil.getRandomString(32);
		}
		// 64位随机字符串
		 if("random-64".equals(tokenStyle)) {
			return SaTokenInsideUtil.getRandomString(64);
		}
		// 128位随机字符串
		 if("random-128".equals(tokenStyle)) {
			return SaTokenInsideUtil.getRandomString(128);
		}
		// tik风格 (2_14_16)
		 if("tik".equals(tokenStyle)) {
			return SaTokenInsideUtil.getRandomString(2) + "_" + SaTokenInsideUtil.getRandomString(14) + "_" + SaTokenInsideUtil.getRandomString(16) + "__";
		}
		// 默认，还是uuid
		return UUID.randomUUID().toString();
	}
	
}
