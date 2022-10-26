package cn.dev33.satoken.sso;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.exception.SaSsoExceptionCode;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token-SSO 单点登录模块 
 * @author kong
 *
 */
public class SaSsoTemplate {

	// ---------------------- 全局配置 ---------------------- 

	/**
	 * 所有 API 名称 
	 */
	public ApiName apiName = new ApiName();
	

	/**
	 * 所有参数名称 
	 */
	public ParamName paramName = new ParamName();

	/**
	 * @param paramName 替换 paramName 对象 
	 * @return 对象自身
	 */
	public SaSsoTemplate setParamName(ParamName paramName) {
		this.paramName = paramName;
		return this;
	}
	
	/**
	 * @param apiName 替换 apiName 对象 
	 * @return 对象自身
	 */
	public SaSsoTemplate setApiName(ApiName apiName) {
		this.apiName = apiName;
		return this;
	}

	/**
	 * 获取底层使用的会话对象 
	 * @return /
	 */
	public StpLogic getStpLogic() {
		return StpUtil.stpLogic;
	}

	/**
	 * 获取底层使用的配置对象 
	 * @return /
	 */
	public SaSsoConfig getSsoConfig() {
		return SaSsoManager.getConfig();
	}
	
	
	// ---------------------- Ticket 操作 ---------------------- 
	
	/**
	 * 根据 账号id 创建一个 Ticket码 
	 * @param loginId 账号id 
	 * @return Ticket码 
	 */
	public String createTicket(Object loginId) {
		// 创建 Ticket
		String ticket = randomTicket(loginId);
		
		// 保存 Ticket
		saveTicket(ticket, loginId);
		saveTicketIndex(ticket, loginId);
		
		// 返回 Ticket
		return ticket;
	}
	
	/**
	 * 保存 Ticket 
	 * @param ticket ticket码
	 * @param loginId 账号id 
	 */
	public void saveTicket(String ticket, Object loginId) {
		long ticketTimeout = SaSsoManager.getConfig().getTicketTimeout();
		SaManager.getSaTokenDao().set(splicingTicketSaveKey(ticket), String.valueOf(loginId), ticketTimeout); 
	}
	
	/**
	 * 保存 Ticket 索引 
	 * @param ticket ticket码
	 * @param loginId 账号id 
	 */
	public void saveTicketIndex(String ticket, Object loginId) {
		long ticketTimeout = SaSsoManager.getConfig().getTicketTimeout();
		SaManager.getSaTokenDao().set(splicingTicketIndexKey(loginId), String.valueOf(ticket), ticketTimeout); 
	}
	
	/**
	 * 删除 Ticket 
	 * @param ticket Ticket码
	 */
	public void deleteTicket(String ticket) {
		if(ticket == null) {
			return;
		}
		SaManager.getSaTokenDao().delete(splicingTicketSaveKey(ticket)); 
	}
	
	/**
	 * 删除 Ticket索引 
	 * @param loginId 账号id 
	 */
	public void deleteTicketIndex(Object loginId) {
		if(loginId == null) {
			return;
		}
		SaManager.getSaTokenDao().delete(splicingTicketIndexKey(loginId)); 
	}

	/**
	 * 根据 Ticket码 获取账号id，如果Ticket码无效则返回null 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public Object getLoginId(String ticket) {
		if(SaFoxUtil.isEmpty(ticket)) {
			return null;
		}
		return SaManager.getSaTokenDao().get(splicingTicketSaveKey(ticket));
	}

	/**
	 * 根据 Ticket码 获取账号id，并转换为指定类型 
	 * @param <T> 要转换的类型 
	 * @param ticket Ticket码
	 * @param cs 要转换的类型 
	 * @return 账号id 
	 */
	public <T> T getLoginId(String ticket, Class<T> cs) {
		return SaFoxUtil.getValueByType(getLoginId(ticket), cs);
	}

	/**
	 * 查询 指定账号id的 Ticket值 
	 * @param loginId 账号id 
	 * @return Ticket值 
	 */
	public String getTicketValue(Object loginId) {
		if(loginId == null) {
			return null;
		}
		return SaManager.getSaTokenDao().get(splicingTicketIndexKey(loginId));
	}

	/**
	 * 校验ticket码，获取账号id，如果此ticket是有效的，则立即删除 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public Object checkTicket(String ticket) {
		Object loginId = getLoginId(ticket);
		if(loginId != null) {
			deleteTicket(ticket);
			deleteTicketIndex(loginId);
		}
		return loginId;
	}
	
	/**
	 * 随机一个 Ticket码 
	 * @param loginId 账号id 
	 * @return Ticket码 
	 */
	public String randomTicket(Object loginId) {
		return SaFoxUtil.getRandomString(64);
	}

	/**
	 * 获取：所有允许的授权回调地址，多个用逗号隔开 (不在此列表中的URL将禁止下放ticket) 
	 * @return see note 
	 */
	public String getAllowUrl() {
		// 默认从配置文件中返回 
		return SaSsoManager.getConfig().getAllowUrl();
	}
	
	/**
	 * 校验重定向url合法性
	 * @param url 下放ticket的url地址 
	 */
	public void checkRedirectUrl(String url) {
		
		// 1、是否是一个有效的url 
		if(SaFoxUtil.isUrl(url) == false) {
			throw new SaSsoException("无效redirect：" + url).setCode(SaSsoExceptionCode.CODE_20001);
		}
		
		// 2、截取掉?后面的部分 
		int qIndex = url.indexOf("?");
		if(qIndex != -1) {
			url = url.substring(0, qIndex);
		}
		
		// 3、是否在[允许地址列表]之中 
		List<String> authUrlList = Arrays.asList(getAllowUrl().replaceAll(" ", "").split(",")); 
		if(SaStrategy.me.hasElement.apply(authUrlList, url) == false) {
			throw new SaSsoException("非法redirect：" + url).setCode(SaSsoExceptionCode.CODE_20002);
		}
		
		// 校验通过 √ 
		return;
	}
	

	// ------------------- SSO 模式三相关 ------------------- 

	/**
	 * 为指定账号id注册单点注销回调URL 
	 * @param loginId 账号id
	 * @param sloCallbackUrl 单点注销时的回调URL 
	 */
	public void registerSloCallbackUrl(Object loginId, String sloCallbackUrl) {
		if(SaFoxUtil.isEmpty(loginId) || SaFoxUtil.isEmpty(sloCallbackUrl)) {
			return;
		}
		SaSession session = getStpLogic().getSessionByLoginId(loginId);
		Set<String> urlSet = session.get(SaSsoConsts.SLO_CALLBACK_SET_KEY, ()-> new HashSet<String>());
		urlSet.add(sloCallbackUrl);
		session.set(SaSsoConsts.SLO_CALLBACK_SET_KEY, urlSet);
	}
	
	/**
	 * 指定账号单点注销 
	 * @param loginId 指定账号 
	 */
	public void ssoLogout(Object loginId) {
		
		// 如果这个账号尚未登录，则无操作 
		SaSession session = getStpLogic().getSessionByLoginId(loginId, false);
		if(session == null) {
			return;
		}
		
		// step.1 遍历通知 Client 端注销会话 
		SaSsoConfig cfg = SaSsoManager.getConfig();
		Set<String> urlSet = session.get(SaSsoConsts.SLO_CALLBACK_SET_KEY, () -> new HashSet<String>());
		for (String url : urlSet) {
			url = addSignParams(url, loginId);
			cfg.getSendHttp().apply(url);
		}
		
		// step.2 Server端注销 
		getStpLogic().logout(loginId);
	}

	/**
	 * 获取：账号资料 
	 * @param loginId 账号id
	 * @return 账号资料  
	 */
	public Object getUserinfo(Object loginId) {
		String url = buildUserinfoUrl(loginId);
		return SaSsoManager.getConfig().getSendHttp().apply(url);
	}

	
	// ---------------------- 构建URL ---------------------- 

	/**
	 * 构建URL：Server端 单点登录地址 
	 * @param clientLoginUrl Client端登录地址 
	 * @param back 回调路径 
	 * @return [SSO-Server端-认证地址 ] 
	 */
	public String buildServerAuthUrl(String clientLoginUrl, String back) {
		
		// 服务端认证地址 
		String serverUrl = SaSsoManager.getConfig().splicingAuthUrl();
		
		// 对back地址编码 
		back = (back == null ? "" : back);
		back = SaFoxUtil.encodeUrl(back);
		
		// 开始拼接 sso 统一认证地址，形如：serverAuthUrl = http://xxx.com?redirectUrl=xxx.com?back=xxx.com
		
		/*
		 * 部分 Servlet 版本 request.getRequestURL() 返回的 url 带有 query 参数，形如：http://domain.com?id=1，
		 * 如果不加判断会造成最终生成的 serverAuthUrl 带有双 back 参数 ，这个 if 判断正是为了解决此问题 
		 */
		if(clientLoginUrl.indexOf(paramName.back + "=" + back) == -1) {
			clientLoginUrl = SaFoxUtil.joinParam(clientLoginUrl, paramName.back, back); 
		}
		String serverAuthUrl = SaFoxUtil.joinParam(serverUrl, paramName.redirect, clientLoginUrl);
		
		// 返回 
		return serverAuthUrl;
	}
	
	/**
	 * 构建URL：Server端向Client下放ticke的地址 
	 * @param loginId 账号id 
	 * @param redirect Client端提供的重定向地址 
	 * @return see note 
	 */
	public String buildRedirectUrl(Object loginId, String redirect) {
		
		// 校验 重定向地址 是否合法 
		checkRedirectUrl(redirect);
		
		// 删掉 旧Ticket  
		deleteTicket(getTicketValue(loginId));
		
		// 创建 新Ticket
		String ticket = createTicket(loginId);
		
		// 构建 授权重定向地址 （Server端 根据此地址向 Client端 下放Ticket）
		return SaFoxUtil.joinParam(encodeBackParam(redirect), paramName.ticket, ticket);
	}

	/**
	 * 对url中的back参数进行URL编码, 解决超链接重定向后参数丢失的bug 
	 * @param url url
	 * @return 编码过后的url 
	 */
	public String encodeBackParam(String url) {
		
		// 获取back参数所在位置 
		int index = url.indexOf("?" + paramName.back + "=");
		if(index == -1) {
			index = url.indexOf("&" + paramName.back + "=");
			if(index == -1) {
				return url;
			}
		}
		
		// 开始编码 
		int length = paramName.back.length() + 2;
		String back = url.substring(index + length);
		back = SaFoxUtil.encodeUrl(back);
		
		// 放回url中 
		url = url.substring(0, index + length) + back;
		return url;
	}

	/**
	 * 构建URL：Server端 账号资料查询地址 
	 * @param loginId 账号id
	 * @return Server端 账号资料查询地址 
	 */
	public String buildUserinfoUrl(Object loginId) {
		String userinfoUrl = SaSsoManager.getConfig().splicingUserinfoUrl();
		return addSignParams(userinfoUrl, loginId);
	}

	/**
	 * 构建URL：校验ticket的URL 
	 * <p> 在模式三下，Client端拿到Ticket后根据此地址向Server端发送请求，获取账号id 
	 * @param ticket ticket码
	 * @param ssoLogoutCallUrl 单点注销时的回调URL 
	 * @return 构建完毕的URL 
	 */
	public String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {
		// 裸地址 
		String url = SaSsoManager.getConfig().splicingCheckTicketUrl();
		
		// 拼接ticket参数 
		url = SaFoxUtil.joinParam(url, paramName.ticket, ticket);
		
		// 拼接单点注销时的回调URL 
		if(ssoLogoutCallUrl != null) {
			url = SaFoxUtil.joinParam(url, paramName.ssoLogoutCall, ssoLogoutCallUrl);
		}
		
		// 返回 
		return url;
	}

	/**
	 * 构建URL：单点注销URL 
	 * @param loginId 要注销的账号id
	 * @return 单点注销URL 
	 */
	public String buildSloUrl(Object loginId) {
		String url = SaSsoManager.getConfig().splicingSloUrl();
		return addSignParams(url, loginId);
	}

	
	// ------------------- 返回相应key ------------------- 

	/** 
	 * 拼接key：Ticket 查 账号Id 
	 * @param ticket ticket值 
	 * @return key
	 */
	public String splicingTicketSaveKey(String ticket) {
		return SaManager.getConfig().getTokenName() + ":ticket:" + ticket;
	}

	/** 
	 * 拼接key：账号Id 反查 Ticket 
	 * @param id 账号id
	 * @return key
	 */
	public String splicingTicketIndexKey(Object id) {
		return SaManager.getConfig().getTokenName() + ":id-ticket:" + id;
	}


	// ------------------- 请求相关 ------------------- 

	/**
	 * 发出请求，并返回 SaResult 结果 
	 * @param url 请求地址 
	 * @return 返回的结果 
	 */
	public SaResult request(String url) {
		String body = SaSsoManager.getConfig().getSendHttp().apply(url);
		Map<String, Object> map = SaManager.getSaJsonTemplate().parseJsonToMap(body);
		return new SaResult(map);
	}

	/**
	 * 获取：接口调用秘钥 
	 * @return see note 
	 */
	public String getSecretkey() {
		// 默认从配置文件中返回 
		String secretkey = SaSsoManager.getConfig().getSecretkey();
		if(SaFoxUtil.isEmpty(secretkey)) {
			throw new SaSsoException("请配置 secretkey 参数").setCode(SaSsoExceptionCode.CODE_20009);
		}
		return secretkey;
	}

	/**
	 * 校验secretkey秘钥是否有效 （API已过期，请更改为更安全的 sign 式校验）
	 * @param secretkey 秘钥 
	 */
	@Deprecated
	public void checkSecretkey(String secretkey) {
		 if(SaFoxUtil.isEmpty(secretkey) || secretkey.equals(getSecretkey()) == false) {
			 throw new SaSsoException("无效秘钥：" + secretkey).setCode(SaSsoExceptionCode.CODE_20003);
		 }
	}
	
	/**
	 * 根据参数计算签名 
	 * @param loginId 账号id
	 * @param timestamp 当前时间戳，13位
	 * @param nonce 随机字符串 
	 * @param secretkey 账号id
	 * @return 签名 
	 */
	public String getSign(Object loginId, String timestamp, String nonce, String secretkey) {
		Map<String, Object> map = new TreeMap<>();
		map.put(paramName.loginId, loginId);
		map.put(paramName.timestamp, timestamp);
		map.put(paramName.nonce, nonce);
		return SaManager.getSaSignTemplate().createSign(map, secretkey);
	}

	/**
	 * 给 url 追加 sign 等参数 
	 * @param url 连接
	 * @param loginId 账号id 
	 * @return 加工后的url 
	 */
	public String addSignParams(String url, Object loginId) {
		
		// 时间戳、随机字符串、参数签名
		String timestamp = String.valueOf(System.currentTimeMillis());
		String nonce = SaFoxUtil.getRandomString(20);
		String sign = getSign(loginId, timestamp, nonce, getSecretkey());
		
		// 追加到url 
		url = SaFoxUtil.joinParam(url, paramName.loginId, loginId);
		url = SaFoxUtil.joinParam(url, paramName.timestamp, timestamp);
		url = SaFoxUtil.joinParam(url, paramName.nonce, nonce);
		url = SaFoxUtil.joinParam(url, paramName.sign, sign);
		return url;
	}

	/**
	 * 校验签名
	 * @param req request 
	 */
	public void checkSign(SaRequest req) {

		// 参数签名、账号id、时间戳、随机字符串
		String sign = req.getParamNotNull(paramName.sign);
		String loginId = req.getParamNotNull(paramName.loginId);
		String timestamp = req.getParamNotNull(paramName.timestamp);
		String nonce = req.getParamNotNull(paramName.nonce);
		
		// 校验时间戳 
		checkTimestamp(Long.valueOf(timestamp));
		
		// 校验签名 
		String calcSign = getSign(loginId, timestamp, nonce, getSecretkey());
		if(calcSign.equals(sign) == false) {
			throw new SaSsoException("签名无效：" + calcSign).setCode(SaSsoExceptionCode.CODE_20008);
		}
	}

	/**
	 * 校验时间戳与当前时间的差距是否超出限制 
	 * @param timestamp 时间戳 
	 */
	public void checkTimestamp(long timestamp) {
		long disparity = Math.abs(System.currentTimeMillis() - timestamp);
		long allowDisparity = SaSsoManager.getConfig().getTimestampDisparity();
		if(allowDisparity != -1 && disparity > allowDisparity) {
			throw new SaSsoException("timestamp 超出允许的范围").setCode(SaSsoExceptionCode.CODE_20007);
		}
	}
	
}
