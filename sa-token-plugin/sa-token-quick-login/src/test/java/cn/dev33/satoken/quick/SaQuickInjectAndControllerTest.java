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
package cn.dev33.satoken.quick;

import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.quick.web.SaQuickController;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.test.SaTokenTest;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

/**
 * {@link SaQuickInject} 注入配置、{@link SaQuickController} 登录页与登录接口测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaQuickInjectAndControllerTest {

    private SaQuickConfig backup;

    /** 先把全局配置存一份 */
    @BeforeEach
    public void backup() {
        backup = SaQuickManager.getConfig();
    }

    /** 把全局配置恢复回去 */
    @AfterEach
    public void restore() {
        SaQuickManager.setConfig(backup);
    }

    /** setSaQuickConfig 应该把配置写进 SaQuickManager */
    @Test
    public void inject_setSaQuickConfig() {
        SaQuickConfig cfg = new SaQuickConfig();
        cfg.setTitle("injected");
        new SaQuickInject().setSaQuickConfig(cfg);
        Assertions.assertSame(cfg, SaQuickManager.getConfig());
        Assertions.assertEquals("injected", SaQuickManager.getConfig().getTitle());
    }

    /** saLogin 应该把当前配置塞进 model，并返回登录页模板名 */
    @Test
    public void controller_saLogin_putsConfig() {
        SaQuickController controller = new SaQuickController();
        Model model = new ExtendedModelMap();
        String view = controller.saLogin(model);
        Assertions.assertEquals("sa-login.html", view);
        Assertions.assertSame(SaQuickManager.getConfig(), model.getAttribute("cfg"));
    }

    /** doLogin 应该走配置里的登录处理函数 */
    @Test
    public void controller_doLogin_delegates() {
        SaQuickController controller = new SaQuickController();
        SaTokenContextMockUtil.setMockContext(() -> {
            SaResult ok = controller.doLogin("sa", "123456");
            Assertions.assertEquals(SaResult.CODE_SUCCESS, ok.getCode());
            Assertions.assertTrue(StpUtil.isLogin());

            SaResult bad = controller.doLogin("sa", "nope");
            Assertions.assertEquals(SaResult.CODE_ERROR, bad.getCode());
        });
    }

    /** Register 常量与默认构造应该可用 */
    @Test
    public void register_constantAndConstructor() {
        Assertions.assertEquals("sa", SaQuickRegister.CONFIG_VERSION);
        Assertions.assertNotNull(new SaQuickRegister());
    }

}
