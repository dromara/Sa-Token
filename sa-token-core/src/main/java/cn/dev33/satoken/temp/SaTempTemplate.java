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
package cn.dev33.satoken.temp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.raw.SaRawSessionDelegator;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.util.SaFoxUtil;

import java.util.*;

/**
 * Sa-Token 临时 token 验证模块
 *
 * <p>
 *     有效期很短的一种token，一般用于一次性接口防盗用、短时间资源访问等业务场景
 * </p>
 *
 * @author click33
 * @since 1.42.0
 */
public class SaTempTemplate {

	/**
	 *默认命名空间
	 */
	public static final String DEFAULT_NAMESPACE = "temp-token";

	/**
	 * 命名空间
	 */
	public String namespace;

	/**
	 * Raw Session 读写委托
	 */
	public SaRawSessionDelegator rawSessionDelegator;

	/**
	 * 在 raw-session 中的保存索引列表使用的 key
	 */
	public static final String TEMP_TOKEN_MAP = "__HD_TEMP_TOKEN_MAP";

	public SaTempTemplate(){
		this(DEFAULT_NAMESPACE);
	}

	/**
	 * 实例化
	 * @param namespace 命名空间，用于多实例隔离
	 */
	public SaTempTemplate(String namespace){
		if(SaFoxUtil.isEmpty(namespace)) {
			throw new SaTokenException("namespace 不能为空");
		}
		this.namespace = namespace;
		this.rawSessionDelegator = new SaRawSessionDelegator(namespace);
	}


	// -------- 创建

	/**
	 * 为指定 value 创建一个临时 token (如果多条业务线均需要创建临时 token，请自行在 value 拼接不同前缀)
	 *
	 * @param value 指定值
	 * @param timeout 有效时间，单位：秒，-1 代表永久有效
	 * @return 生成的 token
	 */
	public String createToken(Object value, long timeout) {
		return createToken(value, timeout, false);
	}

	/**
	 * 为指定 业务标识、指定 value 创建一个 Token
	 * @param value 指定值
	 * @param timeout 有效期，单位：秒，-1 代表永久有效
	 * @param isRecordIndex 是否记录索引，以便后续使用 value 反查 token
	 * @return 生成的token
	 */
	public String createToken(Object value, long timeout, boolean isRecordIndex) {

		// 生成 temp-token
		String tempToken = createTempTokenValue(value);

		// 持久化映射关系
		saveToken(tempToken, value, timeout);

		// 记录索引
		if(isRecordIndex) {
			SaSession session = rawSessionDelegator.getSessionById(value);
			addTempTokenIndex(session, tempToken, timeout);
			adjustIndex(value, session);
		}

		// 返回
		return tempToken;
	}

	/**
	 * 保存 token
	 * @param token /
	 * @param value /
	 * @param timeout /
	 */
	public void saveToken(String token, Object value, long timeout) {
		String key = splicingTempTokenSaveKey(token);
		SaManager.getSaTokenDao().setObject(key, value, timeout);
	}

	/**
	 * 创建一个 temp-token 值
	 *
	 * @return /
	 */
	public String createTempTokenValue(Object value) {
		return SaStrategy.instance.generateUniqueToken.execute(
				"Temp Token",
				SaManager.getConfig().getMaxTryTimes(),
				() -> randomTempToken(value),
				_apiKey -> _getValue(_apiKey) == null
		);
	}

	/**
	 * 随机一个 temp-token
	 *
	 * @return /
	 */
	public String randomTempToken(Object value) {
		return UUID.randomUUID().toString().replace("-", "");
	}


	// -------- 解析

	/**
	 * 解析 Token 获取 value
	 * @param token 指定 Token
	 * @return /
	 */
	public Object parseToken(String token) {
		return _getValue(token);
	}

	/**
	 * 解析 Token 获取 value，并转换为指定类型
	 *
	 * @param token 指定 Token
	 * @param cs 指定类型
	 * @param <T> 默认值的类型
	 * @return /
	 */
	public<T> T parseToken(String token, Class<T> cs) {
		return parseToken(token, null, cs);
	}

	/**
	 * 解析 token 获取 value，并裁剪指定前缀，然后转换为指定类型
	 * <h2>
	 *     请注意此方法在旧版本（<= v1.41.0） 时的三个参数为：service, token, class <br/>
	 *     新版本三个参数为：token, cutPrefix, class <br/>
	 *     请注意其中的逻辑变化
	 * </h2>
	 *
	 * @param token 指定 Token
	 * @param cs 指定类型
	 * @param <T> 默认值的类型
	 * @return /
	 */
	public<T> T parseToken(String token, String cutPrefix, Class<T> cs) {
		// 解析值
		Object value = parseToken(token);

		// 如果未指定裁剪前缀，则直接返回
		if(SaFoxUtil.isEmpty(cutPrefix)) {
			return SaFoxUtil.getValueByType(value, cs);
		}

		// 如果符合前缀则裁剪并返回，如果不符合前缀则返回 null
		checkCutPrefixLength(cutPrefix);
		String str = SaFoxUtil.valueToString(value);
		if(str.startsWith(cutPrefix)) {
			return SaFoxUtil.getValueByType(str.substring(cutPrefix.length()), cs);
		} else {
			return null;
		}
	}

	/**
	 * 获取指定指定 Token 的剩余有效期，单位：秒
	 * <p> 返回值 -1 代表永久，-2 代表 token 无效
	 *
	 * @param token /
	 * @return /
	 */
	public long getTimeout(String token) {
		return _getTimeout(token);
	}


	// -------- 删除

	/**
	 * 删除一个 token
	 * @param token 指定 Token
	 */
	public void deleteToken(String token) {
		// 如果无此数据，则直接返回
		Object value = parseToken(token);
		if(SaFoxUtil.isEmpty(value)) {
			return;
		}

		// 删除 token 本身
		_deleteToken(token);

		// 调整索引
		SaSession session = rawSessionDelegator.getSessionById(value, false);
		if(session != null) {
			deleteTempTokenIndex(session, token);
			adjustIndex(value, null);
		}
	}



	// ------------------- 索引操作

	/**
	 * 调整索引
	 *
	 * @param value 值
	 * @param session 可填写 null，代表使用 value 现场查询
	 * @return 调整后的索引列表
	 */
	public Map<String, Long> adjustIndex(Object value, SaSession session) {

		// 未提供则现场查询
		if(session == null) {
			session = rawSessionDelegator.getSessionById(value, false);
			if(session == null) {
				return newTempTokenMap();
			}
		}

		// 重新整理索引列表
		Map<String, Long>  tempTokenNewList = newTempTokenMap();
		ArrayList<Long> tempTokenTtlList = new ArrayList<>();
		Map<String, Long> tempTokenMap = session.get(TEMP_TOKEN_MAP, this::newTempTokenMap);
		for (Map.Entry<String, Long> entry : tempTokenMap.entrySet()) {
			long ttl = expireTimeToTtl(entry.getValue());
			if(ttl != SaTokenDao.NOT_VALUE_EXPIRE) {
				tempTokenNewList.put(entry.getKey(), entry.getValue());
				tempTokenTtlList.add(ttl);
			}
		}

		// 有则保存，无则删除
		if( ! tempTokenNewList.isEmpty()) {
			session.set(TEMP_TOKEN_MAP, tempTokenNewList);
		} else {
			rawSessionDelegator.deleteSessionById(value);
			return tempTokenNewList;
		}

		// 调整 SaSession TTL
		long maxTtl = getMaxTtl(tempTokenTtlList);
		if(maxTtl != 0) {
			session.updateTimeout(maxTtl);
		}
		return tempTokenNewList;
	}

	/**
	 * 获取指定 value 的 temp-token 列表记录
	 * @param value /
	 * @return /
	 */
	public List<String> getTempTokenList(Object value) {
		// 先调增索引再获取，否则有可能获取到的不是最新有效数据
		Map<String, Long> tempTokenMap = adjustIndex(value, null);
        return new ArrayList<>(tempTokenMap.keySet());
	}

	/**
	 * 在 SaSession 上添加临时 temp-token 索引
	 * @param session /
	 * @param token /
	 * @param timeout /
	 */
	protected void addTempTokenIndex(SaSession session, String token, long timeout) {
		Map<String, Long> tempTokenMap = session.get(TEMP_TOKEN_MAP, this::newTempTokenMap);
		if(! tempTokenMap.containsKey(token)) {
			tempTokenMap.put(token, ttlToExpireTime(timeout));
			session.set(TEMP_TOKEN_MAP, tempTokenMap);
		}
	}

	/**
	 * 在 SaSession 上删除临时 temp-token 索引
	 * @param session /
	 * @param token /
	 */
	protected void deleteTempTokenIndex(SaSession session, String token) {
		Map<String, Long> tempTokenMap = session.get(TEMP_TOKEN_MAP, this::newTempTokenMap);
		if(tempTokenMap.containsKey(token)) {
			tempTokenMap.remove(token);
			session.set(TEMP_TOKEN_MAP, tempTokenMap);
		}
	}

	/**
	 * 获取一个新的 TempTokenMap 集合
	 * @return /
	 */
	protected Map<String, Long> newTempTokenMap() {
		return new LinkedHashMap<>();
	}


	// -------- 元操作

	protected Object _getValue(String token) {
		String key = splicingTempTokenSaveKey(token);
		return SaManager.getSaTokenDao().getObject(key);
	}
	protected void _deleteToken(String token) {
		String key = splicingTempTokenSaveKey(token);
		SaManager.getSaTokenDao().deleteObject(key);
	}
	protected long _getTimeout(String token) {
		String key = splicingTempTokenSaveKey(token);
		return SaManager.getSaTokenDao().getObjectTimeout(key);
	}



	// -------- 其它

	/**
	 * 检查裁剪前缀长度
	 * @param cutPrefix /
	 */
	protected static void checkCutPrefixLength(String cutPrefix) {
		if(cutPrefix.length() >= 32) {
			throw new SaTokenException("裁剪前缀长度必须小于 32 位");
		}
	}

	/**
	 * 过期时间转 ttl (秒) 获取最大 ttl 值
	 * @param tempTokenTtlList /
	 * @return /
	 */
	protected long getMaxTtl(ArrayList<Long> tempTokenTtlList) {
		long maxTtl = 0;
		for (long ttl : tempTokenTtlList) {
			if(ttl == SaTokenDao.NEVER_EXPIRE) {
				maxTtl = SaTokenDao.NEVER_EXPIRE;
				break;
			}
			if(ttl > maxTtl) {
				maxTtl = ttl;
			}
		}
		return maxTtl;
	}

	/**
	 * 过期时间转 ttl (秒)
	 * @param expireTime /
	 * @return /
	 */
	protected long expireTimeToTtl(long expireTime) {
		if(expireTime == SaTokenDao.NEVER_EXPIRE) {
			return SaTokenDao.NEVER_EXPIRE;
		}
		if(expireTime == SaTokenDao.NOT_VALUE_EXPIRE) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		return (expireTime - System.currentTimeMillis()) / 1000;
	}

	/**
	 * ttl (秒) 转 过期时间
	 * @param ttl /
	 * @return /
	 */
	protected long ttlToExpireTime(long ttl) {
		if(ttl == SaTokenDao.NEVER_EXPIRE) {
			return SaTokenDao.NEVER_EXPIRE;
		}
		if(ttl == SaTokenDao.NOT_VALUE_EXPIRE) {
			return SaTokenDao.NOT_VALUE_EXPIRE;
		}
		return ttl * 1000 + System.currentTimeMillis();
	}

	/**
	 * 获取：在存储临时 token 数据时，应该使用的 key
	 * @param token token值
	 * @return key
	 */
	public String splicingTempTokenSaveKey(String token) {
		return SaManager.getConfig().getTokenName() + ":" + namespace + ":" + token;
	}

	/**
	 * @return jwt秘钥 (只有集成 sa-token-temp-jwt 模块时此参数才会生效)
	 */
	public String getJwtSecretKey() {
		return null;
	}

}
