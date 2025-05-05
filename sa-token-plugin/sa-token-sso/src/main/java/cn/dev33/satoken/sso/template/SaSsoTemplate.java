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
package cn.dev33.satoken.sso.template;

import cn.dev33.satoken.sso.message.SaSsoMessage;
import cn.dev33.satoken.sso.message.SaSsoMessageHolder;
import cn.dev33.satoken.sso.name.ApiName;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;

/**
 * SSO 模板方法类 （公共端）
 *
 * @author click33
 * @since 1.30.0
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

	// ----------- 消息处理

	/**
	 * SSO 消息处理器 - 持有器
	 */
	public SaSsoMessageHolder messageHolder = new SaSsoMessageHolder();

	/**
	 * 处理指定消息
	 *
	 * @param message /
	 */
	public Object handleMessage(SaSsoMessage message) {
		return messageHolder.handleMessage(this, message);
	}

}
