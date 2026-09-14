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
package cn.dev33.satoken.sso.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 过时的 model.SaSsoClientModel 还是能 new 出来
 */
public class SaSsoClientModelTest {

	/** 过时子类无参构造应该能用，并且还是 SaSsoClientInfo */
	@Test
	public void deprecatedCtor_extendsInfo() {
		SaSsoClientModel m = new SaSsoClientModel();
		m.setClient("old");
		Assertions.assertEquals("old", m.getClient());
		Assertions.assertTrue(m instanceof SaSsoClientInfo);
	}

}
