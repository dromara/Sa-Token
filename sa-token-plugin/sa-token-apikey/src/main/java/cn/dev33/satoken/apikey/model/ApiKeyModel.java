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
package cn.dev33.satoken.apikey.model;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.error.SaErrorCode;
import cn.dev33.satoken.exception.ApiKeyException;
import cn.dev33.satoken.util.SaFoxUtil;

import java.io.Serializable;
import java.util.*;

/**
 * Model: API Key
 *
 * @author click33
 * @since 1.41.0
 */
public class ApiKeyModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * 名称
	 */
	private String title;

	/**
	 * 介绍
	 */
	private String intro;

	/**
	 * ApiKey 值
	 */
	private String apiKey;

	/**
	 * 账号 id
	 */
	private Object loginId;

	/**
	 * ApiKey 创建时间，13位时间戳
	 */
	private long createTime;

	/**
	 * ApiKey 到期时间，13位时间戳 (-1=永不过期)
	 */
	private long expiresTime;

	/**
	 * 是否有效 (true=生效, false=禁用)
	 */
	private Boolean isValid = true;

	/**
	 * 授权范围
	 */
	private List<String> scopes = new ArrayList<>();

	/**
	 * 扩展数据
	 */
	private Map<String, Object> extraData;

	/**
	 * 构造函数
	 */
	public ApiKeyModel() {
		this.createTime = System.currentTimeMillis();
	}


	// method

	/**
	 * 添加 Scope
	 * @param scope /
	 * @return /
	 */
	public ApiKeyModel addScope(String ...scope) {
		if (this.scopes == null) {
			this.scopes = new ArrayList<>();
		}
		this.scopes.addAll(Arrays.asList(scope));
		return this;
	}

	/**
	 * 添加 扩展数据
	 * @param key /
	 * @param value /
	 * @return /
	 */
	public ApiKeyModel addExtra(String key, Object value) {
		if (this.extraData == null) {
			this.extraData = new LinkedHashMap<>();
		}
		this.extraData.put(key, value);
		return this;
	}

	/**
	 * 查询扩展数据
	 */
	public Object getExtra(String key) {
		if (this.extraData == null) {
			return null;
		}
		return this.extraData.get(key);
	}

	/**
	 * 删除扩展数据
	 */
	public Object removeExtra(String key) {
		if (this.extraData == null) {
			return null;
		}
		return this.extraData.remove(key);
	}

	/**
	 * 数据自检，判断是否可以保存入库
	 */
	public void checkByCanSaved() {
		if (SaFoxUtil.isEmpty(this.apiKey)) {
			throw new ApiKeyException("ApiKey 值不可为空").setApiKey(apiKey).setCode(SaErrorCode.CODE_12304);
		}
		if (this.loginId == null) {
			throw new ApiKeyException("无效 ApiKey: " + apiKey).setApiKey(apiKey).setCode(SaErrorCode.CODE_12304);
		}
		if (this.createTime == 0) {
			throw new ApiKeyException("请指定 createTime 创建时间").setApiKey(apiKey).setCode(SaErrorCode.CODE_12304);
		}
		if (this.expiresTime == 0) {
			throw new ApiKeyException("请指定 expiresTime 过期时间").setApiKey(apiKey).setCode(SaErrorCode.CODE_12304);
		}
		if (this.isValid == null) {
			throw new ApiKeyException("请指定 isValid 是否生效").setApiKey(apiKey).setCode(SaErrorCode.CODE_12304);
		}
	}

	/**
	 * 获取：此 ApiKey 的剩余有效期（秒）, -1=永不过期
	 * @return /
	 */
	public long expiresIn() {
		if (expiresTime == SaTokenDao.NEVER_EXPIRE) {
			return SaTokenDao.NEVER_EXPIRE;
		}
		long s = (expiresTime - System.currentTimeMillis()) / 1000;
		return s < 1 ? -2 : s;
	}

	/**
	 * 判断：此 ApiKey 是否已超时
	 * @return /
	 */
	public boolean timeExpired() {
		if (expiresTime == SaTokenDao.NEVER_EXPIRE) {
			return false;
		}
		return System.currentTimeMillis() > expiresTime;
	}


	// get and set

	/**
	 * 获取 名称
	 *
	 * @return title 名称
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * 设置 名称
	 *
	 * @param title 名称
	 * @return 对象自身
	 */
	public ApiKeyModel setTitle(String title) {
		this.title = title;
		return this;
	}

	/**
	 * 获取 介绍
	 *
	 * @return intro 介绍
	 */
	public String getIntro() {
		return this.intro;
	}

	/**
	 * 设置 介绍
	 *
	 * @param intro 介绍
	 * @return 对象自身
	 */
	public ApiKeyModel setIntro(String intro) {
		this.intro = intro;
		return this;
	}

	/**
	 * 获取 ApiKey 值
	 *
	 * @return apiKey ApiKey 值
	 */
	public String getApiKey() {
		return this.apiKey;
	}

	/**
	 * 设置 ApiKey 值
	 *
	 * @param apiKey ApiKey 值
	 * @return 对象自身
	 */
	public ApiKeyModel setApiKey(String apiKey) {
		this.apiKey = apiKey;
		return this;
	}

	/**
	 * 获取 账号 id
	 *
	 * @return loginId 账号 id
	 */
	public Object getLoginId() {
		return this.loginId;
	}

	/**
	 * 设置 账号 id
	 *
	 * @param loginId 账号 id
	 * @return 对象自身
	 */
	public ApiKeyModel setLoginId(Object loginId) {
		this.loginId = loginId;
		return this;
	}

	/**
	 * 获取 ApiKey 创建时间，13位时间戳
	 *
	 * @return createTime ApiKey 创建时间，13位时间戳
	 */
	public long getCreateTime() {
		return this.createTime;
	}

	/**
	 * 设置 ApiKey 创建时间，13位时间戳
	 *
	 * @param createTime ApiKey 创建时间，13位时间戳
	 * @return 对象自身
	 */
	public ApiKeyModel setCreateTime(long createTime) {
		this.createTime = createTime;
		return this;
	}

	/**
	 * 获取 ApiKey 到期时间，13位时间戳 (-1=永不过期)
	 *
	 * @return expiresTime ApiKey 到期时间，13位时间戳 (-1=永不过期)
	 */
	public long getExpiresTime() {
		return this.expiresTime;
	}

	/**
	 * 设置 ApiKey 到期时间，13位时间戳 (-1=永不过期)
	 *
	 * @param expiresTime ApiKey 到期时间，13位时间戳 (-1=永不过期)
	 * @return 对象自身
	 */
	public ApiKeyModel setExpiresTime(long expiresTime) {
		this.expiresTime = expiresTime;
		return this;
	}

	/**
	 * 获取 是否有效 (true=生效 false=禁用)
	 *
	 * @return /
	 */
	public Boolean getIsValid() {
		return this.isValid;
	}

	/**
	 * 设置 是否有效 (true=生效 false=禁用)
	 *
	 * @param isValid /
	 * @return 对象自身
	 */
	public ApiKeyModel setIsValid(Boolean isValid) {
		this.isValid = isValid;
		return this;
	}

	/**
	 * 获取 授权范围
	 *
	 * @return scopes 授权范围
	 */
	public List<String> getScopes() {
		return this.scopes;
	}

	/**
	 * 设置 授权范围
	 *
	 * @param scopes 授权范围
	 * @return 对象自身
	 */
	public ApiKeyModel setScopes(List<String> scopes) {
		this.scopes = scopes;
		return this;
	}

	/**
	 * 获取 扩展数据
	 *
	 * @return extraData 扩展数据
	 */
	public Map<String, Object> getExtraData() {
		return this.extraData;
	}

	/**
	 * 设置 扩展数据
	 *
	 * @param extraData 扩展数据
	 * @return 对象自身
	 */
	public ApiKeyModel setExtraData(Map<String, Object> extraData) {
		this.extraData = extraData;
		return this;
	}


	@Override
	public String toString() {
		return "ApiKeyModel{" +
				"title='" + title +
				", intro='" + intro +
				", apiKey='" + apiKey +
				", loginId=" + loginId +
				", createTime=" + createTime +
				", expiresTime=" + expiresTime +
				", isValid=" + isValid +
				", scopes=" + scopes +
				", extraData=" + extraData +
				'}';
	}
}
