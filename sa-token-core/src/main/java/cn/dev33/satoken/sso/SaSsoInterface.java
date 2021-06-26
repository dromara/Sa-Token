package cn.dev33.satoken.sso;

import java.util.Arrays;
import java.util.List;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.exception.SaTokenException;
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
		SaManager.getSaTokenDao().delete(splicingKeyTicketToId(ticket)); 
	}
	
	/**
	 * 根据 账号id & 重定向地址，构建[SSO-Client端-重定向地址] 
	 * @param loginId 账号id 
	 * @param redirect 重定向地址
	 * @return [SSO-Client端-重定向地址]
	 */
	public default String buildRedirectUrl(Object loginId, String redirect) {
		// 校验授权地址 
		checkAuthUrl(redirect);
		
		// 删掉旧ticket  
		String oldTicket = SaManager.getSaTokenDao().get(splicingKeyIdToTicket(loginId));
		if(oldTicket != null) {
			deleteTicket(oldTicket);
		}
		
		// 获取新ticket
		String ticket = createTicket(loginId);
		
		// 构建 授权重定向地址
		redirect = encodeBackParam(redirect);
		String redirectUrl = SaFoxUtil.joinParam(redirect, SaSsoConsts.TICKET_NAME + "=" + ticket);
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
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public default <T> T getLoginId(String ticket, Class<T> cs) {
		return SaFoxUtil.getValueByType(getLoginId(ticket), cs);
	}
	
	/**
	 * 校验url合法性
	 * @param url 地址
	 */
	public default void checkAuthUrl(String url) {
		
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
	 * 根据 Client端登录地址 & back地址 ，构建[SSO-Server端-认证地址] 
	 * @param clientLoginUrl Client端登录地址 
	 * @param back 回调路径 
	 * @return [SSO-Server端-认证地址 ]
	 */
	public default String buildServerAuthUrl(String clientLoginUrl, String back) {
		// 服务端认证地址 
		String serverUrl = SaManager.getConfig().getSso().getServerUrl();
		
		// 对back地址编码 
		back = (back == null ? "" : back);
		back = SaFoxUtil.encodeUrl(back);
		
		// 拼接最终地址，格式：serverAuthUrl = http://xxx.com?redirectUrl=xxx.com?back=xxx.com
		clientLoginUrl = SaFoxUtil.joinParam(clientLoginUrl, SaSsoConsts.BACK_NAME + "=" + back); 
		String serverAuthUrl = SaFoxUtil.joinParam(serverUrl, SaSsoConsts.REDIRECT_NAME + "=" + clientLoginUrl);
		
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
		int index = url.indexOf("?" + SaSsoConsts.BACK_NAME + "=");
		if(index == -1) {
			index = url.indexOf("&" + SaSsoConsts.BACK_NAME + "=");
			if(index == -1) {
				return url;
			}
		}
		
		// 开始编码 
		int length = SaSsoConsts.BACK_NAME.length() + 2;
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

	
}
