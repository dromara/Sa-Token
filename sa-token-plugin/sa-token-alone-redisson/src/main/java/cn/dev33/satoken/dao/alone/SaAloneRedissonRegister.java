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

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisson;
import cn.dev33.satoken.exception.SaTokenException;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.InputStream;

/**
 * 为 Sa-Token 单独创建 RedissonClient，使权限缓存与业务缓存分离
 *
 * <p>
 *     使用方式：引入本插件并配置 sa-token.alone-redisson.config 或 sa-token.alone-redisson.file。
 *     无需再引入 sa-token-redisson / sa-token-redisson-spring-boot-starter。
 * </p>
 *
 * @author click33
 * @since 1.45.0
 */
@Configuration
@EnableConfigurationProperties(SaAloneRedissonProperties.class)
public class SaAloneRedissonRegister implements DisposableBean {

	private RedissonClient aloneClient;

	/**
	 * 使用独立连接注册 SaTokenDao，覆盖业务 RedissonClient 对应的 Dao
	 */
	@Bean
	@Primary
	public SaTokenDao saTokenDaoForAloneRedisson(SaAloneRedissonProperties properties) {
		this.aloneClient = Redisson.create(buildConfig(properties));
		return new SaTokenDaoForRedisson(aloneClient);
	}

	@Override
	public void destroy() {
		if (aloneClient != null && !aloneClient.isShuttingDown()) {
			aloneClient.shutdown();
		}
	}

	/**
	 * 解析 Redisson 原生 yaml：优先 config，其次 file
	 */
	public static Config buildConfig(SaAloneRedissonProperties properties) {
		try {
			Config config;
			if (StringUtils.hasText(properties.getConfig())) {
				config = Config.fromYAML(properties.getConfig());
			} else if (StringUtils.hasText(properties.getFile())) {
				try (InputStream in = openFile(properties.getFile())) {
					config = Config.fromYAML(in);
				}
			} else {
				throw new SaTokenException("请配置 sa-token.alone-redisson.config 或 sa-token.alone-redisson.file");
			}
			return config;
		} catch (SaTokenException e) {
			throw e;
		} catch (Exception e) {
			throw new SaTokenException("解析 sa-token.alone-redisson 配置失败", e);
		}
	}

	private static InputStream openFile(String file) throws Exception {
		String path = file.trim();
		Resource resource;
		if (path.startsWith("classpath:")) {
			resource = new ClassPathResource(path.substring("classpath:".length()));
		} else if (path.startsWith("file:")) {
			resource = new FileSystemResource(path.substring("file:".length()));
		} else {
			resource = new ClassPathResource(path);
		}
		if (!resource.exists()) {
			throw new SaTokenException("未找到 Redisson 配置文件: " + file);
		}
		return resource.getInputStream();
	}

}
