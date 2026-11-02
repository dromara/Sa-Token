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
package cn.dev33.satoken.dao.alone;

import cn.dev33.satoken.exception.SaTokenException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.config.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * {@link SaAloneRedissonRegister} / {@link SaAloneRedissonProperties} 独立 Redisson 连接测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaAloneRedissonRegisterTest {

	/** getter/setter 应该能把 file、config 存回去 */
	@Test
	void properties_shouldKeepFileAndConfig() {
		SaAloneRedissonProperties properties = new SaAloneRedissonProperties();
		properties.setFile("classpath:sa-redisson.yml");
		properties.setConfig("singleServerConfig:\n  address: redis://127.0.0.1:6379");
		Assertions.assertEquals("classpath:sa-redisson.yml", properties.getFile());
		Assertions.assertEquals("singleServerConfig:\n  address: redis://127.0.0.1:6379", properties.getConfig());
		Assertions.assertEquals("sa-token.alone-redisson", SaAloneRedissonProperties.PREFIX);
	}

	/** 配了 config 时应该优先按这段 yaml 解析 */
	@Test
	void buildConfig_shouldPreferInlineConfig() {
		SaAloneRedissonProperties properties = new SaAloneRedissonProperties();
		properties.setFile("missing.yml");
		properties.setConfig("singleServerConfig:\n  address: redis://127.0.0.1:6379\n");
		Config config = SaAloneRedissonRegister.buildConfig(properties);
		Assertions.assertNotNull(config.useSingleServer().getAddress());
	}

	/** file 以 classpath: 开头时应该从类路径读 yaml */
	@Test
	void buildConfig_shouldReadClasspathFile() {
		SaAloneRedissonProperties properties = new SaAloneRedissonProperties();
		properties.setFile("classpath:sa-alone-redisson.yml");
		Config config = SaAloneRedissonRegister.buildConfig(properties);
		Assertions.assertTrue(config.useSingleServer().getAddress().contains("127.0.0.1"));
	}

	/** file 不带前缀时也应该当类路径资源读 */
	@Test
	void buildConfig_shouldReadBareClasspathFile() {
		SaAloneRedissonProperties properties = new SaAloneRedissonProperties();
		properties.setFile("sa-alone-redisson.yml");
		Config config = SaAloneRedissonRegister.buildConfig(properties);
		Assertions.assertTrue(config.useSingleServer().getAddress().contains("127.0.0.1"));
	}

	/** file 以 file: 开头时应该从磁盘读 yaml */
	@Test
	void buildConfig_shouldReadFileUrl() throws IOException {
		Path temp = Files.createTempFile("sa-alone-redisson-", ".yml");
		Files.write(temp, "singleServerConfig:\n  address: redis://127.0.0.1:6380\n".getBytes(StandardCharsets.UTF_8));
		SaAloneRedissonProperties properties = new SaAloneRedissonProperties();
		properties.setFile("file:" + temp.toAbsolutePath());
		Config config = SaAloneRedissonRegister.buildConfig(properties);
		Assertions.assertTrue(config.useSingleServer().getAddress().contains("6380"));
	}

	/** 找不到配置文件时应该抛 SaTokenException */
	@Test
	void buildConfig_shouldThrowWhenFileMissing() {
		SaAloneRedissonProperties properties = new SaAloneRedissonProperties();
		properties.setFile("classpath:not-exist-redisson.yml");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> SaAloneRedissonRegister.buildConfig(properties));
		Assertions.assertTrue(ex.getMessage().contains("未找到 Redisson 配置文件"));
	}

	/** config 和 file 都没配时应该提示补配置 */
	@Test
	void buildConfig_shouldThrowWhenConfigAndFileMissing() {
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class,
				() -> SaAloneRedissonRegister.buildConfig(new SaAloneRedissonProperties()));
		Assertions.assertTrue(ex.getMessage().contains("请配置"));
	}

	/** yaml 解析失败时应该包成 SaTokenException */
	@Test
	void buildConfig_shouldWrapInvalidYaml() {
		SaAloneRedissonProperties properties = new SaAloneRedissonProperties();
		properties.setConfig(":::not-yaml:::");
		SaTokenException ex = Assertions.assertThrows(SaTokenException.class, () -> SaAloneRedissonRegister.buildConfig(properties));
		Assertions.assertTrue(ex.getMessage().contains("解析 sa-token.alone-redisson 配置失败"));
	}

	/** 没创建过客户端时 destroy 应该直接返回 */
	@Test
	void destroy_shouldIgnoreNullClient() {
		Assertions.assertDoesNotThrow(() -> new SaAloneRedissonRegister().destroy());
	}

}
