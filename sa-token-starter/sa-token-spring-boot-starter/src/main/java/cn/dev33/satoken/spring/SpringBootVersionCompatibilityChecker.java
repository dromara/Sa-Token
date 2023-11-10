package cn.dev33.satoken.spring;

import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.boot.SpringBootVersion;

public class SpringBootVersionCompatibilityChecker {

    public SpringBootVersionCompatibilityChecker() {
        String version = SpringBootVersion.getVersion();
        if (SaFoxUtil.isEmpty(version)) {
            return;
        }
        if (version.startsWith("1.") || version.startsWith("2.")) {
            return;
        }
        BootstrapMethodError error = new BootstrapMethodError("当前Spring-Boot版本为" + version + "，请尝试改用：sa-token-spring-boot3-starter");
        error.fillInStackTrace();
        throw error;
    }
}
