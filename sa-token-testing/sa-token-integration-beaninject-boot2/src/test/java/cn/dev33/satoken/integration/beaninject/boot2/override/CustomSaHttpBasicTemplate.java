package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.httpauth.basic.SaHttpBasicTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomSaHttpBasicTemplate extends SaHttpBasicTemplate {
}