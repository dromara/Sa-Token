package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.serializer.impl.SaSerializerTemplateForJson;
import org.springframework.stereotype.Component;

@Component
public class CustomSaSerializerTemplate extends SaSerializerTemplateForJson {
}