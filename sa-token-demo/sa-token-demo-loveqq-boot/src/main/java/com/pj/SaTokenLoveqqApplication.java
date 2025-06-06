package com.pj;

import cn.dev33.satoken.SaManager;
import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.BootApplication;
import com.kfyty.loveqq.framework.web.core.autoconfig.annotation.EnableWebMvc;

/**
 * Sa-Token 整合 loveqq-framework 示例
 *
 * @author kfyty725
 */
@EnableWebMvc
@BootApplication
public class SaTokenLoveqqApplication {

	public static void main(String[] args) {
		K.run(SaTokenLoveqqApplication.class, args);
		System.out.println("\n启动成功：Sa-Token配置如下：" + SaManager.getConfig());
	}
}