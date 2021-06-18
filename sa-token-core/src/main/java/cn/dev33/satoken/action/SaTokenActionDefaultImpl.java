package cn.dev33.satoken.action;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Sa-Token 逻辑代理接口 [默认实现类] 
 * @author kong
 *
 */
public class SaTokenActionDefaultImpl implements SaTokenAction {

	/**
	 * 创建一个Token 
	 */
	@Override
	public String createToken(Object loginId, String loginType) {
		// 根据配置的tokenStyle生成不同风格的token 
		String tokenStyle = SaManager.getConfig().getTokenStyle();
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
	 * 创建一个Session 
	 */
	@Override
	public SaSession createSession(String sessionId) {
		return new SaSession(sessionId);
	}

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配） 
	 */
	@Override
	public boolean hasElement(List<String> list, String element) {
		
		// 空集合直接返回false
		if(list == null || list.size() == 0) {
			return false;
		}

		// 先尝试一下简单匹配，如果可以匹配成功则无需继续模糊匹配 
		if (list.contains(element)) {
			return true;
		}
		
		// 开始模糊匹配 
		for (String patt : list) {
			if(SaFoxUtil.vagueMatch(patt, element)) {
				return true;
			}
		}
		
		// 走出for循环说明没有一个元素可以匹配成功 
		return false;
	}

	/**
	 * 对一个Method对象进行注解检查（注解鉴权内部实现） 
	 */
	@Override
	public void checkMethodAnnotation(Method method) {

		// 先校验 Method 所属 Class 上的注解 
		validateAnnotation(method.getDeclaringClass());
		
		// 再校验 Method 上的注解  
		validateAnnotation(method);
	}

	/**
	 * 从指定元素校验注解 
	 * @param target see note 
	 */
	protected void validateAnnotation(AnnotatedElement target) {
		
		// 校验 @SaCheckLogin 注解 
		if(target.isAnnotationPresent(SaCheckLogin.class)) {
			SaCheckLogin at = target.getAnnotation(SaCheckLogin.class);
			SaManager.getStpLogic(at.type()).checkByAnnotation(at);
		}

		// 校验 @SaCheckRole 注解 
		if(target.isAnnotationPresent(SaCheckRole.class)) {
			SaCheckRole at = target.getAnnotation(SaCheckRole.class);
			SaManager.getStpLogic(at.type()).checkByAnnotation(at);
		}

		// 校验 @SaCheckPermission 注解
		if(target.isAnnotationPresent(SaCheckPermission.class)) {
			SaCheckPermission at = target.getAnnotation(SaCheckPermission.class);
			SaManager.getStpLogic(at.type()).checkByAnnotation(at);
		}

		// 校验 @SaCheckSafe 注解
		if(target.isAnnotationPresent(SaCheckSafe.class)) {
			SaCheckSafe at = target.getAnnotation(SaCheckSafe.class);
			SaManager.getStpLogic(null).checkByAnnotation(at);
		}
	}
	
}
