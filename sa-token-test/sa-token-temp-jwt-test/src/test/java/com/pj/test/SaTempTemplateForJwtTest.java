package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.ApiDisabledException;
import cn.dev33.satoken.temp.SaTempUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Sa-Token 整合 temp jwt
 *
 * @author click33
 * @since 1.42.0
 */
@SpringBootTest(classes = StartUpApplication.class)
public class SaTempTemplateForJwtTest {

	// 开始 
	@BeforeAll
    public static void beforeClass() {
    	System.out.println("\n\n------------------------ SaTempTemplateForJwtTest star ...");
    }

	// 结束 
    @AfterAll
    public static void afterClass() {
    	System.out.println("\n\n------------------------ SaTempTemplateForJwtTest end ... \n");
    }

	// 测试：临时Token认证模块
	@Test
	public void testSaTemp() {

		// 生成token
		String token = SaTempUtil.createToken("group-1014", 200);
		//		 System.out.println(((SaTokenDaoDefaultImpl)SaManager.getSaTokenDao()).timedCache.dataMap.keySet());
		//		System.out.println("satoken:temp-token:" + ":" + token);
		Assertions.assertNotNull(token);

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
		Assertions.assertThrows(ApiDisabledException.class, () -> SaTempUtil.deleteToken(token) );
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
        Assertions.assertNull(dao.getObject("satoken:temp-token:" + token1));
        Assertions.assertNull(dao.getObject("satoken:temp-token:" + token2));
        Assertions.assertNull(dao.getObject("satoken:temp-token:" + token3));

		// 索引
		Assertions.assertThrows(ApiDisabledException.class, () -> SaTempUtil.getTempTokenList("1001") );
	}

	@Test
	public void testGetJwtSecretKey() {
		// 秘钥默认为null
		String jwtSecretKey = SaManager.getSaTempTemplate().getJwtSecretKey();
		Assertions.assertNotNull(jwtSecretKey);
	}

}
