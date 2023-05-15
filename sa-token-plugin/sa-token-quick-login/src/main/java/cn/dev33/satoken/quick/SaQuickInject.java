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
package cn.dev33.satoken.quick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.quick.web.SaQuickController;

/**
 * Quick-Bean 注入
 * 
 * @author click33
 * @since <= 1.34.0
 */
@Configuration
@Import({ SaQuickController.class, SaQuickRegister.class})
public class SaQuickInject {

	/**
	 * 注入 quick-login 配置
	 * 
	 * @param saQuickConfig 配置对象
	 */
	@Autowired(required = false)
	public void setSaQuickConfig(SaQuickConfig saQuickConfig) {
		SaQuickManager.setConfig(saQuickConfig);
	}

}
