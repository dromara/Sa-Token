package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoaderDefaultImpl;
import org.springframework.stereotype.Component;

@Component
public class CustomSaOAuth2DataLoader extends SaOAuth2DataLoaderDefaultImpl {
}