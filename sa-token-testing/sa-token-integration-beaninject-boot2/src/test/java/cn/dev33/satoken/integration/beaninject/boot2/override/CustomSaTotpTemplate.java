package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomSaTotpTemplate extends SaTotpTemplate {
}