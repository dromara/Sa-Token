package cn.dev33.satoken.sso;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.sso.SaSsoConsts.ParamName;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * Sa-Token-SSO 单点登录接口 
 * @author kong
 *
 */
public interface SaSsoInterface {
	
	/**
	 * 创建一个 Ticket码 
	 * @param loginId 账号id 
	 * @return 票据 
	 */
	public default String createTicket(Object loginId) {
		// 随机一个ticket
		String ticket = randomTicket(loginId);
		
		// 保存入库 
		long ticketTimeout = SaManager.getConfig().getSso().getTicketTimeout();
		SaManager.getSaTokenDao().set(splicingKeyTicketToId(ticket), String.valueOf(loginId), ticketTimeout); 
		SaManager.getSaTokenDao().set(splicingKeyIdToTicket(loginId), String.valueOf(ticket), ticketTimeout); 
		
		// 返回 
		return ticket;
	}
	
	/**
	 * 删除一个 Ticket码
	 * @param ticket Ticket码
	 */
	public default void deleteTicket(String ticket) {
		Object loginId = getLoginId(ticket);
		if(loginId != null) {
			SaManager.getSaTokenDao().delete(splicingKeyTicketToId(ticket)); 
			SaManager.getSaTokenDao().delete(splicingKeyIdToTicket(loginId));
		}
	}
	
	/**
	 * 构建URL：Server端向Client下放ticke的地址
	 * @param loginId 账号id 
	 * @param redirect Client端提供的重定向地址
	 * @return see note 
	 */
	public default String buildRedirectUrl(Object loginId, String redirect) {
		// 校验重定向地址 
		checkRedirectUrl(redirect);
		
		// 删掉旧ticket  
		String oldTicket = SaManager.getSaTokenDao().get(splicingKeyIdToTicket(loginId));
		if(oldTicket != null) {
			deleteTicket(oldTicket);
		}
		
		// 获取新ticket
		String ticket = createTicket(loginId);
		
		// 构建 授权重定向地址
		redirect = encodeBackParam(redirect);
		String redirectUrl = SaFoxUtil.joinParam(redirect, ParamName.ticket, ticket);
		return redirectUrl;
	}
	
	/**
	 * 根据 Ticket码 获取账号id，如果Ticket码无效则返回null 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public default Object getLoginId(String ticket) {
		if(SaFoxUtil.isEmpty(ticket)) {
			return null;
		}
		return SaManager.getSaTokenDao().get(splicingKeyTicketToId(ticket));
	}

	/**
	 * 根据 Ticket码 获取账号id，并转换为指定类型 
	 * @param <T> 要转换的类型 
	 * @param ticket Ticket码
	 * @param cs 要转换的类型 
	 * @return 账号id 
	 */
	public default <T> T getLoginId(String ticket, Class<T> cs) {
		return SaFoxUtil.getValueByType(getLoginId(ticket), cs);
	}
	
	/**
	 * 校验ticket码，获取账号id，如果ticket可以有效，则立刻删除 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public default Object checkTicket(String ticket) {
		Object loginId = getLoginId(ticket);
		if(loginId != null) {
			deleteTicket(ticket);
		}
		return loginId;
	}
	
	/**
	 * 校验重定向url合法性
	 * @param url 下放ticket的url地址 
	 */
	public default void checkRedirectUrl(String url) {
		
		// 1、是否是一个有效的url
		if(SaFoxUtil.isUrl(url) == false) {
			throw new SaTokenException("无效回调地址：" + url);
		}
		
		// 2、截取掉?后面的部分 
		int qIndex = url.indexOf("?");
		if(qIndex != -1) {
			url = url.substring(0, qIndex);
		}
		
		// 3、是否在[允许地址列表]之中 
		String authUrl = SaManager.getConfig().getSso().getAllowUrl().replaceAll(" ", "");
		List<String> authUrlList = Arrays.asList(authUrl.split(",")); 
		if(SaManager.getSaTokenAction().hasElement(authUrlList, url) == false) {
			throw new SaTokenException("非法回调地址：" + url);
		}
		
		// 验证通过 
		return;
	}
	
	/**
	 * 构建URL：Server端 单点登录地址
	 * @param clientLoginUrl Client端登录地址 
	 * @param back 回调路径 
	 * @return [SSO-Server端-认证地址 ]
	 */
	public default String buildServerAuthUrl(String clientLoginUrl, String back) {
		// 服务端认证地址 
		String serverUrl = SaManager.getConfig().getSso().getAuthUrl();
		
		// 对back地址编码 
		back = (back == null ? "" : back);
		back = SaFoxUtil.encodeUrl(back);
		
		// 拼接最终地址，格式示例：serverAuthUrl = http://xxx.com?redirectUrl=xxx.com?back=xxx.com
		clientLoginUrl = SaFoxUtil.joinParam(clientLoginUrl, ParamName.back, back); 
		String serverAuthUrl = SaFoxUtil.joinParam(serverUrl, ParamName.redirect, clientLoginUrl);
		
		// 返回 
		return serverAuthUrl;
	}
	
	/**
	 * 对url中的back参数进行URL编码, 解决超链接重定向后参数丢失的bug 
	 * @param url url
	 * @return 编码过后的url 
	 */
	public default String encodeBackParam(String url) {
		
		// 获取back参数所在位置 
		int index = url.indexOf("?" + ParamName.back + "=");
		if(index == -1) {
			index = url.indexOf("&" + ParamName.back + "=");
			if(index == -1) {
				return url;
			}
		}
		
		// 开始编码 
		int length = ParamName.back.length() + 2;
		String back = url.substring(index + length);
		back = SaFoxUtil.encodeUrl(back);
		
		// 放回url中 
		url = url.substring(0, index + length) + back;
		return url;
	}

	/**
	 * 随机一个 Ticket码 
	 * @param loginId 账号id 
	 * @return 票据 
	 */
	public default String randomTicket(Object loginId) {
		return SaFoxUtil.getRandomString(64);
	}
	

	// ------------------- SSO 模式三 ------------------- 

	/**
	 * 校验secretkey秘钥是否有效 
	 * @param secretkey 秘钥 
	 */
	public default void checkSecretkey(String secretkey) {
		 if(secretkey == null || secretkey.isEmpty() || secretkey.equals(SaManager.getConfig().getSso().getSecretkey()) == false) {
			 throw new SaTokenException("无效秘钥：" + secretkey);
		 }
	}
	
	/**
	 * 构建URL：校验ticket的URL 
	 * @param ticket ticket码
	 * @param ssoLogoutCallUrl 单点注销时的回调URL 
	 * @return 构建完毕的URL 
	 */
	public default String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {
		String url = SaManager.getConfig().getSso().getCheckTicketUrl();
		// 拼接ticket参数 
		url = SaFoxUtil.joinParam(url, ParamName.ticket, ticket);
		// 拼接单点注销时的回调URL 
		if(ssoLogoutCallUrl != null) {
			url = SaFoxUtil.joinParam(url, ParamName.ssoLogoutCall, ssoLogoutCallUrl);
		}
		// 返回 
		return url;
	}
	
	/**
	 * 为指定账号id注册单点注销回调URL 
	 * @param loginId 账号id
	 * @param sloCallbackUrl 单点注销时的回调URL 
	 */
	public default void registerSloCallbackUrl(Object loginId, String sloCallbackUrl) {
		if(loginId == null || sloCallbackUrl == null || sloCallbackUrl.isEmpty()) {
			return;
		}
		Set<String> urlSet = StpUtil.getSessionByLoginId(loginId).get(SaSsoConsts.SLO_CALLBACK_SET_KEY, ()-> new HashSet<String>());
		urlSet.add(sloCallbackUrl);
		StpUtil.getSessionByLoginId(loginId).set(SaSsoConsts.SLO_CALLBACK_SET_KEY, urlSet);
	}
	
	/**
	 * 循环调用Client端单点注销回调 
	 * @param loginId 账号id
	 * @param fun 调用方法 
	 */
	public default void forEachSloUrl(Object loginId, CallSloUrlFunction fun) {
		String secretkey = SaManager.getConfig().getSso().getSecretkey();
		Set<String> urlSet = StpUtil.getSessionByLoginId(loginId).get(SaSsoConsts.SLO_CALLBACK_SET_KEY,
				() -> new HashSet<String>());
		
		for (String url : urlSet) {
			// 拼接：login参数、秘钥参数
			url = SaFoxUtil.joinParam(url, ParamName.loginId, loginId);
			url = SaFoxUtil.joinParam(url, ParamName.secretkey, secretkey);
			// 调用 
			fun.run(url);
		}
	}

	/**
	 * 构建URL：单点注销URL 
	 * @param loginId 要注销的账号id
	 * @return 单点注销URL 
	 */
	public default String buildSloUrl(Object loginId) {
		SaSsoConfig ssoConfig = SaManager.getConfig().getSso();
		String url = ssoConfig.getSloUrl();
		url = SaFoxUtil.joinParam(url, ParamName.loginId, loginId);
		url = SaFoxUtil.joinParam(url, ParamName.secretkey, ssoConfig.getSecretkey());
		return url;
	}
	
	/**
	 * 指定账号单点注销 
	 * @param secretkey 校验秘钥
	 * @param loginId 指定账号 
	 * @param fun 调用方法 
	 */
	public default void singleLogout(String secretkey, Object loginId, CallSloUrlFunction fun) {
		// step.1 校验秘钥 
		checkSecretkey(secretkey);
		
		// step.2 遍历通知Client端注销会话 
		forEachSloUrl(loginId, fun);
		
		// step.3 Server端注销 
		// StpUtil.logoutByLoginId(loginId);
		StpUtil.logoutByTokenValue(StpUtil.getTokenValueByLoginId(loginId));
	}

	
	
	// ------------------- 返回相应key ------------------- 

	/** 
	 * 拼接key：Ticket 查 账号Id 
	 * @param ticket 
	 * @return key
	 */
	public default String splicingKeyTicketToId(String ticket) {
		return SaManager.getConfig().getTokenName() + ":ticket:" + ticket;
	}

	/** 
	 * 拼接key：账号Id 反查 Ticket
	 * @param id 账号id
	 * @return key
	 */
	public default String splicingKeyIdToTicket(Object id) {
		return SaManager.getConfig().getTokenName() + ":id-ticket:" + id;
	}

	
	@FunctionalInterface
	static interface CallSloUrlFunction{
		/**
		 * 调用function 
		 * @param url 注销回调URL
		 */
		public void run(String url);
	}
	
	
}
