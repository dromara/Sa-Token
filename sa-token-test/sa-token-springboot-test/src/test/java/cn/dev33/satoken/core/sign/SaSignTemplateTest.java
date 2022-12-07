package cn.dev33.satoken.core.sign;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.util.SoMap;

/**
 * API 接口签名测试 
 * 
 * @author kong
 * @since: 2022-9-2
 */
public class SaSignTemplateTest {

	String key = "SwqFmsKxcbq23";

	// 连接参数列表 
	@Test
	public void testJoinParamsDictSort() {
		SoMap map = SoMap.getSoMap()
				.set("name", "zhang")
				.set("age", 18)
				.set("sex", "女");
		String str = SaManager.getSaSignTemplate().joinParamsDictSort(map);

		// 按照音序排列 
		Assertions.assertEquals(str, "age=18&name=zhang&sex=女");
	}
	
	// 给参数签名 
	@Test
	public void testCreateSign() {
		SoMap map = SoMap.getSoMap()
				.set("name", "zhang")
				.set("age", 18)
				.set("sex", "女");
		String sign = SaManager.getSaSignTemplate().createSign(map, key);
		Assertions.assertEquals(sign, "6f5e844a53e74363c2f6b24f64c4f0ff");
		
		// 多次签名，结果一致  
		String sign2 = SaManager.getSaSignTemplate().createSign(map, key);
		Assertions.assertEquals(sign, sign2);
	}
	
}
