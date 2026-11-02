package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.http.SaHttpTemplateDefaultImpl;
import org.springframework.stereotype.Component;

@Component
public class CustomSaHttpTemplate extends SaHttpTemplateDefaultImpl {
}