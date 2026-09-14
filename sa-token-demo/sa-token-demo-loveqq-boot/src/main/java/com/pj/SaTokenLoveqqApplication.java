package com.pj;

import cn.dev33.satoken.SaManager;
import com.kfyty.loveqq.framework.boot.K;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.BootApplication;
import com.kfyty.loveqq.framework.web.core.autoconfig.annotation.EnableWebMvc;

/**
 * Sa-Token 整合 loveqq-framework 示例
 *

 JDK 17 运行报错解决方案：

 1、右上角运行配置下拉 → Edit Configurations…
 2、选中 SaTokenLoveqqApplication
 3、Modify options（改选项）→ 勾上 Add VM options
 4、VM options 里贴这一行：
 --add-opens java.base/sun.reflect.annotation=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED
 5、Apply → 再 Run

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