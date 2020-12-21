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
		// 生成各种花式token 
		String tokenStyle = SaTokenManager.getConfig().getTokenStyle();
		// uuid
		if(tokenStyle.equals("uuid")) {
			return UUID.randomUUID().toString();
		}
		// 简单uuid (不带下划线)
		else if(tokenStyle.equals("simple-uuid")) {
			return UUID.randomUUID().toString().replaceAll("-", "");
		}
		// 32位随机字符串
		else if(tokenStyle.equals("random-32")) {
			return SaTokenInsideUtil.getRandomString(32);
		}
		// 64位随机字符串
		else if(tokenStyle.equals("random-64")) {
			return SaTokenInsideUtil.getRandomString(64);
		}
		// 128位随机字符串
		else if(tokenStyle.equals("random-128")) {
			return SaTokenInsideUtil.getRandomString(128);
		}
		// tik风格 
		else if(tokenStyle.equals("tik")) {
			return SaTokenInsideUtil.getRandomString(2) + "_" + SaTokenInsideUtil.getRandomString(14) + "_" + SaTokenInsideUtil.getRandomString(16) + "__";
		}
		// 默认 
		else {
			return UUID.randomUUID().toString();
		}
	}


	
	
	
}
