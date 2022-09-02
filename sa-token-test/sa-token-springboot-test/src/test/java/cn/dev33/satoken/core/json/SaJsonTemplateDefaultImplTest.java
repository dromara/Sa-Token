package cn.dev33.satoken.core.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.exception.ApiDisabledException;
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
    	// API 禁用
    	Assertions.assertThrows(ApiDisabledException.class, () -> {
    		saJsonTemplate.parseJsonToMap("{}");
    	});
    	// API 禁用
    	Assertions.assertThrows(ApiDisabledException.class, () -> {
    		saJsonTemplate.toJsonString(SoMap.getSoMap("name", "zhangsan"));
    	});
    }

}
