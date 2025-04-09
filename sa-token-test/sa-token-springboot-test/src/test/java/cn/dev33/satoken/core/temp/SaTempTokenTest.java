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
package cn.dev33.satoken.core.temp;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.temp.SaTempUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 临时Token模块测试 
 * 
 * @author click33
 * @since 2022-9-1
 */
public class SaTempTokenTest {

    // 测试：临时Token认证模块
    @Test
    public void testSaTemp() {
    	SaTokenDao dao = SaManager.getSaTokenDao();
    	
    	// 生成token 
    	String token = SaTempUtil.createToken("group-1014", 200);
//		 System.out.println(((SaTokenDaoDefaultImpl)SaManager.getSaTokenDao()).timedCache.dataMap.keySet());
//		System.out.println("satoken:temp-token:" + ":" + token);
    	Assertions.assertNotNull(token);
		Assertions.assertEquals(dao.getObject("satoken:temp-token:" + token), "group-1014");
    	
    	// 解析token  
    	String value = SaTempUtil.parseToken(token, String.class);
    	Assertions.assertEquals(value, "group-1014");

		// 解析 token 并裁剪前缀
		long value2 = SaTempUtil.parseToken(token, "group-", Long.class);
		Assertions.assertEquals(value2, 1014);

		// 默认类型
    	Object value3 = SaTempUtil.parseToken(token);
    	Assertions.assertEquals(value3, "group-1014"); 
    	
    	// 转换类型 
    	String value4 = SaTempUtil.parseToken(token, String.class);
    	Assertions.assertEquals(value4, "group-1014"); 
    	
    	// 过期时间 
    	long timeout = SaTempUtil.getTimeout(token);
    	Assertions.assertTrue(timeout > 195);
		Assertions.assertTrue(timeout < 201);
    	
    	// 回收token 
    	SaTempUtil.deleteToken(token);
    	String value5 = SaTempUtil.parseToken(token, String.class);
        Assertions.assertNull(value5);
        Assertions.assertNull(dao.getObject("satoken:temp-token:" + ":" + token));
    }

    // 测试：临时Token认证模块索引
    @Test
    public void testSaTempIndex() {
    	SaTokenDao dao = SaManager.getSaTokenDao();
    	
    	// 生成token 
		String token1 = SaTempUtil.createToken("1001", 200, true);
		String token2 = SaTempUtil.createToken("1001", 300, true);
		String token3 = SaTempUtil.createToken("1001", 400, true);

		Assertions.assertNotNull(token1);
		Assertions.assertNotNull(token2);
		Assertions.assertNotNull(token3);
    	// System.out.println(((SaTokenDaoDefaultImpl)SaManager.getSaTokenDao()).dataMap);

    	// 解析token
    	Assertions.assertEquals(SaTempUtil.parseToken(token1, String.class), "1001");
		Assertions.assertEquals(SaTempUtil.parseToken(token2, String.class), "1001");
		Assertions.assertEquals(SaTempUtil.parseToken(token3, String.class), "1001");

		// 缓存数据比对
		Assertions.assertEquals(dao.getObject("satoken:temp-token:" + token1), "1001");
		Assertions.assertEquals(dao.getObject("satoken:temp-token:" + token2), "1001");
		Assertions.assertEquals(dao.getObject("satoken:temp-token:" + token3), "1001");

		// 索引
		List<String> tempTokenList = SaTempUtil.getTempTokenList("1001");
		Assertions.assertEquals(tempTokenList.size(), 3);
		Assertions.assertTrue(tempTokenList.contains(token1));
		Assertions.assertTrue(tempTokenList.contains(token2));
		Assertions.assertTrue(tempTokenList.contains(token3));

		long sessionTimeout = dao.getSessionTimeout("satoken:raw-session:temp-token:" + "1001");
		Assertions.assertTrue(sessionTimeout > 395);
		Assertions.assertTrue(sessionTimeout < 401);

		// 移除一个 token
		SaTempUtil.deleteToken(token3);
        Assertions.assertNull(SaTempUtil.parseToken(token3, String.class));
        Assertions.assertNull(dao.getObject("satoken:temp-token:" + token3));

		List<String> tempTokenList2 = SaTempUtil.getTempTokenList("1001");
		Assertions.assertEquals(tempTokenList2.size(), 2);
		Assertions.assertFalse(tempTokenList2.contains(token3));

		long sessionTimeout2 = dao.getSessionTimeout("satoken:raw-session:temp-token:" + "1001");
		Assertions.assertTrue(sessionTimeout2 > 295);
		Assertions.assertTrue(sessionTimeout2 < 301);

		// 新增一个 token
		String token4 = SaTempUtil.createToken("1001", -1, true);
		Assertions.assertEquals(SaTempUtil.parseToken(token4, String.class), "1001");

		List<String> tempTokenList3 = SaTempUtil.getTempTokenList("1001");
		Assertions.assertEquals(tempTokenList3.size(), 3);
		Assertions.assertTrue(tempTokenList3.contains(token4));

		long sessionTimeout4 = dao.getSessionTimeout("satoken:raw-session:temp-token:" + "1001");
        Assertions.assertEquals(-1, sessionTimeout4);
    }

    @Test
    public void testGetJwtSecretKey() {
    	// 秘钥默认为null
    	String jwtSecretKey = SaManager.getSaTempTemplate().getJwtSecretKey();
        Assertions.assertNull(jwtSecretKey);
    }

}
