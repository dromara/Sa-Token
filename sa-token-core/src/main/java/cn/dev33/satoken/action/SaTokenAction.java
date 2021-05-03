package cn.dev33.satoken.action;

import java.lang.reflect.Method;
import java.util.List;

import cn.dev33.satoken.session.SaSession;

/**
 * sa-token逻辑代理接口 
 * <p>此接口将会代理框架内部的一些关键性逻辑，方便开发者进行按需重写</p> 
 * @author kong
 *
 */
public interface SaTokenAction {

	/**
	 * 根据一定的算法生成一个token 
	 * @param loginId 账号id 
	 * @param loginKey 账号体系key 
	 * @return 一个token
	 */
	public String createToken(Object loginId, String loginKey); 
	
	/**
	 * 根据 SessionId 创建一个 Session
	 * @param sessionId Session的Id
	 * @return 创建后的Session 
	 */
	public SaSession createSession(String sessionId); 
	
	/**
	 * 指定集合是否包含指定元素（模糊匹配） 
	 * @param list 集合
	 * @param element 元素
	 * @return 是否包含
	 */
	public boolean hasElement(List<String> list, String element);

	/**
	 * 对一个Method对象进行注解检查（注解鉴权内部实现） 
	 * @param method Method对象
	 */
	public void checkMethodAnnotation(Method method);
	
}
