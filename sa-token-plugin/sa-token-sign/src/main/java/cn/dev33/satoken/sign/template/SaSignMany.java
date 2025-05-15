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
package cn.dev33.satoken.sign.template;

import cn.dev33.satoken.fun.SaParamRetFunction;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.error.SaSignErrorCode;
import cn.dev33.satoken.sign.exception.SaSignException;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * API 参数签名算法 - 多实例总控类
 *
 * @author click33
 * @since 1.41.0
 */
public class SaSignMany {

	/**
	 * 根据 appid 获取 SaSignConfig，允许自定义
	 */
	public static SaParamRetFunction<String, SaSignConfig> findSaSignConfigMethod = (appid) -> {
		return SaSignManager.getSignMany().get(appid);
	};

	/**
	 * 获取 SaSignTemplate，根据 appid
	 * @param appid /
	 * @return /
	 */
	public static SaSignTemplate getSignTemplate(String appid) {

		// appid 为空，返回全局默认 SaSignTemplate
		if(SaFoxUtil.isEmpty(appid)){
			return SaSignManager.getSaSignTemplate();
		}

		// 获取 SaSignConfig
		SaSignConfig config = findSaSignConfigMethod.run(appid);
		if(config == null){
			throw new SaSignException("未找到签名配置，appid=" + appid).setCode(SaSignErrorCode.CODE_12211);
		}

		// 创建 SaSignTemplate 并返回
		return new SaSignTemplate(config);
	}

}
