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
package cn.dev33.satoken.secure.totp;

import cn.dev33.satoken.SaManager;

/**
 * TOTP 工具类，支持 生成/验证 动态一次性密码
 *
 * @author click33
 * @since 1.42.0
 */
public class SaTotpUtil {

	/**
	 * 生成随机密钥（Base32编码）
	 *
	 * @return /
	 */
	public static String generateSecretKey() {
		return SaManager.getSaTotpTemplate().generateSecretKey();
	}

	/**
	 * 生成当前时间的TOTP验证码
	 *
	 * @param secretKey Base32编码的密钥
	 * @return /
	 */
	public static String generateTOTP(String secretKey) {
		return SaManager.getSaTotpTemplate()._generateTOTP(secretKey);
	}

	/**
	 * 判断用户输入的TOTP是否有效
	 *
	 * @param secretKey Base32编码的密钥
	 * @param code 用户输入的验证码
	 * @param timeWindowOffset 允许的时间窗口偏移量（如1表示允许前后各1个时间窗口）
	 * @return /
	 */
	public static boolean validateTOTP(String secretKey, String code, int timeWindowOffset) {
		return SaManager.getSaTotpTemplate().validateTOTP(secretKey, code, timeWindowOffset);
	}

	/**
	 * 校验用户输入的TOTP是否有效，如果无效则抛出异常
	 *
	 * @param secretKey Base32编码的密钥
	 * @param code 用户输入的验证码
	 * @param timeWindowOffset 允许的时间窗口偏移量（如1表示允许前后各1个时间窗口）
	 */
	public static void checkTOTP(String secretKey, String code, int timeWindowOffset) {
		SaManager.getSaTotpTemplate().checkTOTP(secretKey, code, timeWindowOffset);
	}

	/**
	 * 生成谷歌认证器的扫码字符串 (形如：otpauth://totp/{account}?secret={secretKey})
	 *
	 * @param account  账户名
	 * @return /
	 */
	public static String generateGoogleSecretKey(String account) {
		return SaManager.getSaTotpTemplate().generateGoogleSecretKey(account);
	}

	/**
	 * 生成谷歌认证器的扫码字符串 (形如：otpauth://totp/{account}?secret={secretKey})
	 *
	 * @param account  账户名
	 * @param secretKey  TOTP 秘钥
	 * @return /
	 */
	public static String generateGoogleSecretKey(String account, String secretKey) {
		return SaManager.getSaTotpTemplate().generateGoogleSecretKey(account, secretKey);
	}

}
