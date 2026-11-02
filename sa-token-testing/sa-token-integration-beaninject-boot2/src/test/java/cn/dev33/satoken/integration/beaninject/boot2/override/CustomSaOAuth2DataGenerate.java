package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.oauth2.data.generate.SaOAuth2DataGenerateDefaultImpl;
import org.springframework.stereotype.Component;

@Component
public class CustomSaOAuth2DataGenerate extends SaOAuth2DataGenerateDefaultImpl {
}