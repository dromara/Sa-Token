package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.oauth2.data.resolver.SaOAuth2DataResolverDefaultImpl;
import org.springframework.stereotype.Component;

@Component
public class CustomSaOAuth2DataResolver extends SaOAuth2DataResolverDefaultImpl {
}