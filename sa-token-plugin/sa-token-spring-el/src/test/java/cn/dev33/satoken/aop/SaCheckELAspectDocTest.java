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
package cn.dev33.satoken.aop;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckEL;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link SaCheckELAspect} 官方文档示例用法测试（https://sa-token.com/plugin/spel-at.html）
 *
 * <p> 说明：mock 上下文在 beforeEach 中为每个用例独立创建（请求存储器等可变状态互不污染），
 * 用例结束后清理；登录动作不统一放在 beforeEach，因为 doc_stpCheckLogin / doc_stpCheckPermission
 * 需要先断言"未登录"时的行为，登录只能发生在用例内部。
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaCheckELAspectDocTest {

    private SaCheckELAspect aspect;

    @BeforeEach
    public void beforeEach() {
        aspect = new SaCheckELAspect();
        // 注册一个测试 Bean，供表达式通过 @beanName 语法引用
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("testBean", new TestBean());
        aspect.setBeanFactory(beanFactory);
        // 为当前用例创建独立的 mock 请求上下文
        SaTokenContextMockUtil.setMockContext();
    }

    @AfterEach
    public void afterEach() {
        SaTokenContextMockUtil.clearContext();
        // 恢复默认的 rootMap 扩展函数，避免影响其它用例
        SaAnnotationStrategy.instance.checkELRootMapExtendFunction = rootMap -> {
        };
    }

    /** 将目标对象与切面一起织入，返回代理对象 */
    private <T> T proxy(T target) {
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.addAspect(aspect);
        return factory.getProxy();
    }

    /** 注入指定权限集合到当前账号体系 */
    private void injectPermissions(String... permissions) {
        List<String> list = Collections.unmodifiableList(Arrays.asList(permissions));
        SaManager.setStpInterface(new StpInterface() {
            @Override
            public List<String> getPermissionList(Object loginId, String loginType) {
                return list;
            }

            @Override
            public List<String> getRoleList(Object loginId, String loginType) {
                return Collections.emptyList();
            }
        });
    }

    /** 文档示例：stp.checkLogin()，未登录时应抛 NotLoginException，登录后放行 */
    @Test
    public void doc_stpCheckLogin() {
        StpDocService proxy = proxy(new StpDocService());
        Assertions.assertThrows(NotLoginException.class, proxy::checkLogin);
        StpUtil.login(10001);
        Assertions.assertDoesNotThrow(proxy::checkLogin);
    }

    /** 文档示例：stp.checkPermission('user:edit')，权限不足时抛 NotPermissionException，权限满足后放行 */
    @Test
    public void doc_stpCheckPermission() {
        StpDocService proxy = proxy(new StpDocService());
        Assertions.assertThrows(NotLoginException.class, proxy::checkPermission);
        StpUtil.login(10001);
        Assertions.assertThrows(NotPermissionException.class, proxy::checkPermission);
        injectPermissions("user:edit");
        Assertions.assertDoesNotThrow(proxy::checkPermission);
    }

    /** 文档示例：NEED( stp.getSession().get('name') == 'zhangsan' )，Session 取值比对 */
    @Test
    public void doc_stpSessionValue() {
        StpDocService proxy = proxy(new StpDocService());
        StpUtil.login(10001);
        // Session 未放值时校验不通过
        Assertions.assertThrows(SaTokenException.class, proxy::checkSessionName);
        StpUtil.getSession().set("name", "zhangsan");
        Assertions.assertDoesNotThrow(proxy::checkSessionName);
    }

    /** 文档示例：stp.checkPermission( this.permissionCode )，this 引用本类成员变量作为权限码 */
    @Test
    public void doc_thisMemberField() {
        StpDocService proxy = proxy(new StpDocService());
        StpUtil.login(10001);
        Assertions.assertThrows(NotPermissionException.class, proxy::checkPermissionFromThis);
        injectPermissions("article:add");
        Assertions.assertDoesNotThrow(proxy::checkPermissionFromThis);
    }

    /** 文档示例：NEED( #name.length() > 3 )，通过 #参数名 引用方法入参 */
    @Test
    public void doc_paramReference() {
        StpDocService proxy = proxy(new StpDocService());
        Assertions.assertThrows(SaTokenException.class, () -> proxy.checkName("ab"));
        Assertions.assertDoesNotThrow(() -> proxy.checkName("zhangsan"));
    }

    /** 文档示例：通过扩展函数注册自定义账号体系根对象 stpUser，实现多账号体系鉴权 */
    @Test
    public void doc_customStpRoot() {
        StpLogic stpUser = new StpLogic("user");
        SaAnnotationStrategy.instance.checkELRootMapExtendFunction = rootMap -> rootMap.put("stpUser", stpUser);
        StpDocService proxy = proxy(new StpDocService());
        Assertions.assertThrows(NotLoginException.class, () -> proxy.checkUserLogin());
        stpUser.login(20001);
        Assertions.assertDoesNotThrow(() -> proxy.checkUserLogin());
    }

    /** 测试目标：官方文档示例的 stp 用法 */
    public static class StpDocService {

        /** 文档示例：登录校验 */
        @SaCheckEL("stp.checkLogin()")
        public void checkLogin() {
        }

        /** 文档示例：权限校验 */
        @SaCheckEL("stp.checkPermission('user:edit')")
        public void checkPermission() {
        }

        /** 文档示例：Session 取值比对 */
        @SaCheckEL("NEED( stp.getSession().get('name') == 'zhangsan' )")
        public void checkSessionName() {
        }

        /** 文档示例：this 引用本类成员变量 */
        @SaCheckEL("stp.checkPermission( this.permissionCode )")
        public void checkPermissionFromThis() {
        }

        /** 文档示例：#参数名 引用方法入参 */
        @SaCheckEL("NEED( #name.length() > 3 )")
        public void checkName(String name) {
        }

        /** 文档示例：自定义账号体系根对象（由扩展函数注册） */
        @SaCheckEL("stpUser.checkLogin()")
        public void checkUserLogin() {
        }

        /** 本类成员变量：供 this.permissionCode 引用 */
        public String permissionCode = "article:add";
    }

    /** 测试用 Bean：供表达式 @testBean 引用 */
    public static class TestBean {

        public String getName() {
            return "zhang";
        }
    }

}
