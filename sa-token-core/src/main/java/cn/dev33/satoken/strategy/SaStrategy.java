package cn.dev33.satoken.strategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckBasic;
import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * Sa-Token 策略对象 
 * <p>
 * 此类统一定义框架内的一些关键性逻辑算法，方便开发者进行按需重写，例：
 * </p>
 * <pre>
	// SaStrategy全局单例，所有方法都用以下形式重写 
	SaStrategy.me.setCreateToken((loginId, loginType) -》 {
		// 自定义Token生成的算法 
		return "xxxx";
	});
 * </pre>
 * 
 * @author kong
 *
 */
public final class SaStrategy {

	private SaStrategy() {
	}

	/**
	 * 获取 SaStrategy 对象的单例引用 
	 */
	public static final SaStrategy me = new SaStrategy();

	// 
	// 所有策略  
	// 
	
	/**
	 * 创建 Token 的策略 
	 * <p> 参数 [账号id, 账号类型] 
	 */
	public BiFunction<Object, String, String> createToken = (loginId, loginType) -> {
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
	};
	
	/**
	 * 创建 Session 的策略 
	 * <p> 参数 [SessionId] 
	 */
	public Function<String, SaSession> createSession = (sessionId) -> {
		return new SaSession(sessionId);
	};

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配） 
	 * <p> 参数 [集合, 元素] 
	 */
	public BiFunction<List<String>, String, Boolean> hasElement = (list, element) -> {

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
	};

	/**
	 * 对一个 [Method] 对象进行注解校验 （注解鉴权内部实现） 
	 * <p> 参数 [Method句柄] 
	 */
	public Consumer<Method> checkMethodAnnotation = (method) -> {

		// 先校验 Method 所属 Class 上的注解 
		me.checkElementAnnotation.accept(method.getDeclaringClass());

		// 再校验 Method 上的注解  
		me.checkElementAnnotation.accept(method);
	};

	/**
	 * 对一个 [元素] 对象进行注解校验 （注解鉴权内部实现） 
	 * <p> 参数 [element元素] 
	 */
	public Consumer<AnnotatedElement> checkElementAnnotation = (target) -> {
		// 校验 @SaCheckLogin 注解 
		SaCheckLogin checkLogin = (SaCheckLogin) SaStrategy.me.getAnnotation.apply(target, SaCheckLogin.class);
		if(checkLogin != null) {
			SaManager.getStpLogic(checkLogin.type()).checkByAnnotation(checkLogin);
		}
		
		// 校验 @SaCheckRole 注解 
		SaCheckRole checkRole = (SaCheckRole) SaStrategy.me.getAnnotation.apply(target, SaCheckRole.class);
		if(checkRole != null) {
			SaManager.getStpLogic(checkRole.type()).checkByAnnotation(checkRole);
		}
		
		// 校验 @SaCheckPermission 注解
		SaCheckPermission checkPermission = (SaCheckPermission) SaStrategy.me.getAnnotation.apply(target, SaCheckPermission.class);
		if(checkPermission != null) {
			SaManager.getStpLogic(checkPermission.type()).checkByAnnotation(checkPermission);
		}

		// 校验 @SaCheckSafe 注解
		SaCheckSafe checkSafe = (SaCheckSafe) SaStrategy.me.getAnnotation.apply(target, SaCheckSafe.class);
		if(checkSafe != null) {
			SaManager.getStpLogic(checkSafe.type()).checkByAnnotation(checkSafe);
		}

		// 校验 @SaCheckDisable 注解
		SaCheckDisable checkDisable = (SaCheckDisable) SaStrategy.me.getAnnotation.apply(target, SaCheckDisable.class);
		if(checkDisable != null) {
			SaManager.getStpLogic(checkDisable.type()).checkByAnnotation(checkDisable);
		}
		
		// 校验 @SaCheckBasic 注解
		SaCheckBasic checkBasic = (SaCheckBasic) SaStrategy.me.getAnnotation.apply(target, SaCheckBasic.class);
		if(checkBasic != null) {
			SaBasicUtil.check(checkBasic.realm(), checkBasic.account());
		}
	};

	/**
	 * 从元素上获取注解  
	 * <p> 参数 [element元素，要获取的注解类型] 
	 */
	public BiFunction<AnnotatedElement, Class<? extends Annotation> , Annotation> getAnnotation = (element, annotationClass)->{
		// 默认使用jdk的注解处理器 
		return element.getAnnotation(annotationClass);
	};

	/**
	 * 判断一个 Method 或其所属 Class 是否包含指定注解 
	 * <p> 参数 [Method, 注解] 
	 */
	public BiFunction<Method, AnnotatedElement, Boolean> isAnnotationPresent = (method, annotationClass) -> {
		return me.getAnnotation.apply(method, SaIgnore.class) != null || 
				me.getAnnotation.apply(method.getDeclaringClass(), SaIgnore.class) != null;
	};


	// 
	// 重写策略 set连缀风格 
	// 
	

	/**
	 * 重写创建 Token 的策略 
	 * <p> 参数 [账号id, 账号类型] 
	 * @param createToken / 
	 * @return 对象自身 
	 */
	public SaStrategy setCreateToken(BiFunction<Object, String, String> createToken) {
		this.createToken = createToken;
		return this;
	}

	/**
	 * 重写创建 Session 的策略 
	 * <p> 参数 [SessionId] 
	 * @param createSession / 
	 * @return 对象自身 
	 */
	public SaStrategy setCreateSession(Function<String, SaSession> createSession) {
		this.createSession = createSession;
		return this;
	}

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配） 
	 * <p> 参数 [集合, 元素] 
	 * @param hasElement /
	 * @return 对象自身 
	 */
	public SaStrategy setHasElement(BiFunction<List<String>, String, Boolean> hasElement) {
		this.hasElement = hasElement;
		return this;
	}

	/**
	 * 对一个 [Method] 对象进行注解校验 （注解鉴权内部实现） 
	 * <p> 参数 [Method句柄] 
	 * @param checkMethodAnnotation /
	 * @return 对象自身 
	 */
	public SaStrategy setCheckMethodAnnotation(Consumer<Method> checkMethodAnnotation) {
		this.checkMethodAnnotation = checkMethodAnnotation;
		return this;
	}

	/**
	 * 对一个 [元素] 对象进行注解校验 （注解鉴权内部实现） 
	 * <p> 参数 [element元素] 
	 * @param checkElementAnnotation / 
	 * @return 对象自身 
	 */
	public SaStrategy setCheckElementAnnotation(Consumer<AnnotatedElement> checkElementAnnotation) {
		this.checkElementAnnotation = checkElementAnnotation;
		return this;
	}

	/**
	 * 从元素上获取注解  
	 * <p> 参数 [element元素，要获取的注解类型] 
	 * @param getAnnotation / 
	 * @return 对象自身 
	 */
	public SaStrategy setGetAnnotation(BiFunction<AnnotatedElement, Class<? extends Annotation> , Annotation> getAnnotation) {
		this.getAnnotation = getAnnotation;
		return this;
	}

	/**
	 * 判断一个 Method 或其所属 Class 是否包含指定注解 
	 * <p> 参数 [Method, 注解] 
	 * @param isAnnotationPresent / 
	 * @return 对象自身 
	 */
	public SaStrategy setIsAnnotationPresent(BiFunction<Method, AnnotatedElement, Boolean> isAnnotationPresent) {
		this.isAnnotationPresent = isAnnotationPresent;
		return this;
	}
	
}
