package cn.dev33.satoken.quick;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.quick.web.SaQuickController;

/**
 * Quick-Bean 注入
 * 
 * @author click33
 * @since <= 1.34.0
 */
@Configuration
@Import({ SaQuickController.class, SaQuickRegister.class})
public class SaQuickInject {

	/**
	 * 注入 quick-login 配置
	 * 
	 * @param saQuickConfig 配置对象
	 */
	@Autowired(required = false)
	public void setSaQuickConfig(SaQuickConfig saQuickConfig) {
		SaQuickManager.setConfig(saQuickConfig);
	}

}
