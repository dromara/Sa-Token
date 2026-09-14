package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomSaCheckLoginHandler extends SaCheckLoginHandler {
}