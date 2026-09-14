/*
 * Copyright 2020-2099 sa-token.com
 */
package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.log.SaLogForConsole;
import org.springframework.stereotype.Component;

/** 自定义 SaLog，用于验证 SaBeanInject 构造器注入。 */
@Component
public class CustomSaLog extends SaLogForConsole {
}