package cn.dev33.satoken.core.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.exception.NotImplException;
import cn.dev33.satoken.json.SaJsonTemplateDefaultImpl;
import cn.dev33.satoken.util.SoMap;

/**
 * json默认实现类测试 
 * 
 * @author kong
 * @since: 2022-9-1
 */
public class SaJsonTemplateDefaultImplTest {

    @Test
    public void testSaJsonTemplateDefaultImpl() {
    	SaJsonTemplateDefaultImpl saJsonTemplate = new SaJsonTemplateDefaultImpl();
    	// 组件未实现
    	Assertions.assertThrows(NotImplException.class, () -> {
    		saJsonTemplate.parseJsonToMap("{}");
    	});
    	// 组件未实现
    	Assertions.assertThrows(NotImplException.class, () -> {
    		saJsonTemplate.toJsonString(SoMap.getSoMap("name", "zhangsan"));
    	});
    }

}
