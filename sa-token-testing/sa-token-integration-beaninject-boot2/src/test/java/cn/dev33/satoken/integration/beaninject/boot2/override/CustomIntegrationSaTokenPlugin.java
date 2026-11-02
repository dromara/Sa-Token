package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.plugin.SaTokenPlugin;
import org.springframework.stereotype.Component;

@Component
public class CustomIntegrationSaTokenPlugin implements SaTokenPlugin {
    @Override public void install() { }
}