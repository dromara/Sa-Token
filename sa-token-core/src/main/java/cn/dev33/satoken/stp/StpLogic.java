/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.stp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaCookie;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.*;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.fun.SaTwoParamFunction;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.model.wrapperInfo.SaDisableWrapperInfo;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutMode;
import cn.dev33.satoken.stp.parameter.enums.SaLogoutRange;
import cn.dev33.satoken.stp.parameter.enums.SaReplacedRange;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import cn.dev33.satoken.util.SaValue2Box;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static cn.dev33.satoken.exception.NotLoginException.*;


/**
 * Sa-Token 权限认证，逻辑实现类
 *
 * <p>
 *     Sa-Token 的核心，框架大多数功能均由此类提供具体逻辑实现。
 * </p>
 *
 * @author click33
 * @since 1.10.0
 */
public class StpLogic {

	/**
	 * 账号类型标识，多账号体系时（一个系统多套用户表）用此值区分具体要校验的是哪套用户，比如：login、user、admin
	 */
	public String loginType;

	/**
	 * 初始化 StpLogic, 并指定账号类型
	 *
	 * @param loginType 账号类型标识
	 */
	public StpLogic(String loginType) {
		setLoginType(loginType);
	}

	/**
	 * 获取当前 StpLogic 账号类型标识
	 *
	 * @return /
	 */
	public String getLoginType(){
		return loginType;
	}

	/**
	 * 安全的重置当前账号类型
	 *
	 * @param loginType 账号类型标识
	 * @return 对象自身
	 */
	public StpLogic setLoginType(String loginType){

		// 先清除此 StpLogic 在全局 SaManager 中的记录
		if(SaFoxUtil.isNotEmpty(this.loginType)) {
			SaManager.removeStpLogic(this.loginType);
		}

		// 赋值
		this.loginType = loginType;

		// 将新的 loginType -> StpLogic 映射关系 put 到 SaManager 全局集合中，以便后续根据 LoginType 进行查找此对象
		SaManager.putStpLogic(this);

		return this;
	}

	private SaTokenConfig config;

	/**
	 * 写入当前 StpLogic 单独使用的配置对象
	 *
	 * @param config 配置对象
	 * @return 对象自身
	 */
	public StpLogic setConfig(SaTokenConfig config) {
		this.config = config;
		return this;
	}

	/**
	 * 返回当前 StpLogic 使用的配置对象，如果当前 StpLogic 没有配置，则返回 null
	 *
	 * @return /
	 */
	public SaTokenConfig getConfig() {
		return config;
	}

	/**
	 * 返回当前 StpLogic 使用的配置对象，如果当前 StpLogic 没有配置，则返回全局配置对象
	 *
	 * @return /
	 */
	public SaTokenConfig getConfigOrGlobal() {
		SaTokenConfig cfg = getConfig();
		if(cfg != null) {
			return cfg;
		}
		return SaManager.getConfig();
	}



	// ------------------- 获取 token 相关 -------------------

	/**
	 * 返回 token 名称，此名称在以下地方体现：Cookie 保存 token 时的名称、提交 token 时参数的名称、存储 token 时的 key 前缀
	 *
	 * @return /
	 */
	public String getTokenName() {
		return splicingKeyTokenName();
	}

	/**
	 * 为指定账号创建一个 token （只是把 token 创建出来，并不持久化存储）
	 *
	 * @param loginId 账号id
	 * @param deviceType 设备类型
	 * @param timeout 过期时间
	 * @param extraData 扩展信息
	 * @return 生成的tokenValue
	 */
	public String createTokenValue(Object loginId, String deviceType, long timeout, Map<String, Object> extraData) {
		return SaStrategy.instance.createToken.apply(loginId, loginType);
	}

	/**
	 * 在当前会话写入指定 token 值
	 *
	 * @param tokenValue token 值
	 */
	public void setTokenValue(String tokenValue){
		setTokenValue(tokenValue, createSaLoginParameter());
	}

	/**
	 * 在当前会话写入指定 token 值
	 *
	 * @param tokenValue token 值
	 * @param cookieTimeout Cookie存活时间(秒)
	 */
	public void setTokenValue(String tokenValue, int cookieTimeout){
		setTokenValue(tokenValue, createSaLoginParameter().setTimeout(cookieTimeout));
	}

	/**
	 * 在当前会话写入指定 token 值
	 *
	 * @param tokenValue token 值
	 * @param loginParameter 登录参数
	 */
	public void setTokenValue(String tokenValue, SaLoginParameter loginParameter){

		// 先判断一下，如果提供 token 为空，则不执行任何动作
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return;
		}

		// 1、将 token 写入到当前请求的 Storage 存储器里
		setTokenValueToStorage(tokenValue);

		// 2. 将 token 写入到当前会话的 Cookie 里
		if (getConfigOrGlobal().getIsReadCookie()) {
			setTokenValueToCookie(tokenValue, loginParameter.getCookie(), loginParameter.getCookieTimeout());
		}

		// 3. 将 token 写入到当前请求的响应头中
		if(loginParameter.getIsWriteHeader()) {
			setTokenValueToResponseHeader(tokenValue);
		}
	}

	/**
	 * 将 token 写入到当前请求的 Storage 存储器里
	 *
	 * @param tokenValue 要保存的 token 值
	 */
	public void setTokenValueToStorage(String tokenValue){
		// 1、获取当前请求的 Storage 存储器
		SaStorage storage = SaHolder.getStorage();

		// 2、保存 token
		//	- 如果没有配置前缀模式，直接保存
		// 	- 如果配置了前缀模式，则拼接上前缀保存
		String tokenPrefix = getConfigOrGlobal().getTokenPrefix();
		if( SaFoxUtil.isEmpty(tokenPrefix) ) {
			storage.set(splicingKeyJustCreatedSave(), tokenValue);
		} else {
			storage.set(splicingKeyJustCreatedSave(), tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT + tokenValue);
		}

		// 3、以无前缀的方式再写入一次
		storage.set(SaTokenConsts.JUST_CREATED_NOT_PREFIX, tokenValue);
	}

	/**
	 * 将 token 写入到当前会话的 Cookie 里
	 *
	 * @param tokenValue token 值
	 * @param cookieTimeout Cookie存活时间（单位：秒，填-1代表为内存Cookie，浏览器关闭后消失）
	 */
	public void setTokenValueToCookie(String tokenValue, int cookieTimeout){
		setTokenValueToCookie(tokenValue, null, cookieTimeout);
	}

	/**
	 * 将 token 写入到当前会话的 Cookie 里
	 *
	 * @param tokenValue token 值
	 * @param cookieConfig Cookie 配置项
	 * @param cookieTimeout Cookie存活时间（单位：秒，填-1代表为内存Cookie，浏览器关闭后消失）
	 */
	public void setTokenValueToCookie(String tokenValue, SaCookieConfig cookieConfig, int cookieTimeout){
		if(cookieConfig == null) {
			cookieConfig = getConfigOrGlobal().getCookie();
		}
		SaCookie cookie = new SaCookie()
				.setName(getTokenName())
				.setValue(tokenValue)
				.setMaxAge(cookieTimeout)
				.setDomain(cookieConfig.getDomain())
				.setPath(cookieConfig.getPath())
				.setSecure(cookieConfig.getSecure())
				.setHttpOnly(cookieConfig.getHttpOnly())
				.setSameSite(cookieConfig.getSameSite())
				.setExtraAttrs(cookieConfig.getExtraAttrs())
				;
		SaHolder.getResponse().addCookie(cookie);
	}

	/**
	 * 将 token 写入到当前请求的响应头中
	 *
	 * @param tokenValue token 值
	 */
	public void setTokenValueToResponseHeader(String tokenValue){
		// 写入到响应头
		String tokenName = getTokenName();
		SaResponse response = SaHolder.getResponse();
		response.setHeader(tokenName, tokenValue);

		// 此处必须在响应头里指定 Access-Control-Expose-Headers: token-name，否则前端无法读取到这个响应头
		response.addHeader(SaResponse.ACCESS_CONTROL_EXPOSE_HEADERS, tokenName);
	}

	/**
	 * 获取当前请求的 token 值
	 *
	 * @return 当前tokenValue
	 */
	public String getTokenValue(){
		return getTokenValue(false);
	}

	/**
	 * 获取当前请求的 token 值
	 *
	 * @param noPrefixThrowException 如果提交的 token 不带有指定的前缀，是否抛出异常
	 * @return 当前tokenValue
	 */
	public String getTokenValue(boolean noPrefixThrowException){

		// 1、获取前端提交的 token （包含前缀值）
		String tokenValue = getTokenValueNotCut();

		// 2、如果全局配置打开了前缀模式，则二次处理一下
		String tokenPrefix = getConfigOrGlobal().getTokenPrefix();
		if(SaFoxUtil.isNotEmpty(tokenPrefix)) {

			// 情况2.1：如果提交的 token 为空，则转为 null
			if(SaFoxUtil.isEmpty(tokenValue)) {
				tokenValue = null;
			}

			// 情况2.2：如果 token 有值，但是并不是以指定的前缀开头
			else if(! tokenValue.startsWith(tokenPrefix + SaTokenConsts.TOKEN_CONNECTOR_CHAT)) {
				if(noPrefixThrowException) {
					throw NotLoginException.newInstance(loginType, NO_PREFIX, NO_PREFIX_MESSAGE + "，prefix=" + tokenPrefix, null).setCode(SaErrorCode.CODE_11017);
				} else {
					tokenValue = null;
				}
			}

			// 情况2.3：代码至此，说明 token 有值，且是以指定的前缀开头的，现在裁剪掉前缀
			else {
				tokenValue = tokenValue.substring(tokenPrefix.length() + SaTokenConsts.TOKEN_CONNECTOR_CHAT.length());
			}
		}

		// 3、返回
		return tokenValue;
	}

	/**
	 * 获取当前请求的 token 值 （不裁剪前缀）
	 *
	 * @return /
	 */
	public String getTokenValueNotCut(){

		// 获取相应对象
		SaStorage storage = SaHolder.getStorage();
		SaRequest request = SaHolder.getRequest();
		SaTokenConfig config = getConfigOrGlobal();
		String keyTokenName = getTokenName();
		String tokenValue = null;

		// 1. 先尝试从 Storage 存储器里读取
		if(storage.get(splicingKeyJustCreatedSave()) != null) {
			tokenValue = String.valueOf(storage.get(splicingKeyJustCreatedSave()));
		}
		// 2. 再尝试从 请求体 里面读取
		if(SaFoxUtil.isEmpty(tokenValue) && config.getIsReadBody()){
			tokenValue = request.getParam(keyTokenName);
		}
		// 3. 再尝试从 header 头里读取
		if(SaFoxUtil.isEmpty(tokenValue) && config.getIsReadHeader()){
			tokenValue = request.getHeader(keyTokenName);
		}
		// 4. 最后尝试从 cookie 里读取
		if(SaFoxUtil.isEmpty(tokenValue) && config.getIsReadCookie()){
			tokenValue = request.getCookieValue(keyTokenName);
			if(SaFoxUtil.isNotEmpty(tokenValue) && config.getCookieAutoFillPrefix()) {
				tokenValue = config.getTokenPrefix() + SaTokenConsts.TOKEN_CONNECTOR_CHAT + tokenValue;
			}
		}

		// 5. 至此，不管有没有读取到，都不再尝试了，直接返回
		return tokenValue;
	}

	/**
	 * 获取当前请求的 token 值，如果获取不到则抛出异常
	 *
	 * @return /
	 */
	public String getTokenValueNotNull(){
		String tokenValue = getTokenValue(true);
		if(SaFoxUtil.isEmpty(tokenValue)) {
			throw NotLoginException.newInstance(loginType, NOT_TOKEN, NOT_TOKEN_MESSAGE, null).setCode(SaErrorCode.CODE_11001);
		}
		return tokenValue;
	}

	/**
	 * 获取当前会话的 token 参数信息
	 *
	 * @return token 参数信息
	 */
	public SaTokenInfo getTokenInfo() {
		SaTokenInfo info = new SaTokenInfo();
		info.tokenName = getTokenName();
		info.tokenValue = getTokenValue();
		info.isLogin = isLogin();
		info.loginId = getLoginIdDefaultNull();
		info.loginType = getLoginType();
		info.tokenTimeout = getTokenTimeout();
		info.sessionTimeout = getSessionTimeout();
		info.tokenSessionTimeout = getTokenSessionTimeout();
		info.tokenActiveTimeout = getTokenActiveTimeout();
		info.loginDeviceType = getLoginDeviceType();
		return info;
	}


	// ------------------- 登录相关操作 -------------------

	// --- 登录

	/**
	 * 会话登录
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 */
	public void login(Object id) {
		login(id, createSaLoginParameter());
	}

	/**
	 * 会话登录，并指定登录设备类型
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param deviceType 设备类型
	 */
	public void login(Object id, String deviceType) {
		login(id, createSaLoginParameter().setDeviceType(deviceType));
	}

	/**
	 * 会话登录，并指定是否 [记住我]
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param isLastingCookie 是否为持久Cookie，值为 true 时记住我，值为 false 时关闭浏览器需要重新登录
	 */
	public void login(Object id, boolean isLastingCookie) {
		login(id, createSaLoginParameter().setIsLastingCookie(isLastingCookie));
	}

	/**
	 * 会话登录，并指定此次登录 token 的有效期, 单位:秒
	 *
	 * @param id      账号id，建议的类型：（long | int | String）
	 * @param timeout 此次登录 token 的有效期, 单位:秒
	 */
	public void login(Object id, long timeout) {
		login(id, createSaLoginParameter().setTimeout(timeout));
	}

	/**
	 * 会话登录，并指定所有登录参数 Model
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param loginParameter 此次登录的参数Model
	 */
	public void login(Object id, SaLoginParameter loginParameter) {
		// 1、创建会话
		String token = createLoginSession(id, loginParameter);

		// 2、在当前客户端注入 token
		setTokenValue(token, loginParameter);
	}

	/**
	 * 创建指定账号 id 的登录会话数据
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @return 返回会话令牌
	 */
	public String createLoginSession(Object id) {
		return createLoginSession(id, createSaLoginParameter());
	}

	/**
	 * 创建指定账号 id 的登录会话数据
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @param loginParameter 此次登录的参数Model
	 * @return 返回会话令牌
	 */
	public String createLoginSession(Object id, SaLoginParameter loginParameter) {

		// 1、先检查一下，传入的参数是否有效
		checkLoginArgs(id, loginParameter);

		// 2、给这个账号分配一个可用的 token
		String tokenValue = distUsableToken(id, loginParameter);

		// 3、获取此账号的 Account-Session , 续期
		SaSession session = getSessionByLoginId(id, true, loginParameter.getTimeout());
		session.updateMinTimeout(loginParameter.getTimeout());

		// 4、在 Account-Session 上记录本次登录的终端信息
		SaTerminalInfo terminalInfo = new SaTerminalInfo()
				.setDeviceType(loginParameter.getDeviceType())
				.setDeviceId(loginParameter.getDeviceId())
				.setTokenValue(tokenValue)
				.setExtraData(loginParameter.getTerminalExtraData())
				.setCreateTime(System.currentTimeMillis());
		session.addTerminal(terminalInfo);

		// 5、保存 token -> id 的映射关系，方便日后根据 token 找账号 id
		saveTokenToIdMapping(tokenValue, id, loginParameter.getTimeout());

		// 6、写入这个 token 的最后活跃时间 token-last-active
		if(isOpenCheckActiveTimeout()) {
			setLastActiveToNow(tokenValue, loginParameter.getActiveTimeout(), loginParameter.getTimeout());
		}

		// 7、如果该 token 对应的 Token-Session 已经存在，则需要给其续期
		SaSession tokenSession = getTokenSessionByToken(tokenValue, loginParameter.getRightNowCreateTokenSession());
		if(tokenSession != null) {
			tokenSession.updateMinTimeout(loginParameter.getTimeout());
		}

		// 8、$$ 发布全局事件：账号 xxx 登录成功
		SaTokenEventCenter.doLogin(loginType, id, tokenValue, loginParameter);

		// 9、检查此账号会话数量是否超出最大值，如果超过，则按照登录时间顺序，把最开始登录的给注销掉
		if(loginParameter.getMaxLoginCount() != -1) {
			logoutByMaxLoginCount(id, session, null, loginParameter.getMaxLoginCount(), loginParameter.getOverflowLogoutMode());
		}

		// 10、一切处理完毕，返回会话凭证 token
		return tokenValue;
	}

	/**
	 * 为指定账号 id 的登录操作，分配一个可用的 token
	 *
	 * @param id 账号id
	 * @param loginParameter 此次登录的参数Model
	 * @return 返回 token
	 */
	protected String distUsableToken(Object id, SaLoginParameter loginParameter) {

		// 1、获取全局配置的 isConcurrent 参数
		//    如果配置为：不允许一个账号多地同时登录，则需要先将这个账号的历史登录会话标记为：被顶下线
		if( ! loginParameter.getIsConcurrent()) {
			if(loginParameter.getReplacedRange() == SaReplacedRange.CURR_DEVICE_TYPE) {
				replaced(id, loginParameter.getDeviceType());
			}
			if(loginParameter.getReplacedRange() == SaReplacedRange.ALL_DEVICE_TYPE) {
				replaced(id, createSaLogoutParameter());
			}
		}

		// 2、如果调用者预定了要生成的 token，则直接返回这个预定的值，框架无需再操心了
		if(SaFoxUtil.isNotEmpty(loginParameter.getToken())) {
			return loginParameter.getToken();
		}

		// 3、只有在配置了 [ 允许一个账号多地同时登录 ] 时，才尝试复用旧 token，这样可以避免不必要地查询，节省开销
		if(loginParameter.getIsConcurrent()) {

			// 3.1、如果配置了允许复用旧 token
			if(isSupportShareToken() && loginParameter.getIsShare()) {

				// 根据 账号id + 设备类型，尝试获取旧的 token
				String tokenValue = getTokenValueByLoginId(id, loginParameter.getDeviceType());

				// 如果有值，那就直接复用
				if(SaFoxUtil.isNotEmpty(tokenValue)) {
					return tokenValue;
				}

				// 如果没值，那还是要继续往下走，尝试新建 token
				// ↓↓↓
			}
		}

		// 4、如果代码走到此处，说明未能成功复用旧 token，需要根据算法新建 token
		return SaStrategy.instance.generateUniqueToken.execute(
				"token",
				getConfigOfMaxTryTimes(loginParameter),
				() -> {
					return createTokenValue(id, loginParameter.getDeviceType(), loginParameter.getTimeout(), loginParameter.getExtraData());
				},
				tokenValue -> {
					return getLoginIdNotHandle(tokenValue) == null;
				}
		);
	}

	/**
	 * 校验登录时的参数有效性，如果有问题会打印警告或抛出异常
	 *
	 * @param id 账号id
	 * @param loginParameter 此次登录的参数Model
	 */
	protected void checkLoginArgs(Object id, SaLoginParameter loginParameter) {

		// 1、账号 id 不能为空
		if(SaFoxUtil.isEmpty(id)) {
			throw new SaTokenException("loginId 不能为空").setCode(SaErrorCode.CODE_11002);
		}

		// 2、账号 id 不能是异常标记值
		if(NotLoginException.ABNORMAL_LIST.contains(id.toString())) {
			throw new SaTokenException("loginId 不能为以下值：" + NotLoginException.ABNORMAL_LIST);
		}

		// 3、账号 id 不能是复杂类型
		if( ! SaFoxUtil.isBasicType(id.getClass())) {
			SaManager.log.warn("loginId 应该为简单类型，例如：String | int | long，不推荐使用复杂类型：" + id.getClass());
		}

		// 4、判断当前 StpLogic 是否支持 extra 扩展参数
		if( ! isSupportExtra()) {
			// 如果不支持，开发者却传入了 extra 扩展参数，那么就打印警告信息
			if(loginParameter.haveExtraData()) {
				SaManager.log.warn("当前 StpLogic 不支持 extra 扩展参数模式，传入的 extra 参数将被忽略");
			}
		}

		// 5、如果全局配置未启动动态 activeTimeout 功能，但是此次登录却传入了 activeTimeout 参数，那么就打印警告信息
		if( ! getConfigOrGlobal().getDynamicActiveTimeout() && loginParameter.getActiveTimeout() != null) {
			SaManager.log.warn("当前全局配置未开启动态 activeTimeout 功能，传入的 activeTimeout 参数将被忽略");
		}

	}

	/**
	 * 获取指定账号 id 的登录会话数据，如果获取不到则创建并返回
	 *
	 * @param id 账号id，建议的类型：（long | int | String）
	 * @return 返回会话令牌
	 */
	public String getOrCreateLoginSession(Object id) {
		String tokenValue = getTokenValueByLoginId(id);
		if(tokenValue == null) {
			tokenValue = createLoginSession(id, createSaLoginParameter());
		}
		return tokenValue;
	}

	// --- 注销 (根据 token)

	/**
	 * 在当前客户端会话注销
	 */
	public void logout() {
		logout(createSaLogoutParameter());
	}

	/**
	 * 在当前客户端会话注销，根据注销参数
	 */
	public void logout(SaLogoutParameter logoutParameter) {
		// 1、如果本次请求连 Token 都没有提交，那么它本身也不属于登录状态，此时无需执行任何操作
		String tokenValue = getTokenValue();
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return;
		}

		// 2、如果打开了 Cookie 模式，则先把 Cookie 数据清除掉
		if(getConfigOrGlobal().getIsReadCookie()){
			SaCookieConfig cfg = getConfigOrGlobal().getCookie();
			SaCookie cookie = new SaCookie()
					.setName(getTokenName())
					.setValue(null)
					// 有效期指定为0，做到以增代删
					.setMaxAge(0)
					.setDomain(cfg.getDomain())
					.setPath(cfg.getPath())
					.setSecure(cfg.getSecure())
					.setHttpOnly(cfg.getHttpOnly())
					.setSameSite(cfg.getSameSite())
					;
			SaHolder.getResponse().addCookie(cookie);
		}

		// 3、然后从当前 Storage 存储器里删除 Token
		SaStorage storage = SaHolder.getStorage();
		storage.delete(splicingKeyJustCreatedSave());

		// 4、清除当前上下文的 [ 活跃度校验 check 标记 ]
		storage.delete(SaTokenConsts.TOKEN_ACTIVE_TIMEOUT_CHECKED_KEY);

		// 5、清除这个 token 的其它相关信息
		if(logoutParameter.getRange() == SaLogoutRange.TOKEN) {
			logoutByTokenValue(tokenValue, logoutParameter);
		} else {
			Object loginId = getLoginIdByTokenNotThinkFreeze(tokenValue);
			if(loginId != null) {
				if(!logoutParameter.getIsKeepFreezeOps() && isFreeze(tokenValue)) {
					return;
				}
				logout(loginId, logoutParameter);
			}
		}
	}

	/**
	 * 注销下线，根据指定 token
	 *
	 * @param tokenValue 指定 token
	 */
	public void logoutByTokenValue(String tokenValue) {
		logoutByTokenValue(tokenValue, createSaLogoutParameter());
	}

	/**
	 * 注销下线，根据指定 token、注销参数
	 *
	 * @param tokenValue 指定 token
	 * @param logoutParameter /
	 */
	public void logoutByTokenValue(String tokenValue, SaLogoutParameter logoutParameter) {
		_logoutByTokenValue(tokenValue, logoutParameter.setMode(SaLogoutMode.LOGOUT));
	}

	/**
	 * 踢人下线，根据指定 token
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-5 </p>
	 *
	 * @param tokenValue 指定 token
	 */
	public void kickoutByTokenValue(String tokenValue) {
		kickoutByTokenValue(tokenValue, createSaLogoutParameter());
	}

	/**
	 * 踢人下线，根据指定 token、注销参数
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-5 </p>
	 *
	 * @param tokenValue 指定 token
	 * @param logoutParameter 注销参数
	 */
	public void kickoutByTokenValue(String tokenValue, SaLogoutParameter logoutParameter) {
		_logoutByTokenValue(tokenValue, logoutParameter.setMode(SaLogoutMode.KICKOUT));
	}

	/**
	 * 顶人下线，根据指定 token
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-4 </p>
	 *
	 * @param tokenValue 指定 token
	 */
	public void replacedByTokenValue(String tokenValue) {
		replacedByTokenValue(tokenValue, createSaLogoutParameter());
	}

	/**
	 * 顶人下线，根据指定 token、注销参数
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-4 </p>
	 *
	 * @param tokenValue 指定 token
	 * @param logoutParameter /
	 */
	public void replacedByTokenValue(String tokenValue, SaLogoutParameter logoutParameter) {
		_logoutByTokenValue(tokenValue, logoutParameter.setMode(SaLogoutMode.REPLACED));
	}

	/**
	 * [work] 注销下线，根据指定 token 、注销参数
	 *
	 * @param tokenValue 指定 token
	 * @param logoutParameter 注销参数
	 */
	public void _logoutByTokenValue(String tokenValue, SaLogoutParameter logoutParameter) {

		// 1、判断一下：如果此 token 映射的是一个无效 loginId，则此处立即返回，不需要再往下处理了
		// 		如果不提前截止，则后续的操作可能会写入意外数据
		Object loginId = getLoginIdByTokenNotThinkFreeze(tokenValue);
		if( SaFoxUtil.isEmpty(loginId) ) {
			return;
		}
		if(!logoutParameter.getIsKeepFreezeOps() && isFreeze(tokenValue)) {
			return;
		}

		// 2、清除这个 token 的最后活跃时间记录
		if(isOpenCheckActiveTimeout()) {
			clearLastActive(tokenValue);
		}

		// 3、清除 Token-Session
		if( ! logoutParameter.getIsKeepTokenSession()) {
			deleteTokenSession(tokenValue);
		}

		// 4、清理或更改 Token 映射
		// 5、发布事件通知
		// 		SaLogoutMode.LOGOUT：注销下线
		if(logoutParameter.getMode() == SaLogoutMode.LOGOUT) {
			deleteTokenToIdMapping(tokenValue);
			SaTokenEventCenter.doLogout(loginType, loginId, tokenValue);
		}
		// 		SaLogoutMode.LOGOUT：踢人下线
		if(logoutParameter.getMode() == SaLogoutMode.KICKOUT) {
			updateTokenToIdMapping(tokenValue, NotLoginException.KICK_OUT);
			SaTokenEventCenter.doKickout(loginType, loginId, tokenValue);
		}
		//		SaLogoutMode.REPLACED：顶人下线
		if(logoutParameter.getMode() == SaLogoutMode.REPLACED) {
			updateTokenToIdMapping(tokenValue, NotLoginException.BE_REPLACED);
			SaTokenEventCenter.doReplaced(loginType, loginId, tokenValue);
		}

		// 6、清理这个账号的 Account-Session 上的 terminal 信息，并且尝试注销掉 Account-Session
		SaSession session = getSessionByLoginId(loginId, false);
		if(session != null) {
			session.removeTerminal(tokenValue);
			session.logoutByTerminalCountToZero();
		}
	}

	// --- 注销 (根据 loginId)

	/**
	 * 会话注销，根据账号id
	 *
	 * @param loginId 账号id
	 */
	public void logout(Object loginId) {
		logout(loginId, createSaLogoutParameter());
	}

	/**
	 * 会话注销，根据账号id 和 设备类型
	 *
	 * @param loginId 账号id
	 * @param deviceType 设备类型 (填 null 代表注销该账号的所有设备类型)
	 */
	public void logout(Object loginId, String deviceType) {
		logout(loginId, createSaLogoutParameter().setDeviceType(deviceType));
	}

	/**
	 * 会话注销，根据账号id 和 注销参数
	 *
	 * @param loginId 账号id
	 * @param logoutParameter 注销参数
	 */
	public void logout(Object loginId, SaLogoutParameter logoutParameter) {
		_logout(loginId, logoutParameter.setMode(SaLogoutMode.LOGOUT));
	}

	/**
	 * 踢人下线，根据账号id
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-5 </p>
	 *
	 * @param loginId 账号id
	 */
	public void kickout(Object loginId) {
		kickout(loginId, createSaLogoutParameter());
	}

	/**
	 * 踢人下线，根据账号id 和 设备类型
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-5 </p>
	 *
	 * @param loginId 账号id
	 * @param deviceType 设备类型 (填 null 代表踢出该账号的所有设备类型)
	 */
	public void kickout(Object loginId, String deviceType) {
		kickout(loginId, createSaLogoutParameter().setDeviceType(deviceType));
	}

	/**
	 * 踢人下线，根据账号id 和 注销参数
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-5 </p>
	 *
	 * @param loginId 账号id
	 * @param logoutParameter 注销参数
	 */
	public void kickout(Object loginId, SaLogoutParameter logoutParameter) {
		_logout(loginId, logoutParameter.setMode(SaLogoutMode.KICKOUT));
	}

	/**
	 * 顶人下线，根据账号id
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-4 </p>
	 *
	 * @param loginId 账号id
	 */
	public void replaced(Object loginId) {
		replaced(loginId, createSaLogoutParameter());
	}

	/**
	 * 顶人下线，根据账号id 和 设备类型
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-4 </p>
	 *
	 * @param loginId 账号id
	 * @param deviceType 设备类型 （填 null 代表顶替该账号的所有设备类型）
	 */
	public void replaced(Object loginId, String deviceType) {
		replaced(loginId, createSaLogoutParameter().setDeviceType(deviceType));
	}

	/**
	 * 顶人下线，根据账号id 和 注销参数
	 * <p> 当对方再次访问系统时，会抛出 NotLoginException 异常，场景值=-4 </p>
	 *
	 * @param loginId 账号id
	 * @param logoutParameter 注销参数
	 */
	public void replaced(Object loginId, SaLogoutParameter logoutParameter) {
		_logout(loginId, logoutParameter.setMode(SaLogoutMode.REPLACED));
	}

	/**
	 * [work] 会话注销，根据账号id 和 注销参数
	 *
	 * @param loginId 账号id
	 * @param logoutParameter 注销参数
	 */
	public void _logout(Object loginId, SaLogoutParameter logoutParameter) {
		// 1、获取此账号的 Account-Session，上面记录了此账号的所有登录客户端数据
		SaSession session = getSessionByLoginId(loginId, false);
		if(session != null) {

			// 2、遍历此 SaTerminalInfo 客户端列表，清除相关数据
			List<SaTerminalInfo> terminalList = session.terminalListCopy();
			for (SaTerminalInfo terminal: terminalList) {
				// 不符合 deviceType 的跳过
				if( ! SaFoxUtil.isEmpty(logoutParameter.getDeviceType()) && ! logoutParameter.getDeviceType().equals(terminal.getDeviceType())) {
					continue;
				}
				// 不符合 deviceId 的跳过
				if( ! SaFoxUtil.isEmpty(logoutParameter.getDeviceId()) && ! logoutParameter.getDeviceId().equals(terminal.getDeviceId())) {
					continue;
				}
				_removeTerminal(session, terminal, logoutParameter);
			}

			// 3、如果代码走到这里的时候，此账号已经没有客户端在登录了，则直接注销掉这个 Account-Session
			if(logoutParameter.getMode() == SaLogoutMode.REPLACED) {
				// 因为调用顶替下线时，一般都是在新客户端正在登录，所以此种情况不需要清除该账号的 Account-Session
				// 如果清除了 Account-Session，将可能导致 Account-Session 被注销后又立刻创建出来，造成不必要的性能浪费
			} else {
				session.logoutByTerminalCountToZero();
			}
		}
	}

	// --- 注销 (会话管理辅助方法)

	/**
	 * 在 Account-Session 上移除 Terminal 信息 (注销下线方式)
	 * @param session /
	 * @param terminal /
	 */
	public void removeTerminalByLogout(SaSession session, SaTerminalInfo terminal) {
		_removeTerminal(session, terminal, createSaLogoutParameter().setMode(SaLogoutMode.LOGOUT));
	}

	/**
	 * 在 Account-Session 上移除 Terminal 信息 (踢人下线方式)
	 * @param session /
	 * @param terminal /
	 */
	public void removeTerminalByKickout(SaSession session, SaTerminalInfo terminal) {
		_removeTerminal(session, terminal, createSaLogoutParameter().setMode(SaLogoutMode.KICKOUT));
	}

	/**
	 * 在 Account-Session 上移除 Terminal 信息 (顶人下线方式)
	 * @param session /
	 * @param terminal /
	 */
	public void removeTerminalByReplaced(SaSession session, SaTerminalInfo terminal) {
		_removeTerminal(session, terminal, createSaLogoutParameter().setMode(SaLogoutMode.REPLACED));
	}

	/**
	 * 在 Account-Session 上移除 Terminal 信息 (内部方法，仅为减少重复代码，外部调用意义不大)
	 * @param session Account-Session
	 * @param terminal 设备信息
	 * @param logoutParameter 注销参数
	 */
	public void _removeTerminal(SaSession session, SaTerminalInfo terminal, SaLogoutParameter logoutParameter) {

		Object loginId = session.getLoginId();
		String tokenValue = terminal.getTokenValue();

		// 1、从 Account-Session 上清除此设备信息
		session.removeTerminal(tokenValue);

		// 2、清除这个 token 的最后活跃时间记录
		if(isOpenCheckActiveTimeout()) {
			clearLastActive(tokenValue);
		}

		// 3、清除这个 token 的 Token-Session 对象
		if( ! logoutParameter.getIsKeepTokenSession()) {
			deleteTokenSession(tokenValue);
		}

		// 4、清理或更改 Token 映射
		// 5、发布事件通知
		// 		SaLogoutMode.LOGOUT：注销下线
		if(logoutParameter.getMode() == SaLogoutMode.LOGOUT) {
			deleteTokenToIdMapping(tokenValue);
			SaTokenEventCenter.doLogout(loginType, loginId, tokenValue);
		}
		// 		SaLogoutMode.LOGOUT：踢人下线
		if(logoutParameter.getMode() == SaLogoutMode.KICKOUT) {
			updateTokenToIdMapping(tokenValue, NotLoginException.KICK_OUT);
			SaTokenEventCenter.doKickout(loginType, loginId, tokenValue);
		}
		//		SaLogoutMode.REPLACED：顶人下线
		if(logoutParameter.getMode() == SaLogoutMode.REPLACED) {
			updateTokenToIdMapping(tokenValue, NotLoginException.BE_REPLACED);
			SaTokenEventCenter.doReplaced(loginType, loginId, tokenValue);
		}
	}

	/**
	 * 如果指定账号 id、设备类型的登录客户端已经超过了指定数量，则按照登录时间顺序，把最开始登录的给注销掉
	 *
	 * @param loginId 账号id
	 * @param session 此账号的 Account-Session 对象，可填写 null，框架将自动获取
	 * @param deviceType 设备类型（填 null 代表注销此账号所有设备类型的登录）
	 * @param maxLoginCount 最大登录数量，超过此数量的将被注销
	 * @param logoutMode 超出的客户端将以何种方式被注销
	 */
	public void logoutByMaxLoginCount(Object loginId, SaSession session, String deviceType, int maxLoginCount, SaLogoutMode logoutMode) {

		// 1、如果调用者提供的  Account-Session 对象为空，则我们先手动获取一下
		if(session == null) {
			session = getSessionByLoginId(loginId, false);
			if(session == null) {
				return;
			}
		}

		// 2、获取这个账号指定设备类型下的所有登录客户端
		List<SaTerminalInfo> list = session.getTerminalListByDeviceType(deviceType);

		// 3、按照登录时间倒叙，超过 maxLoginCount 数量的，全部注销掉
		for (int i = 0; i < list.size() - maxLoginCount; i++) {
			_removeTerminal(session, list.get(i), createSaLogoutParameter().setMode(logoutMode));
		}

		// 4、如果代码走到这里的时候，此账号已经没有客户端在登录了，则直接注销掉这个 Account-Session
		session.logoutByTerminalCountToZero();
	}


	// ---- 会话查询

	/**
	 * 判断当前会话是否已经登录
	 *
	 * @return 已登录返回 true，未登录返回 false
	 */
	public boolean isLogin() {
		// 判断条件：
		// 		1、获取到的 loginId 不为 null，
		// 		2、并且不在异常项集合里（此项在 getLoginIdDefaultNull() 方法里完成判断）
		return getLoginIdDefaultNull() != null;
	}

	/**
	 * 判断指定账号是否已经登录
	 *
	 * @return 已登录返回 true，未登录返回 false
	 */
	public boolean isLogin(Object loginId) {
		// 判断条件：能否根据 loginId 查询到对应的 terminal 值
		return !getTerminalListByLoginId(loginId, null).isEmpty();
	}

	/**
	 * 检验当前会话是否已经登录，如未登录，则抛出异常
	 */
	public void checkLogin() {
		// 效果与 getLoginId() 相同，只是 checkLogin() 更加语义化一些
		getLoginId();
	}

	/**
	 * 获取当前会话账号id，如果未登录，则抛出异常
	 *
	 * @return 账号id
	 */
	public Object getLoginId() {

		// 1、先判断一下当前会话是否正在 [ 临时身份切换 ], 如果是则返回临时身份
		if(isSwitch()) {
			return getSwitchLoginId();
		}

		// 2、如果前端没有提交 token，则抛出异常: 未能读取到有效 token
		String tokenValue = getTokenValue(true);
		if(SaFoxUtil.isEmpty(tokenValue)) {
			throw NotLoginException.newInstance(loginType, NOT_TOKEN, NOT_TOKEN_MESSAGE, null).setCode(SaErrorCode.CODE_11011);
		}

		// 3、查找此 token 对应的 loginId，如果找不到则抛出：token 无效
		String loginId = getLoginIdNotHandle(tokenValue);
		if(SaFoxUtil.isEmpty(loginId)) {
			throw NotLoginException.newInstance(loginType, INVALID_TOKEN, INVALID_TOKEN_MESSAGE, tokenValue).setCode(SaErrorCode.CODE_11012);
		}

		// 4、如果这个 token 指向的是值是：过期标记，则抛出：token 已过期
		if(loginId.equals(NotLoginException.TOKEN_TIMEOUT)) {
			throw NotLoginException.newInstance(loginType, TOKEN_TIMEOUT, TOKEN_TIMEOUT_MESSAGE, tokenValue).setCode(SaErrorCode.CODE_11013);
		}

		// 5、如果这个 token 指向的是值是：被顶替标记，则抛出：token 已被顶下线
		if(loginId.equals(NotLoginException.BE_REPLACED)) {
			throw NotLoginException.newInstance(loginType, BE_REPLACED, BE_REPLACED_MESSAGE, tokenValue).setCode(SaErrorCode.CODE_11014);
		}

		// 6、如果这个 token 指向的是值是：被踢下线标记，则抛出：token 已被踢下线
		if(loginId.equals(NotLoginException.KICK_OUT)) {
			throw NotLoginException.newInstance(loginType, KICK_OUT, KICK_OUT_MESSAGE, tokenValue).setCode(SaErrorCode.CODE_11015);
		}

		// 7、token 活跃频率检查
		checkActiveTimeoutByConfig(tokenValue);

		// ------ 至此，loginId 已经是一个合法的值，代表当前会话是一个正常的登录状态了

		// 8、返回 loginId
		return loginId;
	}

	/**
	 * 获取当前会话账号id, 如果未登录，则返回默认值
	 *
	 * @param <T> 返回类型
	 * @param defaultValue 默认值
	 * @return 登录id
	 */
	@SuppressWarnings("unchecked")
	public <T> T getLoginId(T defaultValue) {
		// 1、先正常获取一下当前会话的 loginId
		Object loginId = getLoginIdDefaultNull();

		// 2、如果 loginId 为 null，则返回默认值
		if(loginId == null) {
			return defaultValue;
		}
		// 3、loginId 不为 null，则开始尝试类型转换
		if(defaultValue == null) {
			return (T) loginId;
		}
		return (T) SaFoxUtil.getValueByType(loginId, defaultValue.getClass());
	}

	/**
	 * 获取当前会话账号id, 如果未登录，则返回null
	 *
	 * @return 账号id
	 */
	public Object getLoginIdDefaultNull() {

		// 1、先判断一下当前会话是否正在 [ 临时身份切换 ], 如果是则返回临时身份
		if(isSwitch()) {
			return getSwitchLoginId();
		}

		// 2、如果前端连 token 都没有提交，则直接返回 null
		String tokenValue = getTokenValue();
		if(tokenValue == null) {
			return null;
		}

		// 3、根据 token 找到对应的 loginId，如果 loginId 为 null 或者属于异常标记里面，均视为未登录, 统一返回 null
		Object loginId = getLoginIdNotHandle(tokenValue);
		if( ! isValidLoginId(loginId) ) {
			return null;
		}

		// 4、如果 token 已被冻结，也返回 null
		if(getTokenActiveTimeoutByToken(tokenValue) == SaTokenDao.NOT_VALUE_EXPIRE) {
			return null;
		}

		// 5、执行到此，证明此 loginId 已经是个正常合法的账号id了，可以返回
		return loginId;
	}

	/**
	 * 获取当前会话账号id, 并转换为 String 类型
	 *
	 * @return 账号id
	 */
	public String getLoginIdAsString() {
		return String.valueOf(getLoginId());
	}

	/**
	 * 获取当前会话账号id, 并转换为 int 类型
	 *
	 * @return 账号id
	 */
	public int getLoginIdAsInt() {
		return Integer.parseInt(String.valueOf(getLoginId()));
	}

	/**
	 * 获取当前会话账号id, 并转换为 long 类型
	 *
	 * @return 账号id
	 */
	public long getLoginIdAsLong() {
		return Long.parseLong(String.valueOf(getLoginId()));
	}

	/**
	 * 获取指定 token 对应的账号id，如果 token 无效或 token 处于被踢、被顶、被冻结等状态，则返回 null
	 *
	 * @param tokenValue token
	 * @return 账号id
	 */
	public Object getLoginIdByToken(String tokenValue) {

		Object loginId = getLoginIdByTokenNotThinkFreeze(tokenValue);

		if( SaFoxUtil.isNotEmpty(loginId) ) {
			// 如果 token 已被冻结，也返回 null
			long activeTimeout = getTokenActiveTimeoutByToken(tokenValue);
			if(activeTimeout == SaTokenDao.NOT_VALUE_EXPIRE) {
				return null;
			}
		}

		return loginId;
	}

	/**
	 * 获取指定 token 对应的账号id，如果 token 无效或 token 处于被踢、被顶等状态 (不考虑被冻结)，则返回 null
	 *
	 * @param tokenValue token
	 * @return 账号id
	 */
	public Object getLoginIdByTokenNotThinkFreeze(String tokenValue) {

		// 1、如果提供的 token 为空，则直接返回 null
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return null;
		}

		// 2、查找此 token 对应的 loginId，如果找不到或找的到但属于无效值，则返回 null
		String loginId = getLoginIdNotHandle(tokenValue);
		if( ! isValidLoginId(loginId) ) {
			return null;
		}

		// 3、返回
		return loginId;
	}

	/**
	 * 获取指定 token 对应的账号id （不做任何特殊处理）
	 *
	 * @param tokenValue token 值
	 * @return 账号id
	 */
	public String getLoginIdNotHandle(String tokenValue) {
		return getSaTokenDao().get(splicingKeyTokenValue(tokenValue));
	}

	/**
	 * 获取当前 Token 的扩展信息（此函数只在jwt模式下生效）
	 *
	 * @param key 键值
	 * @return 对应的扩展数据
	 */
	public Object getExtra(String key) {
		throw new ApiDisabledException("只有在集成 sa-token-jwt 插件后才可以使用 extra 扩展参数").setCode(SaErrorCode.CODE_11031);
	}

	/**
	 * 获取指定 Token 的扩展信息（此函数只在jwt模式下生效）
	 *
	 * @param tokenValue 指定的 Token 值
	 * @param key 键值
	 * @return 对应的扩展数据
	 */
	public Object getExtra(String tokenValue, String key) {
		throw new ApiDisabledException("只有在集成 sa-token-jwt 插件后才可以使用 extra 扩展参数").setCode(SaErrorCode.CODE_11031);
	}

	// ---- 其它操作

	/**
	 * 判断一个 loginId 是否是有效的 (判断标准：不为 null、空字符串，且不在异常标记项里面)
	 *
	 * @param loginId 账号id
	 * @return /
	 */
	public boolean isValidLoginId(Object loginId) {
		return SaFoxUtil.isNotEmpty(loginId) && !NotLoginException.ABNORMAL_LIST.contains(loginId.toString());
	}

	/**
	 * 判断一个 token 是否是有效的 (判断标准：使用此 token 查询到的 loginId 不为 Empty )
	 *
	 * @param tokenValue /
	 * @return /
	 */
	public boolean isValidToken(String tokenValue) {
		Object loginId = getLoginIdByToken(tokenValue);
		return SaFoxUtil.isNotEmpty(loginId);
	}

	/**
	 * 存储 token - id 映射关系
	 *
	 * @param tokenValue token值
	 * @param loginId 账号id
	 * @param timeout 会话有效期 (单位: 秒)
	 */
	public void saveTokenToIdMapping(String tokenValue, Object loginId, long timeout) {
		getSaTokenDao().set(splicingKeyTokenValue(tokenValue), String.valueOf(loginId), timeout);
	}

	/**
	 * 更改 token - id 映射关系
	 *
	 * @param tokenValue token值
	 * @param loginId 新的账号Id值
	 */
	public void updateTokenToIdMapping(String tokenValue, Object loginId) {
		// 先判断一下，是否传入了空值
		SaTokenException.notTrue(SaFoxUtil.isEmpty(loginId), "loginId 不能为空", SaErrorCode.CODE_11003);

		// 更新缓存中的 token 指向
		getSaTokenDao().update(splicingKeyTokenValue(tokenValue), loginId.toString());
	}

	/**
	 * 删除 token - id 映射
	 *
	 * @param tokenValue token值
	 */
	public void deleteTokenToIdMapping(String tokenValue) {
		getSaTokenDao().delete(splicingKeyTokenValue(tokenValue));
	}


	// ------------------- Account-Session 相关 -------------------

	/**
	 * 获取指定 key 的 SaSession, 如果该 SaSession 尚未创建，isCreate = 是否立即新建并返回
	 *
	 * @param sessionId SessionId
	 * @param isCreate 是否新建
	 * @param timeout 如果这个 SaSession 是新建的，则使用此值作为过期值（单位：秒），可填 null，代表使用全局 timeout 值
	 * @param appendOperation 如果这个 SaSession 是新建的，则要追加执行的动作，可填 null，代表无追加动作
	 * @return Session对象
	 */
	public SaSession getSessionBySessionId(String sessionId, boolean isCreate, Long timeout, Consumer<SaSession> appendOperation) {

		// 如果提供的 sessionId 为 null，则直接返回 null
		if(SaFoxUtil.isEmpty(sessionId)) {
			throw new SaTokenException("SessionId 不能为空").setCode(SaErrorCode.CODE_11072);
		}

		// 先检查这个 SaSession 是否已经存在，如果不存在且 isCreate=true，则新建并返回
		SaSession session = getSaTokenDao().getSession(sessionId);

		if(session == null && isCreate) {
			// 创建这个 SaSession
			session = SaStrategy.instance.createSession.apply(sessionId);

			// 追加操作
			if(appendOperation != null) {
				appendOperation.accept(session);
			}

			// 如果未提供 timeout，则根据相应规则设定默认的 timeout
			if(timeout == null) {
				// 如果是 Token-Session，则使用对用 token 的有效期，使 token 和 token-session 保持相同ttl，同步失效
				if(SaTokenConsts.SESSION_TYPE__TOKEN.equals(session.getType())) {
					timeout = getTokenTimeout(session.getToken());
					if(timeout == SaTokenDao.NOT_VALUE_EXPIRE) {
						timeout = getConfigOrGlobal().getTimeout();
					}
				} else {
					// 否则使用全局配置的 timeout
					timeout = getConfigOrGlobal().getTimeout();
				}
			}

			// 将这个 SaSession 入库
			getSaTokenDao().setSession(session, timeout);
		}
		return session;
	}

	/**
	 * 获取指定 key 的 SaSession, 如果该 SaSession 尚未创建，则返回 null
	 *
	 * @param sessionId SessionId
	 * @return Session对象
	 */
	public SaSession getSessionBySessionId(String sessionId) {
		return getSessionBySessionId(sessionId, false, null, null);
	}

	/**
	 * 获取指定账号 id 的 Account-Session, 如果该 SaSession 尚未创建，isCreate=是否新建并返回
	 *
	 * @param loginId 账号id
	 * @param isCreate 是否新建
	 * @param timeout 如果这个 SaSession 是新建的，则使用此值作为过期值（单位：秒），可填 null，代表使用全局 timeout 值
	 * @return SaSession 对象
	 */
	public SaSession getSessionByLoginId(Object loginId, boolean isCreate, Long timeout) {
		if(SaFoxUtil.isEmpty(loginId)) {
			throw new SaTokenException("Account-Session 获取失败：loginId 不能为空");
		}
		return getSessionBySessionId(splicingKeySession(loginId), isCreate, timeout, session -> {
			// 这里是该 Account-Session 首次创建时才会被执行的方法：
			// 		设定这个 SaSession 的各种基础信息：类型、账号体系、账号id
			session.setType(SaTokenConsts.SESSION_TYPE__ACCOUNT);
			session.setLoginType(getLoginType());
			session.setLoginId(loginId);
		});
	}

	/**
	 * 获取指定账号 id 的 Account-Session, 如果该 SaSession 尚未创建，isCreate=是否新建并返回
	 *
	 * @param loginId 账号id
	 * @param isCreate 是否新建
	 * @return SaSession 对象
	 */
	public SaSession getSessionByLoginId(Object loginId, boolean isCreate) {
		return getSessionByLoginId(loginId, isCreate, null);
	}

	/**
	 * 获取指定账号 id 的 Account-Session，如果该 SaSession 尚未创建，则新建并返回
	 *
	 * @param loginId 账号id
	 * @return SaSession 对象
	 */
	public SaSession getSessionByLoginId(Object loginId) {
		return getSessionByLoginId(loginId, true, null);
	}

	/**
	 * 获取当前已登录账号的 Account-Session, 如果该 SaSession 尚未创建，isCreate=是否新建并返回
	 *
	 * @param isCreate 是否新建
	 * @return Session对象
	 */
	public SaSession getSession(boolean isCreate) {
		return getSessionByLoginId(getLoginId(), isCreate);
	}

	/**
	 * 获取当前已登录账号的 Account-Session，如果该 SaSession 尚未创建，则新建并返回
	 *
	 * @return Session对象
	 */
	public SaSession getSession() {
		return getSession(true);
	}


	// ------------------- Token-Session 相关 -------------------

	/**
	 * 获取指定 token 的 Token-Session，如果该 SaSession 尚未创建，isCreate代表是否新建并返回
	 *
	 * @param tokenValue token值
	 * @param isCreate 是否新建
	 * @return session对象
	 */
	public SaSession getTokenSessionByToken(String tokenValue, boolean isCreate) {
		// 1、token 为空，不允许创建
		if(SaFoxUtil.isEmpty(tokenValue)) {
			throw new SaTokenException("Token-Session 获取失败：token 为空").setCode(SaErrorCode.CODE_11073);
		}

		// 2、如果能查询到旧记录，则直接返回
		String sessionId = splicingKeyTokenSession(tokenValue);
		SaSession tokenSession = getSaTokenDao().getSession(sessionId);
		if(tokenSession != null) {
			return tokenSession;
		}
		// 以下是查不到的情况

		// 3、指定了不需要创建，返回 null
		if( ! isCreate) {
			return null;
		}
		// 以下是需要创建的情况

		// 4、检查一下这个 token 是否为有效 token，无效 token 不允许创建
		if(getConfigOrGlobal().getTokenSessionCheckLogin() && ! isValidToken(tokenValue)) {
			throw new SaTokenException("Token-Session 获取失败，token 无效: " + tokenValue).setCode(SaErrorCode.CODE_11074);
		}

		// 5、创建 Token-Session 并返回
		return getSessionBySessionId(sessionId, true, null, session -> {
			// 这里是该 Token-Session 首次创建时才会被执行的方法：
			// 		设定这个 SaSession 的各种基础信息：类型、账号体系、Token 值
			session.setType(SaTokenConsts.SESSION_TYPE__TOKEN);
			session.setLoginType(getLoginType());
			session.setToken(tokenValue);
		});
	}

	/**
	 * 获取指定 token 的 Token-Session，如果该 SaSession 尚未创建，则新建并返回
	 *
	 * @param tokenValue Token值
	 * @return Session对象
	 */
	public SaSession getTokenSessionByToken(String tokenValue) {
		return getTokenSessionByToken(tokenValue, true);
	}

	/**
	 * 获取当前 token 的 Token-Session，如果该 SaSession 尚未创建，isCreate代表是否新建并返回
	 *
	 * @param isCreate 是否新建
	 * @return Session对象
	 */
	public SaSession getTokenSession(boolean isCreate) {
		String tokenValue = getTokenValue();
		checkActiveTimeoutByConfig(tokenValue);
		return getTokenSessionByToken(tokenValue, isCreate);
	}

	/**
	 * 获取当前 token 的 Token-Session，如果该 SaSession 尚未创建，则新建并返回
	 *
	 * @return Session对象
	 */
	public SaSession getTokenSession() {
		return getTokenSession(true);
	}

	/**
	 * 获取当前匿名 Token-Session （可在未登录情况下使用的 Token-Session）
	 *
	 * @param isCreate 在 Token-Session 尚未创建的情况是否新建并返回
	 * @return Token-Session 对象
	 */
	public SaSession getAnonTokenSession(boolean isCreate) {
		/*
		 * 情况1、如果调用方提供了有效 Token，则：直接返回其 [Token-Session]
		 * 情况2、如果调用方提供了无效 Token，或根本没有提供 Token，则：创建新 Token -> 返回 [ Token-Session ]
		 */
		String tokenValue = getTokenValue();

		// q1 —— 判断这个 Token 是否有效，两个条件符合其一即可：
		/*
		 * 条件1、能查出 Token-Session
		 * 条件2、能查出 LoginId
		 */
		if(SaFoxUtil.isNotEmpty(tokenValue)) {

			// 符合条件1
			SaSession session = getTokenSessionByToken(tokenValue, false);
			if(session != null) {
				return session;
			}

			// 符合条件2
			String loginId = getLoginIdNotHandle(tokenValue);
			if(isValidLoginId(loginId)) {
				return getTokenSessionByToken(tokenValue, isCreate);
			}
		}

		// q2 —— 此时q2分两种情况：
		/*
		 * 情况 2.1、isCreate=true：说明调用方想让框架帮其创建一个 SaSession，那框架就创建并返回
		 * 情况 2.2、isCreate=false：说明调用方并不想让框架帮其创建一个 SaSession，那框架就直接返回 null
		 */
		if(isCreate) {
			// 随机创建一个 Token
			tokenValue = SaStrategy.instance.generateUniqueToken.execute(
					"token",
					getConfigOfMaxTryTimes(createSaLoginParameter()),
					() -> {
						return createTokenValue(null, null, getConfigOrGlobal().getTimeout(), null);
					},
					token -> {
						return getTokenSessionByToken(token, false) == null;
					}
			);

			// 写入此 token 的最后活跃时间
			if(isOpenCheckActiveTimeout()) {
				setLastActiveToNow(tokenValue, null, null);
			}

			// 在当前上下文写入此 TokenValue
			setTokenValue(tokenValue);

			// 返回其 Token-Session 对象
			final String finalTokenValue = tokenValue;
			return getSessionBySessionId(splicingKeyTokenSession(tokenValue), isCreate, getConfigOrGlobal().getTimeout(), session -> {
				// 这里是该 Anon-Token-Session 首次创建时才会被执行的方法：
				// 		设定这个 SaSession 的各种基础信息：类型、账号体系、Token 值
				session.setType(SaTokenConsts.SESSION_TYPE__ANON);
				session.setLoginType(getLoginType());
				session.setToken(finalTokenValue);
			});
		}
		else {
			return null;
		}
	}

	/**
	 * 获取当前匿名 Token-Session （可在未登录情况下使用的Token-Session）
	 *
	 * @return Token-Session 对象
	 */
	public SaSession getAnonTokenSession() {
		return getAnonTokenSession(true);
	}

	/**
	 * 删除指定 token 的 Token-Session
	 *
	 * @param tokenValue token值
	 */
	public void deleteTokenSession(String tokenValue) {
		getSaTokenDao().delete(splicingKeyTokenSession(tokenValue));
	}


	// ------------------- Active-Timeout token 最低活跃度 验证相关 -------------------

	/**
	 * 写入指定 token 的 [ 最后活跃时间 ] 为当前时间戳 √√√
	 *
	 * @param tokenValue 指定token
	 * @param activeTimeout 这个 token 的最低活跃频率，单位：秒，填 null 代表使用全局配置的 activeTimeout 值
	 * @param timeout 保存数据时使用的 ttl 值，单位：秒，填 null 代表使用全局配置的 timeout 值
	 */
	protected void setLastActiveToNow(String tokenValue, Long activeTimeout, Long timeout) {

		// 如果提供的 timeout 为null，则使用全局配置的 timeout 值
		SaTokenConfig config = getConfigOrGlobal();
		if(timeout == null) {
			timeout = config.getTimeout();
		}
		// activeTimeout 变量无需赋值默认值，因为当缓存中没有这个值时，会自动使用全局配置的值

		// 将此 token 的 [ 最后活跃时间 ] 标记为当前时间戳
		String key = splicingKeyLastActiveTime(tokenValue);
		String value = String.valueOf(System.currentTimeMillis());
		if(config.getDynamicActiveTimeout() && activeTimeout != null) {
			value += "," + activeTimeout;
		}
		getSaTokenDao().set(key, value, timeout);
	}

	/**
	 * 续签指定 token：将这个 token 的 [ 最后活跃时间 ] 更新为当前时间戳
	 *
	 * @param tokenValue 指定token
	 */
	public void updateLastActiveToNow(String tokenValue) {
		String key = splicingKeyLastActiveTime(tokenValue);
		String value = new SaValue2Box(System.currentTimeMillis(), getTokenUseActiveTimeout(tokenValue)).toString();
		getSaTokenDao().update(key, value);
	}

	/**
	 * 续签当前 token：(将 [最后操作时间] 更新为当前时间戳)
	 * <h2>
	 * 		请注意: 即使 token 已被冻结 也可续签成功，
	 * 		如果此场景下需要提示续签失败，可在此之前调用 checkActiveTimeout() 强制检查是否冻结即可
	 * </h2>
	 */
	public void updateLastActiveToNow() {
		updateLastActiveToNow(getTokenValue());
	}

	/**
	 * 清除指定 Token 的 [ 最后活跃时间记录 ]
	 *
	 * @param tokenValue 指定 token
	 */
	protected void clearLastActive(String tokenValue) {
		getSaTokenDao().delete(splicingKeyLastActiveTime(tokenValue));
	}

	/**
	 * 判断指定 token 是否已被冻结
	 *
	 * @param tokenValue 指定 token
	 */
	public boolean isFreeze(String tokenValue) {

		// 1、获取这个 token 的剩余活跃有效期
		long activeTimeout = getTokenActiveTimeoutByToken(tokenValue);

		// 2、值为 -1 代表此 token 已经被设置永不冻结
		if(activeTimeout == SaTokenDao.NEVER_EXPIRE) {
			return false;
		}

		// 3、值为 -2 代表已被冻结
		if(activeTimeout == SaTokenDao.NOT_VALUE_EXPIRE) {
			return true;
		}
		return false;
	}

	/**
	 * 根据全局配置决定是否校验指定 token 的活跃度
	 *
	 * @param tokenValue 指定 token
	 */
	public void checkActiveTimeoutByConfig(String tokenValue) {
		if(isOpenCheckActiveTimeout()) {
			// storage.get(key, () -> {}) 可以避免一次请求多次校验，造成不必要的性能消耗
			SaHolder.getStorage().get(SaTokenConsts.TOKEN_ACTIVE_TIMEOUT_CHECKED_KEY, () -> {

				// 1、检查此 token 的最后活跃时间是否已经超过了 active-timeout 的限制，如果是则代表其已被冻结，需要抛出：token 已被冻结
				checkActiveTimeout(tokenValue);

				// 2、如果配置了自动续签功能, 则: 更新这个 token 的最后活跃时间 （注意此处的续签是在续 active-timeout，而非 timeout）
				if(SaStrategy.instance.autoRenew.apply(this)) {
					updateLastActiveToNow(tokenValue);
				}

				return true;
			});
		}
	}

	/**
	 * 检查指定 token 是否已被冻结，如果是则抛出异常
	 *
	 * @param tokenValue 指定 token
	 */
	public void checkActiveTimeout(String tokenValue) {
		if (isFreeze(tokenValue)) {
			throw NotLoginException.newInstance(loginType, TOKEN_FREEZE, TOKEN_FREEZE_MESSAGE, tokenValue).setCode(SaErrorCode.CODE_11016);
		}
	}

	/**
	 * 检查当前 token 是否已被冻结，如果是则抛出异常
	 */
	public void checkActiveTimeout() {
		checkActiveTimeout(getTokenValue());
	}

	/**
	 * 获取指定 token 在缓存中的 activeTimeout 值，如果不存在则返回 null
	 *
	 * @param tokenValue 指定token
	 * @return /
	 */
	public Long getTokenUseActiveTimeout(String tokenValue) {
		// 在未启用动态 activeTimeout 功能时，直接返回 null
		if( ! getConfigOrGlobal().getDynamicActiveTimeout()) {
			return null;
		}

		// 先取出这个 token 的最后活跃时间值
		String key = splicingKeyLastActiveTime(tokenValue);
		String value = getSaTokenDao().get(key);

		// 解析，无值的情况下返回 null
		SaValue2Box box = new SaValue2Box(value);
		return box.getValue2AsLong(null);
	}

	/**
	 * 获取指定 token 在缓存中的 activeTimeout 值，如果不存在则返回全局配置的 activeTimeout 值
	 *
	 * @param tokenValue 指定token
	 * @return /
	 */
	public long getTokenUseActiveTimeoutOrGlobalConfig(String tokenValue) {
		Long activeTimeout = getTokenUseActiveTimeout(tokenValue);
		if(activeTimeout == null) {
			return getConfigOrGlobal().getActiveTimeout();
		}
		return activeTimeout;
	}

	/**
	 * 获取指定 token 的最后活跃时间（13位时间戳），如果不存在则返回 -2
	 *
	 * @param tokenValue 指定token
	 * @return /
	 */
	public long getTokenLastActiveTime(String tokenValue) {
		// 1、如果提供的 token 为 null，则返回 -2
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}

		// 2、获取这个 token 的最后活跃时间，13位时间戳
		String key = splicingKeyLastActiveTime(tokenValue);
		String lastActiveTimeString = getSaTokenDao().get(key);

		// 3、查不到，返回-2
		if(lastActiveTimeString == null) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}

		// 4、根据逗号切割字符串
		return new SaValue2Box(lastActiveTimeString).getValue1AsLong();
	}

	/**
	 * 获取当前 token 的最后活跃时间（13位时间戳），如果不存在则返回 -2
	 *
	 * @return /
	 */
	public long getTokenLastActiveTime() {
		return getTokenLastActiveTime(getTokenValue());
	}


	// ------------------- 过期时间相关 -------------------

	/**
	 * 获取当前会话 token 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @return token剩余有效时间
	 */
	public long getTokenTimeout() {
		return getTokenTimeout(getTokenValue());
	}

	/**
	 * 获取指定 token 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @param token 指定token
	 * @return token剩余有效时间
	 */
	public long getTokenTimeout(String token) {
		return getSaTokenDao().getTimeout(splicingKeyTokenValue(token));
	}

	/**
	 * 获取指定账号 id 的 token 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @param loginId 指定loginId
	 * @return token剩余有效时间
	 */
	public long getTokenTimeoutByLoginId(Object loginId) {
		return getSaTokenDao().getTimeout(splicingKeyTokenValue(getTokenValueByLoginId(loginId)));
	}

	/**
	 * 获取当前登录账号的 Account-Session 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @return token剩余有效时间
	 */
	public long getSessionTimeout() {
		return getSessionTimeoutByLoginId(getLoginIdDefaultNull());
	}

	/**
	 * 获取指定账号 id 的 Account-Session 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @param loginId 指定loginId
	 * @return token剩余有效时间
	 */
	public long getSessionTimeoutByLoginId(Object loginId) {
		return getSaTokenDao().getSessionTimeout(splicingKeySession(loginId));
	}

	/**
	 * 获取当前 token 的 Token-Session 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @return token剩余有效时间
	 */
	public long getTokenSessionTimeout() {
		return getTokenSessionTimeoutByTokenValue(getTokenValue());
	}

	/**
	 * 获取指定 token 的 Token-Session 剩余有效时间（单位: 秒，返回 -1 代表永久有效，-2 代表没有这个值）
	 *
	 * @param tokenValue 指定token
	 * @return token 剩余有效时间
	 */
	public long getTokenSessionTimeoutByTokenValue(String tokenValue) {
		return getSaTokenDao().getSessionTimeout(splicingKeyTokenSession(tokenValue));
	}

	/**
	 * 获取当前 token 剩余活跃有效期：当前 token 距离被冻结还剩多少时间（单位: 秒，返回 -1 代表永不冻结，-2 代表没有这个值或 token 已被冻结了）
	 *
	 * @return /
	 */
	public long getTokenActiveTimeout() {
		return getTokenActiveTimeoutByToken(getTokenValue());
	}

	/**
	 * 获取指定 token 剩余活跃有效期：这个 token 距离被冻结还剩多少时间（单位: 秒，返回 -1 代表永不冻结，-2 代表没有这个值或 token 已被冻结了）
	 *
	 * @param tokenValue 指定 token
	 * @return /
	 */
	public long getTokenActiveTimeoutByToken(String tokenValue) {

		// 如果全局配置了永不冻结, 则返回 -1
		if( ! isOpenCheckActiveTimeout() ) {
			return SaTokenDao.NEVER_EXPIRE;
		}

		// ------ 开始查询

		// 先获取这个 token 的最后活跃时间，13位时间戳
		long lastActiveTime = getTokenLastActiveTime(tokenValue);
		if(lastActiveTime == SaTokenDao.NOT_VALUE_EXPIRE) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}

		// 实际时间差
		long timeDiff = (System.currentTimeMillis() - lastActiveTime) / 1000;
		// 该 token 允许的时间差
		long allowTimeDiff = getTokenUseActiveTimeoutOrGlobalConfig(tokenValue);
		if(allowTimeDiff == SaTokenDao.NEVER_EXPIRE) {
			// 如果允许的时间差为 -1 ，则代表永不冻结，此处需要立即返回 -1 ，无需后续计算
			return SaTokenDao.NEVER_EXPIRE;
		}

		// 校验这个时间差是否超过了允许的值
		//    计算公式为: 允许的最大时间差 - 实际时间差，判断是否 < 0， 如果是则代表已经被冻结 ，返回-2
		long activeTimeout = allowTimeDiff - timeDiff;
		if(activeTimeout < 0) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		} else {
			// 否则代表没冻结，返回剩余有效时间
			return activeTimeout;
		}
	}

	/**
	 * 对当前 token 的 timeout 值进行续期
	 *
	 * @param timeout 要修改成为的有效时间 (单位: 秒)
	 */
	public void renewTimeout(long timeout) {
		// 1、续期缓存数据
		String tokenValue = getTokenValue();
		renewTimeout(tokenValue, timeout);

		// 2、续期客户端 Cookie 有效期
		if(getConfigOrGlobal().getIsReadCookie()) {
			// 如果 timeout = -1，代表永久，但是一般浏览器不支持永久 Cookie，所以此处设置为 int 最大值
			// 如果 timeout 大于 int 最大值，会造成数据溢出，所以也要将其设置为 int 最大值
			if(timeout == SaTokenDao.NEVER_EXPIRE || timeout > Integer.MAX_VALUE) {
				timeout = Integer.MAX_VALUE;
			}
			setTokenValueToCookie(tokenValue, (int)timeout);
		}
	}

	/**
	 * 对指定 token 的 timeout 值进行续期
	 *
	 * @param tokenValue 指定 token
	 * @param timeout 要修改成为的有效时间 (单位: 秒，填 -1 代表要续为永久有效)
	 */
	public void renewTimeout(String tokenValue, long timeout) {

		// 1、如果 token 指向的 loginId 为空，或者属于异常项时，不进行续期操作
		Object loginId = getLoginIdByToken(tokenValue);
		if(loginId == null) {
			return;
		}

		// 2、检查 token 合法性
		SaSession session = getSessionByLoginId(loginId);
		if(session == null) {
			throw new SaTokenException("未能查询到对应 Access-Session 会话，无法续期");
		}
		if(session.getTerminal(tokenValue) == null) {
			throw new SaTokenException("未能查询到对应终端信息，无法续期");
		}

		// 3、续期此 token 本身的有效期 （改 ttl）
		SaTokenDao dao = getSaTokenDao();
		dao.updateTimeout(splicingKeyTokenValue(tokenValue), timeout);

		// 4、续期此 token 的 Token-Session 有效期
		SaSession tokenSession = getTokenSessionByToken(tokenValue, false);
		if(tokenSession != null) {
			tokenSession.updateTimeout(timeout);
		}

		// 5、续期此 token 指向的账号的 Account-Session 有效期
		session.updateMinTimeout(timeout);

		// 6、更新此 token 的最后活跃时间
		if(isOpenCheckActiveTimeout()) {
			dao.updateTimeout(splicingKeyLastActiveTime(tokenValue), timeout);
		}

		// 7、$$ 发布事件：某某 token 被续期了
		SaTokenEventCenter.doRenewTimeout(loginType, loginId, tokenValue, timeout);
	}


	// ------------------- 角色认证操作 -------------------

	/**
	 * 获取：当前账号的角色集合
	 *
	 * @return /
	 */
	public List<String> getRoleList() {
		return getRoleList(getLoginId());
	}

	/**
	 * 获取：指定账号的角色集合
	 *
	 * @param loginId 指定账号id
	 * @return /
	 */
	public List<String> getRoleList(Object loginId) {
		return SaManager.getStpInterface().getRoleList(loginId, loginType);
	}

	/**
	 * 判断：当前账号是否拥有指定角色, 返回 true 或 false
	 *
	 * @param role 角色
	 * @return /
	 */
	public boolean hasRole(String role) {
		try {
			return hasRole(getLoginId(), role);
		} catch (NotLoginException e) {
			return false;
		}
	}

	/**
	 * 判断：指定账号是否含有指定角色标识, 返回 true 或 false
	 *
	 * @param loginId 账号id
	 * @param role 角色标识
	 * @return 是否含有指定角色标识
	 */
	public boolean hasRole(Object loginId, String role) {
		return hasElement(getRoleList(loginId), role);
	}

	/**
	 * 判断：当前账号是否含有指定角色标识 [ 指定多个，必须全部验证通过 ]
	 *
	 * @param roleArray 角色标识数组
	 * @return true或false
	 */
	public boolean hasRoleAnd(String... roleArray){
		try {
			checkRoleAnd(roleArray);
			return true;
		} catch (NotLoginException | NotRoleException e) {
			return false;
		}
	}

	/**
	 * 判断：当前账号是否含有指定角色标识 [ 指定多个，只要其一验证通过即可 ]
	 *
	 * @param roleArray 角色标识数组
	 * @return true或false
	 */
	public boolean hasRoleOr(String... roleArray){
		try {
			checkRoleOr(roleArray);
			return true;
		} catch (NotLoginException | NotRoleException e) {
			return false;
		}
	}

	/**
	 * 校验：当前账号是否含有指定角色标识, 如果验证未通过，则抛出异常: NotRoleException
	 *
	 * @param role 角色标识
	 */
	public void checkRole(String role) {
		if( ! hasRole(getLoginId(), role)) {
			throw new NotRoleException(role, this.loginType).setCode(SaErrorCode.CODE_11041);
		}
	}

	/**
	 * 校验：当前账号是否含有指定角色标识 [ 指定多个，必须全部验证通过 ]
	 *
	 * @param roleArray 角色标识数组
	 */
	public void checkRoleAnd(String... roleArray){
		// 先获取当前是哪个账号id
		Object loginId = getLoginId();

		// 如果没有指定要校验的角色，那么直接跳过
		if(roleArray == null || roleArray.length == 0) {
			return;
		}

		// 开始校验
		List<String> roleList = getRoleList(loginId);
		for (String role : roleArray) {
			if(!hasElement(roleList, role)) {
				throw new NotRoleException(role, this.loginType).setCode(SaErrorCode.CODE_11041);
			}
		}
	}

	/**
	 * 校验：当前账号是否含有指定角色标识 [ 指定多个，只要其一验证通过即可 ]
	 *
	 * @param roleArray 角色标识数组
	 */
	public void checkRoleOr(String... roleArray){
		// 先获取当前是哪个账号id
		Object loginId = getLoginId();

		// 如果没有指定权限，那么直接跳过
		if(roleArray == null || roleArray.length == 0) {
			return;
		}

		// 开始校验
		List<String> roleList = getRoleList(loginId);
		for (String role : roleArray) {
			if(hasElement(roleList, role)) {
				// 有的话提前退出
				return;
			}
		}

		// 代码至此，说明一个都没通过，需要抛出无角色异常
		throw new NotRoleException(roleArray[0], this.loginType).setCode(SaErrorCode.CODE_11041);
	}


	// ------------------- 权限认证操作 -------------------

	/**
	 * 获取：当前账号的权限码集合
	 *
	 * @return /
	 */
	public List<String> getPermissionList() {
		return getPermissionList(getLoginId());
	}

	/**
	 * 获取：指定账号的权限码集合
	 *
	 * @param loginId 指定账号id
	 * @return /
	 */
	public List<String> getPermissionList(Object loginId) {
		return SaManager.getStpInterface().getPermissionList(loginId, loginType);
	}

	/**
	 * 判断：当前账号是否含有指定权限, 返回 true 或 false
	 *
	 * @param permission 权限码
	 * @return 是否含有指定权限
	 */
	public boolean hasPermission(String permission) {
		try {
			return hasPermission(getLoginId(), permission);
		} catch (NotLoginException e) {
			return false;
		}
	}

	/**
	 * 判断：指定账号 id 是否含有指定权限, 返回 true 或 false
	 *
	 * @param loginId 账号 id
	 * @param permission 权限码
	 * @return 是否含有指定权限
	 */
	public boolean hasPermission(Object loginId, String permission) {
		return hasElement(getPermissionList(loginId), permission);
	}

	/**
	 * 判断：当前账号是否含有指定权限 [ 指定多个，必须全部具有 ]
	 *
	 * @param permissionArray 权限码数组
	 * @return true 或 false
	 */
	public boolean hasPermissionAnd(String... permissionArray){
		try {
			checkPermissionAnd(permissionArray);
			return true;
		} catch (NotLoginException | NotPermissionException e) {
			return false;
		}
	}

	/**
	 * 判断：当前账号是否含有指定权限 [ 指定多个，只要其一验证通过即可 ]
	 *
	 * @param permissionArray 权限码数组
	 * @return true 或 false
	 */
	public boolean hasPermissionOr(String... permissionArray){
		try {
			checkPermissionOr(permissionArray);
			return true;
		} catch (NotLoginException | NotPermissionException e) {
			return false;
		}
	}

	/**
	 * 校验：当前账号是否含有指定权限, 如果验证未通过，则抛出异常: NotPermissionException
	 *
	 * @param permission 权限码
	 */
	public void checkPermission(String permission) {
		if( ! hasPermission(getLoginId(), permission)) {
			throw new NotPermissionException(permission, this.loginType).setCode(SaErrorCode.CODE_11051);
		}
	}

	/**
	 * 校验：当前账号是否含有指定权限 [ 指定多个，必须全部验证通过 ]
	 *
	 * @param permissionArray 权限码数组
	 */
	public void checkPermissionAnd(String... permissionArray){
		// 先获取当前是哪个账号id
		Object loginId = getLoginId();

		// 如果没有指定权限，那么直接跳过
		if(permissionArray == null || permissionArray.length == 0) {
			return;
		}

		// 开始校验
		List<String> permissionList = getPermissionList(loginId);
		for (String permission : permissionArray) {
			if(!hasElement(permissionList, permission)) {
				throw new NotPermissionException(permission, this.loginType).setCode(SaErrorCode.CODE_11051);
			}
		}
	}

	/**
	 * 校验：当前账号是否含有指定权限 [ 指定多个，只要其一验证通过即可 ]
	 *
	 * @param permissionArray 权限码数组
	 */
	public void checkPermissionOr(String... permissionArray){
		// 先获取当前是哪个账号id
		Object loginId = getLoginId();

		// 如果没有指定要校验的权限，那么直接跳过
		if(permissionArray == null || permissionArray.length == 0) {
			return;
		}

		// 开始校验
		List<String> permissionList = getPermissionList(loginId);
		for (String permission : permissionArray) {
			if(hasElement(permissionList, permission)) {
				// 有的话提前退出
				return;
			}
		}

		// 代码至此，说明一个都没通过，需要抛出无权限异常
		throw new NotPermissionException(permissionArray[0], this.loginType).setCode(SaErrorCode.CODE_11051);
	}



	// ------------------- id 反查 token 相关操作 -------------------

	/**
	 * 获取指定账号 id 的 token
	 * <p>
	 * 		在配置为允许并发登录时，此方法只会返回队列的最后一个 token，
	 * 		如果你需要返回此账号 id 的所有 token，请调用 getTokenValueListByLoginId
	 * </p>
	 *
	 * @param loginId 账号id
	 * @return token值
	 */
	public String getTokenValueByLoginId(Object loginId) {
		return getTokenValueByLoginId(loginId, null);
	}

	/**
	 * 获取指定账号 id 指定设备类型端的 token
	 * <p>
	 * 		在配置为允许并发登录时，此方法只会返回队列的最后一个 token，
	 * 		如果你需要返回此账号 id 的所有 token，请调用 getTokenValueListByLoginId
	 * </p>
	 *
	 * @param loginId 账号id
	 * @param deviceType 设备类型，填 null 代表不限设备类型
	 * @return token值
	 */
	public String getTokenValueByLoginId(Object loginId, String deviceType) {
		List<String> tokenValueList = getTokenValueListByLoginId(loginId, deviceType);
		return tokenValueList.isEmpty() ? null : tokenValueList.get(tokenValueList.size() - 1);
	}

	/**
	 * 获取指定账号 id 的 token 集合
	 *
	 * @param loginId 账号id
	 * @return 此 loginId 的所有相关 token
	 */
	public List<String> getTokenValueListByLoginId(Object loginId) {
		return getTokenValueListByLoginId(loginId, null);
	}

	/**
	 * 获取指定账号 id 指定设备类型端的 token 集合
	 *
	 * @param loginId 账号id
	 * @param deviceType 设备类型，填 null 代表不限设备类型
	 * @return 此 loginId 的所有登录 token
	 */
	public List<String> getTokenValueListByLoginId(Object loginId, String deviceType) {
		// 如果该账号的 Account-Session 为 null，说明此账号尚没有客户端在登录，此时返回空集合
		SaSession session = getSessionByLoginId(loginId, false);
		if(session == null) {
			return new ArrayList<>();
		}

		// 按照设备类型进行筛选
		return session.getTokenValueListByDeviceType(deviceType);
	}

	/**
	 * 获取指定账号 id 已登录设备信息集合
	 *
	 * @param loginId 账号id
	 * @return 此 loginId 的所有登录 token
	 */
	public List<SaTerminalInfo> getTerminalListByLoginId(Object loginId) {
		return getTerminalListByLoginId(loginId, null);
	}

	/**
	 * 获取指定账号 id 指定设备类型端的已登录设备信息集合
	 *
	 * @param loginId 账号id
	 * @param deviceType 设备类型，填 null 代表不限设备类型
	 * @return 此 loginId 的所有登录 token
	 */
	public List<SaTerminalInfo> getTerminalListByLoginId(Object loginId, String deviceType) {
		// 如果该账号的 Account-Session 为 null，说明此账号尚没有客户端在登录，此时返回空集合
		SaSession session = getSessionByLoginId(loginId, false);
		if(session == null) {
			return new ArrayList<>();
		}

		// 按照设备类型进行筛选
		return session.getTerminalListByDeviceType(deviceType);
	}

	/**
	 * 获取指定账号 id 已登录设备信息集合，执行特定函数
	 *
	 * @param loginId 账号id
	 * @param function 需要执行的函数
	 */
	public void forEachTerminalList(Object loginId, SaTwoParamFunction<SaSession, SaTerminalInfo> function) {
		// 如果该账号的 Account-Session 为 null，说明此账号尚没有客户端在登录，此时无需遍历
		SaSession session = getSessionByLoginId(loginId, false);
		if(session == null) {
			return;
		}

		// 遍历
		session.forEachTerminalList(function);
	}

	/**
	 * 返回当前 token 指向的 SaTerminalInfo 设备信息，如果 token 无效则返回 null
	 *
	 * @return /
	 */
	public SaTerminalInfo getTerminalInfo() {
		return getTerminalInfoByToken(getTokenValue());
	}

	/**
	 * 返回指定 token 指向的 SaTerminalInfo 设备信息，如果 Token 无效则返回 null
	 *
	 * @param tokenValue 指定 token
	 * @return /
	 */
	public SaTerminalInfo getTerminalInfoByToken(String tokenValue) {
		// 1、如果 token 为 null，直接提前返回
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return null;
		}

		// 2、判断 Token 是否有效
		Object loginId = getLoginIdNotHandle(tokenValue);
		if( ! isValidLoginId(loginId)) {
			return null;
		}

		// 3、判断 Account-Session 是否存在
		SaSession session = getSessionByLoginId(loginId, false);
		if(session == null) {
			return null;
		}

		// 4、判断 Token 是否已被冻结
		if(isFreeze(tokenValue)) {
			return null;
		}

		// 5、遍历 Account-Session 上的客户端 token 列表，寻找当前 token 对应的设备类型
		List<SaTerminalInfo> terminalList = session.terminalListCopy();
		for (SaTerminalInfo terminal : terminalList) {
			if(terminal.getTokenValue().equals(tokenValue)) {
				return terminal;
			}
		}

		// 6、没有找到，还是返回 null
		return null;
	}

	/**
	 * 返回当前会话的登录设备类型
	 *
	 * @return 当前令牌的登录设备类型
	 */
	public String getLoginDeviceType() {
		return getLoginDeviceTypeByToken(getTokenValue());
	}

	/**
	 * 返回指定 token 会话的登录设备类型
	 *
	 * @param tokenValue 指定token
	 * @return 当前令牌的登录设备类型
	 */
	public String getLoginDeviceTypeByToken(String tokenValue) {
		SaTerminalInfo terminalInfo = getTerminalInfoByToken(tokenValue);
		return terminalInfo == null ? null : terminalInfo.getDeviceType();
	}

	/**
	 * 返回当前会话的登录设备 ID
	 *
	 * @return /
	 */
	public String getLoginDeviceId() {
		return getLoginDeviceIdByToken(getTokenValue());
	}

	/**
	 * 返回指定 token 会话的登录设备 ID
	 *
	 * @param tokenValue 指定token
	 * @return /
	 */
	public String getLoginDeviceIdByToken(String tokenValue) {
		SaTerminalInfo terminalInfo = getTerminalInfoByToken(tokenValue);
		return terminalInfo == null ? null : terminalInfo.getDeviceId();
	}

	/**
	 * 判断对于指定 loginId 来讲，指定设备 id 是否为可信任设备
	 * @param deviceId /
	 * @return /
	 */
	public boolean isTrustDeviceId(Object userId, String deviceId) {
		// 先查询此账号的 Account-Session，如果连 Account-Session 都没有，那么此账号尚未登录，直接返回 false
		SaSession session = getSessionByLoginId(userId, false);
		if(session == null) {
			return false;
		}

		// 判断
		return session.isTrustDeviceId(deviceId);
	}


	// ------------------- 会话管理 -------------------

	/**
	 * 根据条件查询缓存中所有的 token
	 *
	 * @param keyword 关键字
	 * @param start 开始处索引
	 * @param size 获取数量 (-1代表一直获取到末尾)
	 * @param sortType 排序类型（true=正序，false=反序）
	 *
	 * @return token集合
	 */
	public List<String> searchTokenValue(String keyword, int start, int size, boolean sortType) {
		return getSaTokenDao().searchData(splicingKeyTokenValue(""), (keyword == null ? "" : keyword), start, size, sortType);
	}

	/**
	 * 根据条件查询缓存中所有的 SessionId
	 *
	 * @param keyword 关键字
	 * @param start 开始处索引
	 * @param size 获取数量  (-1代表一直获取到末尾)
	 * @param sortType 排序类型（true=正序，false=反序）
	 *
	 * @return sessionId集合
	 */
	public List<String> searchSessionId(String keyword, int start, int size, boolean sortType) {
		return getSaTokenDao().searchData(splicingKeySession(""), (keyword == null ? "" : keyword), start, size, sortType);
	}

	/**
	 * 根据条件查询缓存中所有的 Token-Session-Id
	 *
	 * @param keyword 关键字
	 * @param start 开始处索引
	 * @param size 获取数量 (-1代表一直获取到末尾)
	 * @param sortType 排序类型（true=正序，false=反序）
	 *
	 * @return sessionId集合
	 */
	public List<String> searchTokenSessionId(String keyword, int start, int size, boolean sortType) {
		return getSaTokenDao().searchData(splicingKeyTokenSession(""), (keyword == null ? "" : keyword), start, size, sortType);
	}


	// ------------------- 账号封禁 -------------------

	/**
	 * 封禁：指定账号
	 * <p> 此方法不会直接将此账号id踢下线，如需封禁后立即掉线，请追加调用 StpUtil.logout(id)
	 *
	 * @param loginId 指定账号id
	 * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public void disable(Object loginId, long time) {
		disableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, SaTokenConsts.DEFAULT_DISABLE_LEVEL, time);
	}

	/**
	 * 判断：指定账号是否已被封禁 (true=已被封禁, false=未被封禁)
	 *
	 * @param loginId 账号id
	 * @return /
	 */
	public boolean isDisable(Object loginId) {
		return isDisableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, SaTokenConsts.MIN_DISABLE_LEVEL);
	}

	/**
	 * 校验：指定账号是否已被封禁，如果被封禁则抛出异常
	 *
	 * @param loginId 账号id
	 */
	public void checkDisable(Object loginId) {
		checkDisableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, SaTokenConsts.MIN_DISABLE_LEVEL);
	}

	/**
	 * 获取：指定账号剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
	 *
	 * @param loginId 账号id
	 * @return /
	 */
	public long getDisableTime(Object loginId) {
		return getDisableTime(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE);
	}

	/**
	 * 解封：指定账号
	 *
	 * @param loginId 账号id
	 */
	public void untieDisable(Object loginId) {
		untieDisable(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE);
	}


	// ------------------- 分类封禁 -------------------

	/**
	 * 封禁：指定账号的指定服务
	 * <p> 此方法不会直接将此账号id踢下线，如需封禁后立即掉线，请追加调用 StpUtil.logout(id)
	 *
	 * @param loginId 指定账号id
	 * @param service 指定服务
	 * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public void disable(Object loginId, String service, long time) {
		disableLevel(loginId, service, SaTokenConsts.DEFAULT_DISABLE_LEVEL, time);
	}

	/**
	 * 判断：指定账号的指定服务 是否已被封禁（true=已被封禁, false=未被封禁）
	 *
	 * @param loginId 账号id
	 * @param service 指定服务
	 * @return /
	 */
	public boolean isDisable(Object loginId, String service) {
		return isDisableLevel(loginId, service, SaTokenConsts.MIN_DISABLE_LEVEL);
	}

	/**
	 * 校验：指定账号 指定服务 是否已被封禁，如果被封禁则抛出异常
	 *
	 * @param loginId 账号id
	 * @param services 指定服务，可以指定多个
	 */
	public void checkDisable(Object loginId, String... services) {
		if(services != null) {
			for (String service : services) {
				checkDisableLevel(loginId, service, SaTokenConsts.MIN_DISABLE_LEVEL);
			}
		}
	}

	/**
	 * 获取：指定账号 指定服务 剩余封禁时间，单位：秒（-1=永久封禁，-2=未被封禁）
	 *
	 * @param loginId 账号id
	 * @param service 指定服务
	 * @return see note
	 */
	public long getDisableTime(Object loginId, String service) {
		return getSaTokenDao().getTimeout(splicingKeyDisable(loginId, service));
	}

	/**
	 * 解封：指定账号、指定服务
	 *
	 * @param loginId 账号id
	 * @param services 指定服务，可以指定多个
	 */
	public void untieDisable(Object loginId, String... services) {

		// 先检查提供的参数是否有效
		if(SaFoxUtil.isEmpty(loginId)) {
			throw new SaTokenException("请提供要解禁的账号").setCode(SaErrorCode.CODE_11062);
		}
		if(services == null || services.length == 0) {
			throw new SaTokenException("请提供要解禁的服务").setCode(SaErrorCode.CODE_11063);
		}

		// 遍历逐个解禁
		for (String service : services) {
			// 解封
			getSaTokenDao().delete(splicingKeyDisable(loginId, service));

			// $$ 发布事件
			SaTokenEventCenter.doUntieDisable(loginType, loginId, service);
		}
	}


	// ------------------- 阶梯封禁 -------------------

	/**
	 * 封禁：指定账号，并指定封禁等级
	 *
	 * @param loginId 指定账号id
	 * @param level 指定封禁等级
	 * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public void disableLevel(Object loginId, int level, long time) {
		disableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, level, time);
	}

	/**
	 * 封禁：指定账号的指定服务，并指定封禁等级
	 *
	 * @param loginId 指定账号id
	 * @param service 指定封禁服务
	 * @param level 指定封禁等级
	 * @param time 封禁时间, 单位: 秒 （-1=永久封禁）
	 */
	public void disableLevel(Object loginId, String service, int level, long time) {
		// 先检查提供的参数是否有效
		if(SaFoxUtil.isEmpty(loginId)) {
			throw new SaTokenException("请提供要封禁的账号").setCode(SaErrorCode.CODE_11062);
		}
		if(SaFoxUtil.isEmpty(service)) {
			throw new SaTokenException("请提供要封禁的服务").setCode(SaErrorCode.CODE_11063);
		}
		if(level < SaTokenConsts.MIN_DISABLE_LEVEL && level != 0) {
			throw new SaTokenException("封禁等级不可以小于最小值：" + SaTokenConsts.MIN_DISABLE_LEVEL + " (0除外)").setCode(SaErrorCode.CODE_11064);
		}

		// 打上封禁标记
		getSaTokenDao().set(splicingKeyDisable(loginId, service), String.valueOf(level), time);

		// $$ 发布事件
		SaTokenEventCenter.doDisable(loginType, loginId, service, level, time);
	}

	/**
	 * 判断：指定账号是否已被封禁到指定等级
	 *
	 * @param loginId 指定账号id
	 * @param level 指定封禁等级
	 * @return /
	 */
	public boolean isDisableLevel(Object loginId, int level) {
		return isDisableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, level);
	}

	/**
	 * 判断：指定账号的指定服务，是否已被封禁到指定等级
	 *
	 * @param loginId 指定账号id
	 * @param service 指定封禁服务
	 * @param level 指定封禁等级
	 * @return /
	 */
	public boolean isDisableLevel(Object loginId, String service, int level) {
		// 1、先前置检查一下这个账号是否被封禁了
		int disableLevel = getDisableLevel(loginId, service);
		if(disableLevel == SaTokenConsts.NOT_DISABLE_LEVEL) {
			return false;
		}

		// 2、再判断被封禁的等级是否达到了指定级别
		return disableLevel >= level;
	}

	/**
	 * 校验：指定账号是否已被封禁到指定等级（如果已经达到，则抛出异常）
	 *
	 * @param loginId 指定账号id
	 * @param level 封禁等级 （只有 封禁等级 ≥ 此值 才会抛出异常）
	 */
	public void checkDisableLevel(Object loginId, int level) {
		checkDisableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE, level);
	}

	/**
	 * 校验：指定账号的指定服务，是否已被封禁到指定等级（如果已经达到，则抛出异常）
	 *
	 * @param loginId 指定账号id
	 * @param service 指定封禁服务
	 * @param level 封禁等级 （只有 封禁等级 ≥ 此值 才会抛出异常）
	 */
	public void checkDisableLevel(Object loginId, String service, int level) {
		// 1、先前置检查一下这个账号是否被封禁了
		int disableLevel = getDisableLevel(loginId, service);
		if(disableLevel == SaTokenConsts.NOT_DISABLE_LEVEL) {
			return;
		}

		// 2、再判断被封禁的等级是否达到了指定级别
		if(disableLevel >= level) {
			throw new DisableServiceException(loginType, loginId, service, disableLevel, level, getDisableTime(loginId, service))
					.setCode(SaErrorCode.CODE_11061);
		}
	}

	/**
	 * 获取：指定账号被封禁的等级，如果未被封禁则返回-2
	 *
	 * @param loginId 指定账号id
	 * @return /
	 */
	public int getDisableLevel(Object loginId) {
		return getDisableLevel(loginId, SaTokenConsts.DEFAULT_DISABLE_SERVICE);
	}

	/**
	 * 获取：指定账号的 指定服务 被封禁的等级，如果未被封禁则返回-2
	 *
	 * @param loginId 指定账号id
	 * @param service 指定封禁服务
	 * @return /
	 */
	public int getDisableLevel(Object loginId, String service) {
		// 1、先从缓存中查询数据，缓存中有值，以缓存值优先
		String value = getSaTokenDao().get(splicingKeyDisable(loginId, service));
		if(SaFoxUtil.isNotEmpty(value)) {
			return SaFoxUtil.getValueByType(value, int.class);
		}

		// 2、如果缓存中无数据，则从"数据加载器"中再次查询
		SaDisableWrapperInfo disableWrapperInfo = SaManager.getStpInterface().isDisabled(loginId, service);

		// 如果返回值 disableTime 有效，则代表返回结果需要写入缓存
		if(disableWrapperInfo.disableTime == SaTokenDao.NEVER_EXPIRE || disableWrapperInfo.disableTime > 0) {
			disableLevel(loginId, service, disableWrapperInfo.disableLevel, disableWrapperInfo.disableTime);
		}

		// 返回查询结果
		return disableWrapperInfo.disableLevel;
	}


	// ------------------- 临时身份切换 -------------------

	/**
	 * 临时切换身份为指定账号id
	 *
	 * @param loginId 指定loginId
	 */
	public void switchTo(Object loginId) {
		SaHolder.getStorage().set(splicingKeySwitch(), loginId);
	}

	/**
	 * 结束临时切换身份
	 */
	public void endSwitch() {
		SaHolder.getStorage().delete(splicingKeySwitch());
	}

	/**
	 * 判断当前请求是否正处于 [ 身份临时切换 ] 中
	 *
	 * @return /
	 */
	public boolean isSwitch() {
		return SaHolder.getStorage().get(splicingKeySwitch()) != null;
	}

	/**
	 * 返回 [ 身份临时切换 ] 的 loginId
	 *
	 * @return /
	 */
	public Object getSwitchLoginId() {
		return SaHolder.getStorage().get(splicingKeySwitch());
	}

	/**
	 * 在一个 lambda 代码段里，临时切换身份为指定账号id，lambda 结束后自动恢复
	 *
	 * @param loginId 指定账号id
	 * @param function 要执行的方法
	 */
	public void switchTo(Object loginId, SaFunction function) {
		try {
			switchTo(loginId);
			function.run();
		} finally {
			endSwitch();
		}
	}


	// ------------------- 二级认证 -------------------

	/**
	 * 在当前会话 开启二级认证
	 *
	 * @param safeTime 维持时间 (单位: 秒)
	 */
	public void openSafe(long safeTime) {
		openSafe(SaTokenConsts.DEFAULT_SAFE_AUTH_SERVICE, safeTime);
	}

	/**
	 * 在当前会话 开启二级认证
	 *
	 * @param service 业务标识
	 * @param safeTime 维持时间 (单位: 秒)
	 */
	public void openSafe(String service, long safeTime) {
		// 1、开启二级认证前必须处于登录状态，否则抛出异常
		checkLogin();

		// 2、写入指定的 可以 标记，打开二级认证
		String tokenValue = getTokenValueNotNull();
		getSaTokenDao().set(splicingKeySafe(tokenValue, service), SaTokenConsts.SAFE_AUTH_SAVE_VALUE, safeTime);

		// 3、$$ 发布事件，某某 token 令牌开启了二级认证
		SaTokenEventCenter.doOpenSafe(loginType, tokenValue, service, safeTime);
	}

	/**
	 * 判断：当前会话是否处于二级认证时间内
	 *
	 * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时
	 */
	public boolean isSafe() {
		return isSafe(SaTokenConsts.DEFAULT_SAFE_AUTH_SERVICE);
	}

	/**
	 * 判断：当前会话 是否处于指定业务的二级认证时间内
	 *
	 * @param service 业务标识
	 * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时
	 */
	public boolean isSafe(String service) {
		return isSafe(getTokenValue(), service);
	}

	/**
	 * 判断：指定 token 是否处于二级认证时间内
	 *
	 * @param tokenValue Token 值
	 * @param service 业务标识
	 * @return true=二级认证已通过, false=尚未进行二级认证或认证已超时
	 */
	public boolean isSafe(String tokenValue, String service) {
		// 1、如果提供的 Token 为空，则直接视为未认证
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return false;
		}

		// 2、如果此 token 不处于登录状态，也将其视为未认证
		Object loginId = getLoginIdNotHandle(tokenValue);
		if( ! isValidLoginId(loginId) ) {
			return false;
		}

		// 3、如果缓存中可以查询出指定的键值，则代表已认证，否则视为未认证
		String value = getSaTokenDao().get(splicingKeySafe(tokenValue, service));
		return !(SaFoxUtil.isEmpty(value));
	}

	/**
	 * 校验：当前会话是否已通过二级认证，如未通过则抛出异常
	 */
	public void checkSafe() {
		checkSafe(SaTokenConsts.DEFAULT_SAFE_AUTH_SERVICE);
	}

	/**
	 * 校验：检查当前会话是否已通过指定业务的二级认证，如未通过则抛出异常
	 *
	 * @param service 业务标识
	 */
	public void checkSafe(String service) {
		// 1、必须先通过登录校验
		checkLogin();

		// 2、再进行二级认证校验
		// 		如果缓存中可以查询出指定的键值，则代表已认证，否则视为未认证
		String tokenValue = getTokenValue();
		String value = getSaTokenDao().get(splicingKeySafe(tokenValue, service));
		if(SaFoxUtil.isEmpty(value)) {
			throw new NotSafeException(loginType, tokenValue, service).setCode(SaErrorCode.CODE_11071);
		}
	}

	/**
	 * 获取：当前会话的二级认证剩余有效时间（单位: 秒, 返回-2代表尚未通过二级认证）
	 *
	 * @return 剩余有效时间
	 */
	public long getSafeTime() {
		return getSafeTime(SaTokenConsts.DEFAULT_SAFE_AUTH_SERVICE);
	}

	/**
	 * 获取：当前会话的二级认证剩余有效时间（单位: 秒, 返回-2代表尚未通过二级认证）
	 *
	 * @param service 业务标识
	 * @return 剩余有效时间
	 */
	public long getSafeTime(String service) {
		// 1、如果前端没有提交 Token，则直接视为未认证
		String tokenValue = getTokenValue();
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}

		// 2、从缓存中查询这个 key 的剩余有效期
		return getSaTokenDao().getTimeout(splicingKeySafe(tokenValue, service));
	}

	/**
	 * 在当前会话 结束二级认证
	 */
	public void closeSafe() {
		closeSafe(SaTokenConsts.DEFAULT_SAFE_AUTH_SERVICE);
	}

	/**
	 * 在当前会话 结束指定业务标识的二级认证
	 *
	 * @param service 业务标识
	 */
	public void closeSafe(String service) {
		// 1、如果前端没有提交 Token，则无需任何操作
		String tokenValue = getTokenValue();
		if(SaFoxUtil.isEmpty(tokenValue)) {
			return;
		}

		// 2、删除 key
		getSaTokenDao().delete(splicingKeySafe(tokenValue, service));

		// 3、$$ 发布事件，某某 token 令牌关闭了二级认证
		SaTokenEventCenter.doCloseSafe(loginType, tokenValue, service);
	}


	// ------------------- 拼接相应key -------------------

	/**
	 * 获取：客户端 tokenName
	 *
	 * @return key
	 */
	public String splicingKeyTokenName() {
		return getConfigOrGlobal().getTokenName();
	}

	/**
	 * 拼接： 在保存 token - id 映射关系时，应该使用的key
	 *
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyTokenValue(String tokenValue) {
		return getConfigOrGlobal().getTokenName() + ":" + loginType + ":token:" + tokenValue;
	}

	/**
	 * 拼接： 在保存 Account-Session 时，应该使用的 key
	 *
	 * @param loginId 账号id
	 * @return key
	 */
	public String splicingKeySession(Object loginId) {
		return getConfigOrGlobal().getTokenName() + ":" + loginType + ":session:" + loginId;
	}

	/**
	 * 拼接：在保存 Token-Session 时，应该使用的 key
	 *
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyTokenSession(String tokenValue) {
		return getConfigOrGlobal().getTokenName() + ":" + loginType + ":token-session:" + tokenValue;
	}

	/**
	 * 拼接： 在保存 token 最后活跃时间时，应该使用的 key
	 *
	 * @param tokenValue token值
	 * @return key
	 */
	public String splicingKeyLastActiveTime(String tokenValue) {
		return getConfigOrGlobal().getTokenName() + ":" + loginType + ":last-active:" + tokenValue;
	}

	/**
	 * 拼接：在进行临时身份切换时，应该使用的 key
	 *
	 * @return key
	 */
	public String splicingKeySwitch() {
		return SaTokenConsts.SWITCH_TO_SAVE_KEY + loginType;
	}

	/**
	 * 如果 token 为本次请求新创建的，则以此字符串为 key 存储在当前 request 中
	 *
	 * @return key
	 */
	public String splicingKeyJustCreatedSave() {
		//		return SaTokenConsts.JUST_CREATED_SAVE_KEY + loginType;
		return SaTokenConsts.JUST_CREATED;
	}

	/**
	 * 拼接： 在保存服务封禁标记时，应该使用的 key
	 *
	 * @param loginId 账号id
	 * @param service 具体封禁的服务
	 * @return key
	 */
	public String splicingKeyDisable(Object loginId, String service) {
		return getConfigOrGlobal().getTokenName() + ":" + loginType + ":disable:" + service + ":" + loginId;
	}

	/**
	 * 拼接： 在保存业务二级认证标记时，应该使用的 key
	 *
	 * @param tokenValue 要认证的 Token
	 * @param service 要认证的业务标识
	 * @return key
	 */
	public String splicingKeySafe(String tokenValue, String service) {
		// 格式：<Token名称>:<账号类型>:<safe>:<业务标识>:<Token值>
		// 形如：satoken:login:safe:important:gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__
		return getConfigOrGlobal().getTokenName() + ":" + loginType + ":safe:" + service + ":" + tokenValue;
	}


	// ------------------- Bean 对象、字段代理 -------------------

	/**
	 * 返回当前 StpLogic 使用的持久化对象
	 *
	 * @return /
	 */
	public SaTokenDao getSaTokenDao() {
		return SaManager.getSaTokenDao();
	}

	/**
	 * 返回当前 StpLogic 是否支持共享 token 策略
	 *
	 * @return /
	 */
	public boolean isSupportShareToken() {
		return getConfigOrGlobal().getIsShare();
	}

	/**
	 * 返回全局配置是否开启了 Token 活跃度校验，返回 true 代表已打开，返回 false 代表不打开，此时永不冻结 token
	 *
	 * @return /
	 */
	public boolean isOpenCheckActiveTimeout() {
		SaTokenConfig cfg = getConfigOrGlobal();
		return cfg.getActiveTimeout() != SaTokenDao.NEVER_EXPIRE || cfg.getDynamicActiveTimeout();
	}

	/**
	 * 返回全局配置的 Cookie 保存时长，单位：秒 （根据全局 timeout 计算）
	 *
	 * @return Cookie 应该保存的时长
	 */
	public int getConfigOfCookieTimeout() {
		long timeout = getConfigOrGlobal().getTimeout();
		if(timeout == SaTokenDao.NEVER_EXPIRE) {
			return Integer.MAX_VALUE;
		}
		return (int) timeout;
	}

	/**
	 * 返回全局配置的 maxTryTimes 值，在每次创建 token 时，对其唯一性测试的最高次数（-1=不测试）
	 *
	 * @param loginParameter /
	 * @return /
	 */
	public int getConfigOfMaxTryTimes(SaLoginParameter loginParameter) {
		return loginParameter.getMaxTryTimes();
	}

	/**
	 * 判断：集合中是否包含指定元素（模糊匹配）
	 *
	 * @param list 集合
	 * @param element 元素
	 * @return /
	 */
	public boolean hasElement(List<String> list, String element) {
		return SaStrategy.instance.hasElement.apply(list, element);
	}

	/**
	 * 当前 StpLogic 对象是否支持 token 扩展参数
	 *
	 * @return /
	 */
	public boolean isSupportExtra() {
		return false;
	}

	/**
	 * 根据当前配置对象创建一个 SaLoginParameter 对象
	 *
	 * @return /
	 */
	public SaLoginParameter createSaLoginParameter() {
		return new SaLoginParameter(getConfigOrGlobal());
	}

	/**
	 * 根据当前配置对象创建一个 SaLogoutParameter 对象
	 *
	 * @return /
	 */
	public SaLogoutParameter createSaLogoutParameter() {
		return new SaLogoutParameter(getConfigOrGlobal());
	}



	// ------------------- 过期方法 -------------------

	/**
	 * <h2>请更换为 getLoginDeviceType </h2>
	 * 返回当前会话的登录设备类型
	 *
	 * @return 当前令牌的登录设备类型
	 */
	@Deprecated
	public String getLoginDevice() {
		return getLoginDeviceType();
	}

	/**
	 * <h2>请更换为 getLoginDeviceTypeByToken </h2>
	 * 返回指定 token 会话的登录设备类型
	 *
	 * @param tokenValue 指定token
	 * @return 当前令牌的登录设备类型
	 */
	@Deprecated
	public String getLoginDeviceByToken(String tokenValue) {
		return getLoginDeviceTypeByToken(tokenValue);
	}

}
