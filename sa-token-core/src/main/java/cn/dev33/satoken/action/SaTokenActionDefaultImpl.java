package cn.dev33.satoken.action;

import java.util.List;
import java.util.UUID;

import cn.dev33.satoken.SaTokenManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 对 SaTokenAction 接口的默认实现 
 * @author kong
 *
 */
public class SaTokenActionDefaultImpl implements SaTokenAction {

	/**
	 * 根据一定的算法生成一个token 
	 */
	@Override
	public String createToken(Object loginId, String loginKey) {
		// 根据配置的tokenStyle生成不同风格的token 
		String tokenStyle = SaTokenManager.getConfig().getTokenStyle();
		// uuid 
		if(SaTokenConsts.TOKEN_STYLE_UUID.equals(tokenStyle)) {
			return UUID.randomUUID().toString();
		}
		// 简单uuid (不带下划线)
		if(SaTokenConsts.TOKEN_STYLE_SIMPLE_UUID.equals(tokenStyle)) {
			return UUID.randomUUID().toString().replaceAll("-", "");
		}
		// 32位随机字符串
		if(SaTokenConsts.TOKEN_STYLE_RANDOM_32.equals(tokenStyle)) {
			return SaFoxUtil.getRandomString(32);
		}
		// 64位随机字符串
		if(SaTokenConsts.TOKEN_STYLE_RANDOM_64.equals(tokenStyle)) {
			return SaFoxUtil.getRandomString(64);
		}
		// 128位随机字符串
		if(SaTokenConsts.TOKEN_STYLE_RANDOM_128.equals(tokenStyle)) {
			return SaFoxUtil.getRandomString(128);
		}
		// tik风格 (2_14_16)
		if(SaTokenConsts.TOKEN_STYLE_TIK.equals(tokenStyle)) {
			return SaFoxUtil.getRandomString(2) + "_" + SaFoxUtil.getRandomString(14) + "_" + SaFoxUtil.getRandomString(16) + "__";
		}
		// 默认，还是uuid 
		return UUID.randomUUID().toString();
	}

	/**
	 * 根据 SessionId 创建一个 Session 
	 */
	@Override
	public SaSession createSession(String sessionId) {
		return new SaSession(sessionId);
	}

	/**
	 * 指定集合是否包含指定元素（模糊匹配） 
	 */
	@Override
	public boolean hasElement(List<String> list, String element) {
		// 集合为空直接返回false
		if(list == null || list.size() == 0) {
			return false;
		}
		// 遍历匹配
		for (String patt : list) {
			if(SaFoxUtil.vagueMatch(patt, element)) {
				return true;
			}
		}
		// 走出for循环说明没有一个元素可以匹配成功 
		return false;
	}
	
}
