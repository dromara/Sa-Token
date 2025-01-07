package com.pj.satoken;

import cn.dev33.satoken.freemarker.dialect.SaTokenTemplateModel;
import cn.dev33.satoken.stp.StpUtil;
import freemarker.template.TemplateModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.PostConstruct;


/**
 * [Sa-Token 权限认证] 配置类
 *
 * @author click33
 */
@Configuration
public class SaTokenConfigure {

	@Autowired
	FreeMarkerConfigurer configurer;

	/**
	 * 注入 Sa-Token Freemarker 标签模板模型 对象
	 */
	@PostConstruct
	public void setSaTokenTemplateModel() throws TemplateModelException {

		// 注入 Sa-Token Freemarker 标签模板模型，使之可以在 xxx.ftl 文件中使用 sa 标签，
		// 例如：<#if sa.login()>...</#if>
		configurer.getConfiguration().setSharedVariable("sa", new SaTokenTemplateModel());

		// 注入 Sa-Token Freemarker 全局对象，使之可以在 xxx.ftl 文件中调用 StpLogic 相关方法，
		// 例如：<span>${stp.getSession().get('name')}</span>
		configurer.getConfiguration().setSharedVariable("stp", StpUtil.stpLogic);
	}

}
