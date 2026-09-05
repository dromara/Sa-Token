package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomSaOAuth2ServerProcessor extends SaOAuth2ServerProcessor {
}