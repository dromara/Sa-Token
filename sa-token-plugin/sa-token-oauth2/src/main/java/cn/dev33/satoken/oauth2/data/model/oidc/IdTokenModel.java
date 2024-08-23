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
package cn.dev33.satoken.oauth2.data.model.oidc;

import java.io.Serializable;
import java.util.Map;

/**
 * OIDC IdToken Model
 *
 * <br/> 参考：
 * <br/> <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken">IDToken</a>
 * <br/> <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims">StandardClaims</a>
 *
 * @author click33
 * @since 1.23.0
 */
public class IdTokenModel implements Serializable {

	private static final long serialVersionUID = -6541180061782004705L;

	/**
	 * 必填：发行者标识符，例如：https://server.example.com
	 */
	public String iss;

	/**
	 * 必填：用户标识符，用户id，例如：10001
	 */
	public Object sub;

	/**
	 * 必填：客户端标识符，clientId，例如：s6BhdRkqt3
	 */
	public String aud;

	/**
	 * 必填：令牌到期时间，10位时间戳，例如：1723341795
	 */
	public long exp;

	/**
	 * 必填：签发此令牌的时间，10位时间戳，例如：1723339995
	 */
	public long iat;

	/**
	 * 用户认证时间，10位时间戳，例如：1723339988
	 */
	public long authTime;

	/**
	 * 随机数，客户端提供，防止重放攻击，例如：e9a3f4d9
	 */
	public String nonce;

	/**
	 * 身份验证上下文类引用
	 */
	public String acr;

	/**
	 * 身份验证方法参考
	 */
	public String amr;

	/**
	 * 授权方 - 签发 ID 令牌的一方，如果存在，它必须包含此方的 OAuth 2.0 客户端 ID。
	 */
	public String azp;

	/**
	 * 扩展数据
	 */
	public Map<String, Object> extraData;

}
