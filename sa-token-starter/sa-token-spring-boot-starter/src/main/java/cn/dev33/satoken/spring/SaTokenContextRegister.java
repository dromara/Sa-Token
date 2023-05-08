package cn.dev33.satoken.spring;

import org.springframework.context.annotation.Bean;

import cn.dev33.satoken.context.SaTokenContext;

/**
 * 注册Sa-Token所需要的Bean 
 * <p> Bean 的注册与注入应该分开在两个文件中，否则在某些场景下会造成循环依赖 
 * @author click33
 *
 */
public class SaTokenContextRegister {

	/**
	 * 获取上下文Bean (Spring版)
	 * 
	 * @return 容器交互Bean (Spring版)
	 */
	@Bean
	public SaTokenContext getSaTokenContextForSpring() {
		return new SaTokenContextForSpring();
	}

}
