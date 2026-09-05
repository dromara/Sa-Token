package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.oauth2.data.convert.SaOAuth2DataConverterDefaultImpl;
import org.springframework.stereotype.Component;

@Component
public class CustomSaOAuth2DataConverter extends SaOAuth2DataConverterDefaultImpl {
}