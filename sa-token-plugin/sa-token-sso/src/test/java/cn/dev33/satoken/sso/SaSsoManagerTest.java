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
package cn.dev33.satoken.sso;

import cn.dev33.satoken.sso.config.SaSsoClientConfig;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * SSO 总控：get/set、关闭验签时打警告、懒加载
 */
public class SaSsoManagerTest {

	/** get/set Server、Client 配置应该能写回去 */
	@Test
	public void getSetConfig_roundTrip() {
		new SaSsoManager();
		SaSsoServerConfig server = new SaSsoServerConfig().setSecretKey("s");
		SaSsoClientConfig client = new SaSsoClientConfig().setClient("c");
		SaSsoManager.setServerConfig(server);
		SaSsoManager.setClientConfig(client);
		Assertions.assertSame(server, SaSsoManager.getServerConfig());
		Assertions.assertSame(client, SaSsoManager.getClientConfig());
	}

	/** isCheckSign=false 时 set 配置应该走警告打印，别去断言 stderr */
	@Test
	public void setConfig_isCheckSignFalse_printsWarning() {
		SaSsoManager.setServerConfig(new SaSsoServerConfig().setIsCheckSign(false));
		SaSsoManager.setClientConfig(new SaSsoClientConfig().setIsCheckSign(false));
		Assertions.assertFalse(SaSsoManager.getServerConfig().getIsCheckSign());
		Assertions.assertFalse(SaSsoManager.getClientConfig().getIsCheckSign());
		SaSsoManager.printNoCheckSignWarningByStartup();
		SaSsoManager.printNoCheckSignWarningByRuntime();
	}

	/** get 在已有配置时应该直接返回，懒加载分支至少把 get 跑通 */
	@Test
	public void getConfig_returnsExisting() {
		SaSsoManager.setServerConfig(new SaSsoServerConfig().setHomeRoute("/h"));
		SaSsoManager.setClientConfig(new SaSsoClientConfig().setClient("x"));
		Assertions.assertEquals("/h", SaSsoManager.getServerConfig().getHomeRoute());
		Assertions.assertEquals("x", SaSsoManager.getClientConfig().getClient());
	}

}
