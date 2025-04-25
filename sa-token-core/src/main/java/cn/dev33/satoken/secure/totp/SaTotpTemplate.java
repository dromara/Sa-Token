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

import cn.dev33.satoken.exception.TotpAuthException;
import cn.dev33.satoken.secure.SaBase32Util;
import cn.dev33.satoken.util.StrFormatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.Instant;

/**
 * TOTP 算法类，支持 生成/验证 动态一次性密码
 *
 * @author click33
 * @since 1.42.0
 */
public class SaTotpTemplate {

	/**
	 * 时间窗口步长（秒）
	 */
	public int timeStep = 30;

	/**
	 * 生成的验证码位数
	 */
	public int codeDigits = 6;

	/**
	 * 哈希算法（HmacSHA1、HmacSHA256等）
	 */
	public String hmacAlgorithm = "HmacSHA1";

	/**
	 * 密钥长度（字节，推荐16或32）
	 */
	public int secretKeyLength = 16;

	/**
	 * 构造函数 (使用默认参数)
	 */
	public SaTotpTemplate() {
	}

	/**
	 * 构造函数 (使用自定义参数)
	 *
	 * @param timeStep 时间窗口步长（秒）
	 * @param codeDigits 生成的验证码位数
	 * @param hmacAlgorithm 哈希算法（HmacSHA1、HmacSHA256等）
	 * @param secretKeyLength 密钥长度（字节，推荐16或32）
	 */
	public SaTotpTemplate(int timeStep, int codeDigits, String hmacAlgorithm, int secretKeyLength) {
		this.timeStep = timeStep;
		this.codeDigits = codeDigits;
		this.hmacAlgorithm = hmacAlgorithm;
		this.secretKeyLength = secretKeyLength;
	}


	/**
	 * 生成随机密钥（Base32编码）
	 *
	 * @return /
	 */
	public String generateSecretKey() {
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[secretKeyLength];
		random.nextBytes(bytes);
		return SaBase32Util.encodeBytesToString(bytes).replace("=", "");
	}

	/**
	 * 生成当前时间的 TOTP 验证码
	 *
	 * @param secretKey Base32 编码的密钥
	 * @return /
	 */
	public String _generateTOTP(String secretKey) {
		return _generateTOTP(secretKey, Instant.now().getEpochSecond());
	}

	/**
	 * 判断用户输入的 TOTP 是否有效
	 *
	 * @param secretKey Base32编码的密钥
	 * @param code 用户输入的验证码
	 * @param timeWindowOffset 允许的时间窗口偏移量（如1表示允许前后各1个时间窗口）
	 * @return /
	 */
	public boolean validateTOTP(String secretKey, String code, int timeWindowOffset) {
		long currentWindow = Instant.now().getEpochSecond() / timeStep;
		for (int i = -timeWindowOffset; i <= timeWindowOffset; i++) {
			String calculatedCode = _generateTOTP(secretKey, (currentWindow + i) * timeStep);
			if (calculatedCode.equals(code)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 校验用户输入的TOTP是否有效，如果无效则抛出异常
	 *
	 * @param secretKey Base32编码的密钥
	 * @param code 用户输入的验证码
	 * @param timeWindowOffset 允许的时间窗口偏移量（如1表示允许前后各1个时间窗口）
	 */
	public void checkTOTP(String secretKey, String code, int timeWindowOffset) {
		if (!validateTOTP(secretKey, code, timeWindowOffset)) {
			throw new TotpAuthException();
		}
	}

	/**
	 * 生成谷歌认证器的扫码字符串 (形如：otpauth://totp/{account}?secret={secretKey})
	 *
	 * @param account  账户名
	 * @return /
	 */
	public String generateGoogleSecretKey(String account) {
		return generateGoogleSecretKey(account, generateSecretKey());
	}

	/**
	 * 生成谷歌认证器的扫码字符串 (形如：otpauth://totp/{account}?secret={secretKey})
	 *
	 * @param account  账户名
	 * @param secretKey  TOTP 秘钥
	 * @return /
	 */
	public String generateGoogleSecretKey(String account, String secretKey) {
		return StrFormatter.format("otpauth://totp/{}?secret={}", account, secretKey);
	}

	/**
	 * 生成谷歌认证器的扫码字符串 (形如：otpauth://totp/{issuer}:{account}?secret={secretKey}&issuer={issuer})
	 *
	 * @param account  账户名
	 * @param secretKey  TOTP 秘钥
	 * @param issuer  签发者
	 * @return /
	 */
	public String generateGoogleSecretKey(String account, String issuer, String secretKey) {
		return StrFormatter.format("otpauth://totp/{}:{}?secret={}&issuer={}", issuer, account, secretKey, issuer);
	}

	protected String _generateTOTP(String secretKey, long time) {
		// Base32解码密钥
		byte[] keyBytes = SaBase32Util.decodeStringToBytes(secretKey);
		byte[] counterBytes = ByteBuffer.allocate(8).putLong(time / timeStep).array();

		try {
			// 计算HMAC哈希
			Mac hmac = Mac.getInstance(hmacAlgorithm);
			hmac.init(new SecretKeySpec(keyBytes, hmacAlgorithm));
			byte[] hash = hmac.doFinal(counterBytes);

			// 动态截断（RFC 6238）
			int offset = hash[hash.length - 1] & 0xF;
			int binary = ((hash[offset] & 0x7F) << 24)
					| ((hash[offset + 1] & 0xFF) << 16)
					| ((hash[offset + 2] & 0xFF) << 8)
					| (hash[offset + 3] & 0xFF);

			// 生成指定位数的验证码
			int otp = binary % (int) Math.pow(10, codeDigits);
			return String.format("%0" + codeDigits + "d", otp);

		} catch (GeneralSecurityException e) {
			throw new RuntimeException("TOTP生成失败", e);
		}
	}

}
