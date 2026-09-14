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
package cn.dev33.satoken.context.dubbo3.spi;

import cn.dev33.satoken.context.dubbo3.filter.SaTokenDubbo3ConsumerFilter;
import cn.dev33.satoken.context.dubbo3.filter.SaTokenDubbo3ContextFilter;
import cn.dev33.satoken.context.dubbo3.filter.SaTokenDubbo3ProviderFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

/**
 * Dubbo SPI 文件应该登记三个 Filter。
 */
public class SaTokenDubbo3FilterSpiTest {

	/** META-INF/dubbo 里三个名字应该对上三个 Filter 类 */
	@Test
	public void spiFile_registersThreeFilters() throws Exception {
		InputStream in = getClass().getClassLoader()
				.getResourceAsStream("META-INF/dubbo/org.apache.dubbo.rpc.Filter");
		Assertions.assertNotNull(in);
		Properties props = new Properties();
		try {
			props.load(in);
		} finally {
			in.close();
		}
		Assertions.assertEquals(SaTokenDubbo3ConsumerFilter.class.getName(),
				props.getProperty("saTokenDubbo3ConsumerFilter"));
		Assertions.assertEquals(SaTokenDubbo3ProviderFilter.class.getName(),
				props.getProperty("saTokenDubbo3ProviderFilter"));
		Assertions.assertEquals(SaTokenDubbo3ContextFilter.class.getName(),
				props.getProperty("saTokenDubbo3ContextFilter"));
	}

}
