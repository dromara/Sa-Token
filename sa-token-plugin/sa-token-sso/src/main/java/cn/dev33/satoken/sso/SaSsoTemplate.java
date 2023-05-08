package cn.dev33.satoken.sso;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaSsoConfig;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.sso.error.SaSsoErrorCode;
import cn.dev33.satoken.sso.exception.SaSsoException;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.*;

/**
 * Sa-Token-SSO 单点登录模块 
 * @author click33
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

	/**
	 * 获取底层使用的 API 签名对象
	 * @return /
	 */
	public SaSignTemplate getSignTemplate() {
		return SaManager.getSaSignTemplate();
	}


	// ---------------------- Ticket 操作 ---------------------- 
	
	/**
	 * 根据 账号id 创建一个 Ticket码 
	 * @param loginId 账号id 
	 * @param client 客户端标识 
	 * @return Ticket码 
	 */
	public String createTicket(Object loginId, String client) {
		// 创建 Ticket
		String ticket = randomTicket(loginId);
		
		// 保存 Ticket
		saveTicket(ticket, loginId, client);
		saveTicketIndex(ticket, loginId);
		
		// 返回 Ticket
		return ticket;
	}
	
	/**
	 * 保存 Ticket 
	 * @param ticket ticket码
	 * @param loginId 账号id 
	 * @param client 客户端标识 
	 */
	public void saveTicket(String ticket, Object loginId, String client) {
		String value = String.valueOf(loginId);
		if(SaFoxUtil.isNotEmpty(client)) {
			value += "," + client;
		}
		long ticketTimeout = SaSsoManager.getConfig().getTicketTimeout();
		SaManager.getSaTokenDao().set(splicingTicketSaveKey(ticket), value, ticketTimeout); 
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
		String loginId = SaManager.getSaTokenDao().get(splicingTicketSaveKey(ticket));
		// 如果是 "a,b" 的格式，则只取最前面的一项 
		if(loginId != null && loginId.contains(",")) {
			String[] arr = loginId.split(",");
			loginId = arr[0];
		}
		return loginId;
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
	 * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除 
	 * @param ticket Ticket码
	 * @return 账号id 
	 */
	public Object checkTicket(String ticket) {
		return checkTicket(ticket, getSsoConfig().getClient());
	}
	
	/**
	 * 校验 Ticket 码，获取账号id，如果此ticket是有效的，则立即删除 
	 * @param ticket Ticket码
	 * @param client client 标识 
	 * @return 账号id 
	 */
	public Object checkTicket(String ticket, String client) {
		// 读取 loginId
		String loginId = SaManager.getSaTokenDao().get(splicingTicketSaveKey(ticket));
		
		if(loginId != null) {

			// 如果是 "a,b" 的格式，则解析出对应的 Client
			String ticketClient = null;
			if(loginId.contains(",")) {
				String[] arr = loginId.split(",");
				loginId = arr[0];
				ticketClient = arr[1];
			}
			
			// 如果指定了 client 标识，则校验一下 client 标识是否一致 
			if(SaFoxUtil.isNotEmpty(client) && SaFoxUtil.notEquals(client, ticketClient)) {
				throw new SaSsoException("该 ticket 不属于 client=" + client + ", ticket 值: " + ticket)
					.setCode(SaSsoErrorCode.CODE_30011);
			}
			
			// 删除 ticket 信息，使其只有一次性有效
			deleteTicket(ticket);
			deleteTicketIndex(loginId);
		}
		
		// 
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
		if( ! SaFoxUtil.isUrl(url) ) {
			throw new SaSsoException("无效redirect：" + url).setCode(SaSsoErrorCode.CODE_30001);
		}
		
		// 2、截取掉?后面的部分 
		int qIndex = url.indexOf("?");
		if(qIndex != -1) {
			url = url.substring(0, qIndex);
		}
		
		// 3、是否在[允许地址列表]之中 
		List<String> authUrlList = Arrays.asList(getAllowUrl().replaceAll(" ", "").split(",")); 
		if( ! SaStrategy.me.hasElement.apply(authUrlList, url) ) {
			throw new SaSsoException("非法redirect：" + url).setCode(SaSsoErrorCode.CODE_30002);
		}
		
		// 校验通过 √
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
		Set<String> urlSet = session.get(SaSsoConsts.SLO_CALLBACK_SET_KEY, HashSet::new);
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
		Set<String> urlSet = session.get(SaSsoConsts.SLO_CALLBACK_SET_KEY, HashSet::new);
		for (String url : urlSet) {
			url = joinLoginIdAndSign(url, loginId);
			cfg.getSendHttp().apply(url);
		}
		
		// step.2 Server端注销 
		getStpLogic().logout(loginId);
	}

	/**
	 * 根据配置的 getData 地址，查询数据
	 * @param paramMap 查询参数
	 * @return 查询结果
	 */
	public Object getData(Map<String, Object> paramMap) {
		String getDataUrl = SaSsoManager.getConfig().splicingGetDataUrl();
		return getData(getDataUrl, paramMap);
	}

	/**
	 * 根据自定义 path 地址，查询数据 （此方法需要配置 sa-token.sso.server-url 地址）
	 * @param path 自定义 path
	 * @param paramMap 查询参数
	 * @return 查询结果
	 */
	public Object getData(String path, Map<String, Object> paramMap) {
		String url = buildCustomPathUrl(path, paramMap);
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
		
		// 拼接客户端标识 
		String client = SaSsoManager.getConfig().getClient();
		if(SaFoxUtil.isNotEmpty(client)) {
			serverUrl = SaFoxUtil.joinParam(serverUrl, paramName.client, client); 
		}
		
		
		// 对back地址编码 
		back = (back == null ? "" : back);
		back = SaFoxUtil.encodeUrl(back);
		
		// 开始拼接 sso 统一认证地址，形如：serverAuthUrl = http://xxx.com?redirectUrl=xxx.com?back=xxx.com
		
		/*
		 * 部分 Servlet 版本 request.getRequestURL() 返回的 url 带有 query 参数，形如：http://domain.com?id=1，
		 * 如果不加判断会造成最终生成的 serverAuthUrl 带有双 back 参数 ，这个 if 判断正是为了解决此问题 
		 */
		if( ! clientLoginUrl.contains(paramName.back + "=" + back) ) {
			clientLoginUrl = SaFoxUtil.joinParam(clientLoginUrl, paramName.back, back); 
		}

		// 返回 
		return SaFoxUtil.joinParam(serverUrl, paramName.redirect, clientLoginUrl);
	}
	
	/**
	 * 构建URL：Server端向Client下放ticket的地址
	 * @param loginId 账号id 
	 * @param client 客户端标识 
	 * @param redirect Client端提供的重定向地址 
	 * @return see note 
	 */
	public String buildRedirectUrl(Object loginId, String client, String redirect) {
		
		// 校验 重定向地址 是否合法 
		checkRedirectUrl(redirect);
		
		// 删掉 旧Ticket  
		deleteTicket(getTicketValue(loginId));
		
		// 创建 新Ticket
		String ticket = createTicket(loginId, client);
		
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
	 * 构建URL：校验ticket的URL 
	 * <p> 在模式三下，Client端拿到Ticket后根据此地址向Server端发送请求，获取账号id 
	 * @param ticket ticket码
	 * @param ssoLogoutCallUrl 单点注销时的回调URL 
	 * @return 构建完毕的URL 
	 */
	public String buildCheckTicketUrl(String ticket, String ssoLogoutCallUrl) {
		// 裸地址 
		String url = SaSsoManager.getConfig().splicingCheckTicketUrl();
		
		// 拼接 client 参数
		String client = getSsoConfig().getClient();
		if(SaFoxUtil.isNotEmpty(client)) {
			url = SaFoxUtil.joinParam(url, paramName.client, client);
		}
		
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
		return joinLoginIdAndSign(url, loginId);
	}

	/**
	 * 构建URL：Server端 getData 地址，带签名等参数
	 * @param paramMap 查询参数
	 * @return /
	 */
	public String buildGetDataUrl(Map<String, Object> paramMap) {
		String getDataUrl = SaSsoManager.getConfig().getGetDataUrl();
		return buildCustomPathUrl(getDataUrl, paramMap);
	}

	/**
	 * 构建URL：Server 端自定义 path 地址，带签名等参数 （此方法需要配置 sa-token.sso.server-url 地址）
	 * @param paramMap 请求参数
	 * @return /
	 */
	public String buildCustomPathUrl(String path, Map<String, Object> paramMap) {
		// 如果path不是以 http 开头，那么就拼接上 serverUrl
		String url = path;
		if( ! url.startsWith("http") ) {
			String serverUrl = SaSsoManager.getConfig().getServerUrl();
			SaSsoException.throwByNull(serverUrl, "请先配置 sa-token.sso.server-url 地址", SaSsoErrorCode.CODE_30012);
			url = SaFoxUtil.spliceTwoUrl(serverUrl, path);
		}

		// 添加签名等参数，并序列化
		return joinParamMapAndSign(url, paramMap);
	}


	// ------------------- 发起请求 -------------------

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
	 * 给 paramMap 追加 sign 等参数，并序列化为kv字符串，拼接到url后面
	 * @param url 请求地址
	 * @param paramMap 请求原始参数列表
	 * @return 加工后的url
	 */
	public String joinParamMapAndSign(String url, Map<String, Object> paramMap) {
		// 在参数列表中追加：时间戳、随机字符串、参数签名
		SaManager.getSaSignTemplate().addSignParams(paramMap);

		// 将参数列表序列化为kv字符串
		String signParams = SaManager.getSaSignTemplate().joinParams(paramMap);

		// 将kv字符串拼接到url后面
		return SaFoxUtil.joinParam(url, signParams);
	}

	/**
	 * 给 url 拼接 loginId 参数，并拼接 sign 等参数
	 * @param url 链接
	 * @param loginId 账号id
	 * @return 加工后的url
	 */
	public String joinLoginIdAndSign(String url, Object loginId) {
		Map<String, Object> paramMap = new LinkedHashMap<>();
		paramMap.put(paramName.loginId, loginId);
		return joinParamMapAndSign(url, paramMap);
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


	// -------- 以下方法已废弃，仅为兼容旧版本而保留 --------

	/**
	 * 构建URL：Server端 账号资料查询地址
	 * @param loginId 账号id
	 * @return Server端 账号资料查询地址
	 */
	@Deprecated
	public String buildUserinfoUrl(Object loginId) {
		String userinfoUrl = SaSsoManager.getConfig().splicingUserinfoUrl();
		return joinLoginIdAndSign(userinfoUrl, loginId);
	}

	/**
	 * 获取：账号资料
	 * @param loginId 账号id
	 * @return 账号资料
	 */
	@Deprecated
	public Object getUserinfo(Object loginId) {
		String url = buildUserinfoUrl(loginId);
		return request(url);
	}


}
