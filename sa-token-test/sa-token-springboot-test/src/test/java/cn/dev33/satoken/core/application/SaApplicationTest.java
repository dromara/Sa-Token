package cn.dev33.satoken.core.application;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.application.SaApplication;
import cn.dev33.satoken.context.SaHolder;

/**
 * SaApplication 存取值测试 
 * 
 * @author click33
 * @since: 2022-9-4
 */
public class SaApplicationTest {

	// 测试 
	@Test
	public void testSaApplication() {
		SaApplication application = SaHolder.getApplication();
		
		// 取值 
		application.set("age", "18");
		Assertions.assertEquals(application.get("age").toString(), "18");
		Assertions.assertEquals(application.getInt("age"), 18);
		Assertions.assertEquals(application.getLong("age"), 18L);
		Assertions.assertEquals(application.getFloat("age"), 18f);
		Assertions.assertEquals(application.getDouble("age"), 18.0);
		Assertions.assertEquals(application.getString("age"), "18");
		Assertions.assertEquals(application.get("age", 20), 18);
		Assertions.assertEquals(application.get("age2", 20), 20);
		Assertions.assertEquals(application.getString("age2"), null);
		// lambda 取值，有值时依然是原值 
		Assertions.assertEquals(application.get("age", () -> "23"), "18");
		Assertions.assertEquals(application.getInt("age"), 18);
		// lambda 取值，无值时被写入新值 
		Assertions.assertEquals(application.get("age2", () -> "23"), "23");
		Assertions.assertEquals(application.getInt("age2"), 23);

		// getModel取值 
		Assertions.assertEquals(application.getModel("age", int.class), 18);
		Assertions.assertEquals(application.getModel("age", int.class, 30), 18);
		Assertions.assertEquals(application.getModel("age3", int.class, 30), 30);
		
		// 删除值 
		application.delete("age");
		Assertions.assertNull(application.get("age"));
		
		// 是否为空 
		Assertions.assertTrue(application.valueIsNull(null));
		Assertions.assertTrue(application.valueIsNull(""));
		Assertions.assertFalse(application.valueIsNull("abc"));
		
		// 为空时才能写入 
		application.setByNull("age4", "18");
		Assertions.assertEquals(application.getInt("age4"), 18);
		application.setByNull("age4", "20");
		Assertions.assertEquals(application.getInt("age4"), 18);
		
		// 清空 
		application.clear();
		Assertions.assertEquals(application.keys().size(), 0);
		
		// 获取所有值 
		application.set("key1", "value1");
		application.set("key2", "value2");
		application.set("key3", "value3");
		Assertions.assertEquals(application.keys().size(), 3);

		// 空列表 
		application.clear();
		Assertions.assertEquals(application.keys().size(), 0);
	}
	
}
