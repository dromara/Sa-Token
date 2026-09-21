/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.plugin;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.json.SaJsonTemplateForJackson3Plain;
import cn.dev33.satoken.session.SaSessionForJackson3PlainCustomized;
import cn.dev33.satoken.strategy.SaStrategy;

/**
 * SaToken 插件安装：无类型信息 JSON 转换器（Jackson 3 版）。
 *
 * @author click33
 * @since 1.47.0
 */
public class SaTokenPluginForJackson3Plain implements SaTokenPlugin {

	@Override
	public void install() {
		SaManager.setSaJsonTemplate(new SaJsonTemplateForJackson3Plain());
		SaStrategy.instance.createSession = SaSessionForJackson3PlainCustomized::new;
		SaStrategy.instance.sessionClassType = SaSessionForJackson3PlainCustomized.class;
	}

}
