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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.temp.SaTempUtil;
import cn.dev33.satoken.util.SaTokenConsts;

/**
 * 临时Token模块测试 
 * 
 * @author click33
 * @since: 2022-9-1
 */
public class SaTempTest {

    // 测试：临时Token认证模块
    @Test
    public void testSaTemp() {
    	SaTokenDao dao = SaManager.getSaTokenDao();
    	
    	// 生成token 
    	String token = SaTempUtil.createToken("group-1014", 200);
    	Assertions.assertNotNull(token);
    	// System.out.println(((SaTokenDaoDefaultImpl)SaManager.getSaTokenDao()).dataMap);
    	
    	// 解析token  
    	String value = SaTempUtil.parseToken(token, String.class);
    	Assertions.assertEquals(value, "group-1014"); 
    	Assertions.assertEquals(dao.getObject("satoken:temp-token:" + SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE + ":" + token), "group-1014");
    	
    	// 默认类型 
    	Object value3 = SaTempUtil.parseToken(token);
    	Assertions.assertEquals(value3, "group-1014"); 
    	
    	// 转换类型 
    	String value4 = SaTempUtil.parseToken(token, String.class);
    	Assertions.assertEquals(value4, "group-1014"); 
    	
    	// 过期时间 
    	long timeout = SaTempUtil.getTimeout(token);
    	Assertions.assertTrue(timeout > 195);
    	
    	// 回收token 
    	SaTempUtil.deleteToken(token);
    	String value2 = SaTempUtil.parseToken(token, String.class);
    	Assertions.assertEquals(value2, null); 
    	Assertions.assertEquals(dao.getObject("satoken:temp-token:" + SaTokenConsts.DEFAULT_TEMP_TOKEN_SERVICE + ":" + token), null);
    }

    // 测试：临时Token认证模块，带 Service 参数 
    @Test
    public void testSaTempService() {
    	SaTokenDao dao = SaManager.getSaTokenDao();
    	
    	// 生成token 
    	String token = SaTempUtil.createToken("shop", "1001", 200);
    	Assertions.assertNotNull(token);
    	// System.out.println(((SaTokenDaoDefaultImpl)SaManager.getSaTokenDao()).dataMap);
    	
    	// 解析token  
    	String value = SaTempUtil.parseToken("shop", token, String.class);
    	Assertions.assertEquals(value, "1001"); 
    	Assertions.assertEquals(dao.getObject("satoken:temp-token:" + "shop" + ":" + token), "1001");
    	
    	// 默认类型 
    	Object value3 = SaTempUtil.parseToken("shop", token);
    	Assertions.assertEquals(value3, "1001"); 
    	
    	// 转换类型 
    	String value4 = SaTempUtil.parseToken("shop", token, String.class);
    	Assertions.assertEquals(value4, "1001"); 
    	
    	// service 参数不对的情况下，无法取出 
    	String value5 = SaTempUtil.parseToken("goods", token, String.class);
    	Assertions.assertNull(value5); 
    	
    	// 过期时间 
    	long timeout = SaTempUtil.getTimeout("shop", token);
    	Assertions.assertTrue(timeout > 195);
    	
    	// 回收token 
    	SaTempUtil.deleteToken("shop", token);
    	String value2 = SaTempUtil.parseToken("shop", token, String.class);
    	Assertions.assertEquals(value2, null); 
    	Assertions.assertEquals(dao.getObject("satoken:temp-token:" + "shop" + ":" + token), null);
    }

    @Test
    public void testSaTemp2() {
    	// 秘钥默认为null 
    	String jwtSecretKey = SaManager.getSaTemp().getJwtSecretKey();
    	Assertions.assertEquals(jwtSecretKey, null); 
    }
}
