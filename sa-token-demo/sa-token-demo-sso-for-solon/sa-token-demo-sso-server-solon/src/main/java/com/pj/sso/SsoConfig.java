package com.pj.sso;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoForRedisx;
import cn.dev33.satoken.sso.config.SaSsoServerConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.dtflys.forest.Forest;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.handle.ModelAndView;

/**
 * @author noear 2023/1/3 created
 */
@Configuration
public class SsoConfig {

    /**
     * 构建建 SaToken redis dao（如果不需要 redis；可以注释掉）
     * */
    @Bean
    public SaTokenDao saTokenDaoInit(@Inject("${sa-token.dao.redis}") SaTokenDaoForRedisx saTokenDao) {
        return saTokenDao;
    }

    // 配置SSO相关参数
    @Bean
    public void configSso(SaSsoServerConfig ssoServer) { //SaSsoConfig 已自动构建

        // 配置：未登录时返回的View
        ssoServer.notLoginView = () -> {
            return new ModelAndView("sa-login.html");
        };

        // 配置：登录处理函数
        ssoServer.doLoginHandle = (name, pwd) -> {
            // 此处仅做模拟登录，真实环境应该查询数据进行登录
            if("sa".equals(name) && "123456".equals(pwd)) {
                StpUtil.login(10001);
                return SaResult.ok("登录成功！").setData(StpUtil.getTokenValue());
            }
            return SaResult.error("登录失败！");
        };

        // 配置 Http 请求处理器
        ssoServer.sendHttp = url -> {
            try {
                System.out.println("------ 发起请求：" + url);
                String resStr = Forest.get(url).executeAsString();
                System.out.println("------ 请求结果：" + resStr);
                return resStr;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }
}
