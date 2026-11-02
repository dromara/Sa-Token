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
package cn.dev33.satoken.quick.config;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * {@link SaQuickConfig} 字段读写与默认登录处理函数测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaQuickConfigTest {

    /** getter/setter 读写应该一致，默认值应该是文档里那套 */
    @Test
    public void gettersSetters_defaultAndOverride() {
        SaQuickConfig cfg = new SaQuickConfig();
        Assertions.assertTrue(cfg.getAuth());
        Assertions.assertEquals("sa", cfg.getName());
        Assertions.assertEquals("123456", cfg.getPwd());
        Assertions.assertFalse(cfg.getAuto());
        Assertions.assertEquals("Sa-Token 登录", cfg.getTitle());
        Assertions.assertTrue(cfg.getCopr());
        Assertions.assertEquals("/**", cfg.getInclude());
        Assertions.assertEquals("", cfg.getExclude());

        cfg.setAuth(false);
        cfg.setName("admin");
        cfg.setPwd("secret");
        cfg.setAuto(true);
        cfg.setTitle("Quick");
        cfg.setCopr(false);
        cfg.setInclude("/admin/**");
        cfg.setExclude("/open/**");

        Assertions.assertFalse(cfg.getAuth());
        Assertions.assertEquals("admin", cfg.getName());
        Assertions.assertEquals("secret", cfg.getPwd());
        Assertions.assertTrue(cfg.getAuto());
        Assertions.assertEquals("Quick", cfg.getTitle());
        Assertions.assertFalse(cfg.getCopr());
        Assertions.assertEquals("/admin/**", cfg.getInclude());
        Assertions.assertEquals("/open/**", cfg.getExclude());
        Assertions.assertTrue(cfg.toString().contains("admin"));
        Assertions.assertTrue(cfg.toString().contains("Quick"));
    }

    /** 账号或密码为空时登录处理应该返回 500 */
    @Test
    public void doLoginHandle_emptyNameOrPwd() {
        SaQuickConfig cfg = new SaQuickConfig();
        SaResult emptyName = cfg.doLoginHandle.apply("", "123456");
        Assertions.assertEquals(500, emptyName.getCode());
        Assertions.assertEquals("请输入账号和密码", emptyName.getMsg());

        SaResult emptyPwd = cfg.doLoginHandle.apply("sa", "");
        Assertions.assertEquals(500, emptyPwd.getCode());
    }

    /** 账号密码不对时应该返回错误提示 */
    @Test
    public void doLoginHandle_wrongPassword() {
        SaQuickConfig cfg = new SaQuickConfig();
        SaResult res = cfg.doLoginHandle.apply("sa", "bad");
        Assertions.assertEquals(SaResult.CODE_ERROR, res.getCode());
        Assertions.assertEquals("账号或密码输入错误", res.getMsg());
    }

    /** 账号不对、密码对时也应该返回错误提示 */
    @Test
    public void doLoginHandle_wrongName() {
        SaQuickConfig cfg = new SaQuickConfig();
        SaResult res = cfg.doLoginHandle.apply("wrong", "123456");
        Assertions.assertEquals(SaResult.CODE_ERROR, res.getCode());
        Assertions.assertEquals("账号或密码输入错误", res.getMsg());
    }

    /** 账号密码正确时应该登录成功并带上 token 信息 */
    @Test
    public void doLoginHandle_success_logsIn() {
        SaQuickConfig cfg = new SaQuickConfig();
        SaTokenContextMockUtil.setMockContext(() -> {
            SaResult res = cfg.doLoginHandle.apply("sa", "123456");
            Assertions.assertEquals(SaResult.CODE_SUCCESS, res.getCode());
            Assertions.assertTrue(StpUtil.isLogin());
            Assertions.assertEquals("sa", StpUtil.getLoginId());
            Assertions.assertNotNull(res.getData());
        });
    }

}
