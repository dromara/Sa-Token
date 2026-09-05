package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomSaHttpDigestTemplate extends SaHttpDigestTemplate {
}