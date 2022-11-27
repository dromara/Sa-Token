package cn.dev33.satoken.spring;

import cn.dev33.satoken.context.SaTokenContext;
import org.springframework.context.annotation.Bean;

/**
 * Sa-Token上下文注册
 *
 * @author lishangbu
 * @date 2022/11/27
 */
public class SaTokenContextRegister {
    /**
     * 获取上下文Bean (Spring版)
     *
     * @return 容器交互Bean (Spring版)
     */
    @Bean
    public SaTokenContext getSaTokenContext() {
        return new SaTokenContextForSpring();
    }
}
