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
package cn.dev33.satoken.loveqq.boot.sign;

import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.config.SaSignManyConfigWrapper;
import cn.dev33.satoken.sign.template.SaSignTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

/**
 * {@link SaSignBeanInject} 注入方法测试
 */
public class SaSignBeanInjectTest {

	/** 单套配置、many 配置和模板应该写进 SaSignManager */
	@Test
	public void injectMethods_shouldWriteIntoManager() {
		SaSignBeanInject inject = new SaSignBeanInject();

		SaSignConfig config = new SaSignConfig();
		inject.setSignConfig(config);
		Assertions.assertSame(config, SaSignManager.getConfig());

		SaSignManyConfigWrapper wrapper = new SaSignManyConfigWrapper();
		wrapper.setSignMany(new LinkedHashMap<String, SaSignConfig>());
		inject.setSignManyConfig(wrapper);
		Assertions.assertSame(wrapper.getSignMany(), SaSignManager.getSignMany());

		SaSignTemplate template = new SaSignTemplate();
		inject.setSaSignTemplate(template);
		Assertions.assertSame(template, SaSignManager.getSaSignTemplate());
	}

}
