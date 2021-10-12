package cn.dev33.satoken.strategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;

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
@SuppressWarnings("deprecation")
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
		return SaManager.getSaTokenAction().createToken(loginId, loginType);
	};
	
	/**
	 * 创建 Session 的策略 
	 * <p> 参数 [SessionId] 
	 */
	public Function<String, SaSession> createSession = (sessionId) -> {
		return SaManager.getSaTokenAction().createSession(sessionId);
	};

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配） 
	 * <p> 参数 [集合, 元素] 
	 */
	public BiFunction<List<String>, String, Boolean> hasElement = (list, element) -> {
		return SaManager.getSaTokenAction().hasElement(list, element);
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
	public Consumer<AnnotatedElement> checkElementAnnotation = (element) -> {
		// 为了兼容旧版本 
		SaManager.getSaTokenAction().validateAnnotation(element);
	};

	/**
	 * 从元素上获取注解（注解鉴权内部实现） 
	 * <p> 参数 [element元素，要获取的注解类型] 
	 */
	public BiFunction<AnnotatedElement, Class<? extends Annotation> , Annotation> getAnnotation = (element, annotationClass)->{
		// 默认使用jdk的注解处理器 
		return element.getAnnotation(annotationClass);
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
	 * 从元素上获取注解（注解鉴权内部实现） 
	 * <p> 参数 [element元素，要获取的注解类型] 
	 * @param getAnnotation / 
	 * @return 对象自身 
	 */
	public SaStrategy setGetAnnotation(BiFunction<AnnotatedElement, Class<? extends Annotation> , Annotation> getAnnotation) {
		this.getAnnotation = getAnnotation;
		return this;
	}
	
	
}
