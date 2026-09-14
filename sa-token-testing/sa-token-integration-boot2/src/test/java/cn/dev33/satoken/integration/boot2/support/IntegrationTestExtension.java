/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.integration.boot2.support;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.servlet.util.SaTokenContextServletUtil;
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
 * 集成测试全局清理扩展：Sa-Token 核心 API 为静态单例，用例之间必须隔离状态。
 */
public class IntegrationTestExtension implements BeforeEachCallback, AfterEachCallback {

    /** 从 Spring 容器同步 StpInterface 到 SaManager，保证注解鉴权读到测试配置 */
    @Override
    public void beforeEach(ExtensionContext context) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        if (applicationContext != null) {
            applicationContext.getBeanProvider(StpInterface.class).ifAvailable(SaManager::setStpInterface);
        }
    }

    /** 每个用例结束后登出、清上下文、重置 StpLogic 并清空内存 DAO 缓存 */
    @Override
    public void afterEach(ExtensionContext context) {
        try {
            if (StpUtil.isLogin()) {
                StpUtil.logout();
            }
        } catch (Exception ignored) {
        }
        SaTokenContextServletUtil.clearContext();
        StpUtil.setStpLogic(new StpLogic(StpUtil.TYPE));
        if (SaManager.getSaTokenDao() instanceof SaTokenDaoDefaultImpl) {
            SaTokenDaoDefaultImpl dao = (SaTokenDaoDefaultImpl) SaManager.getSaTokenDao();
            List<String> keys = new ArrayList<>(dao.timedCache.keySet());
            keys.forEach(dao::deleteObject);
        }
    }

}
