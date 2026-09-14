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
package cn.dev33.satoken.context.dubbo.spi;

import cn.dev33.satoken.context.dubbo.filter.SaTokenDubboConsumerFilter;
import cn.dev33.satoken.context.dubbo.filter.SaTokenDubboContextFilter;
import cn.dev33.satoken.context.dubbo.filter.SaTokenDubboProviderFilter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Properties;

/**
 * Dubbo SPI 文件应该登记三个 Filter。
 */
public class SaTokenDubboFilterSpiTest {

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
		Assertions.assertEquals(SaTokenDubboConsumerFilter.class.getName(),
				props.getProperty("saTokenDubboConsumerFilter"));
		Assertions.assertEquals(SaTokenDubboProviderFilter.class.getName(),
				props.getProperty("saTokenDubboProviderFilter"));
		Assertions.assertEquals(SaTokenDubboContextFilter.class.getName(),
				props.getProperty("saTokenDubboContextFilter"));
	}

}
