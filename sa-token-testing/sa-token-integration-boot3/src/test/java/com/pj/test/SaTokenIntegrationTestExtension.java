package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.servlet.util.SaTokenContextJakartaServletUtil;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * 集成测试全局清理：Sa-Token 核心 API 为静态单例，不同 Spring 上下文之间会互相污染。
 */
public class SaTokenIntegrationTestExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        if (applicationContext != null) {
            applicationContext.getBeanProvider(StpInterface.class).ifAvailable(SaManager::setStpInterface);
        }
    }
    @Override
    public void afterEach(ExtensionContext context) {
        try {
            if (StpUtil.isLogin()) {
                StpUtil.logout();
            }
        } catch (Exception ignored) {
        }
        SaTokenContextJakartaServletUtil.clearContext();
        StpUtil.setStpLogic(new StpLogic(StpUtil.TYPE));
        if (SaManager.getSaTokenDao() instanceof SaTokenDaoDefaultImpl) {
            SaTokenDaoDefaultImpl dao = (SaTokenDaoDefaultImpl) SaManager.getSaTokenDao();
            List<String> keys = new ArrayList<>(dao.timedCache.keySet());
            keys.forEach(dao::deleteObject);
        }
    }

}
