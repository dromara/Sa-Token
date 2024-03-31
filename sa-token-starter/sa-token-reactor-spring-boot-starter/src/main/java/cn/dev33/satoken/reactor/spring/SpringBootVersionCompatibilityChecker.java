package cn.dev33.satoken.reactor.spring;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.boot.SpringBootVersion;

/**
 * SpringBoot 版本与 Sa-Token 版本兼容检查器
 *
 * @author Uncarbon
 * @since 1.38.0
 */
public class SpringBootVersionCompatibilityChecker {

    public SpringBootVersionCompatibilityChecker() {
        String version = SpringBootVersion.getVersion();
        if (SaFoxUtil.isEmpty(version) || version.startsWith("1.") || version.startsWith("2.")) {
            return;
        }
        String str = "当前 SpringBoot 版本（" + version + "）与 Sa-Token 依赖不兼容，" +
                "请将依赖 sa-token-reactor-spring-boot-starter 修改为：sa-token-reactor-spring-boot3-starter";
        System.err.println(str);
        throw new SaTokenException(str);
    }
}
