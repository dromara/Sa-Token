package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoaderDefaultImpl;
import org.springframework.stereotype.Component;

@Component
public class CustomSaApiKeyDataLoader extends SaApiKeyDataLoaderDefaultImpl {
}