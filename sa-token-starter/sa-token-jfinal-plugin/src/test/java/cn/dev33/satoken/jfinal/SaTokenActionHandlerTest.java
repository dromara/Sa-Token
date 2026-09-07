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
package cn.dev33.satoken.jfinal;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.jfinal.testsupport.JfinalTestHelper;
import cn.dev33.satoken.jfinal.testsupport.TestActionController;
import cn.dev33.satoken.test.SaTokenTest;
import com.jfinal.aop.Interceptor;
import com.jfinal.config.Constants;
import com.jfinal.core.Action;
import com.jfinal.core.ActionMapping;
import com.jfinal.core.ActionReporter;
import com.jfinal.core.ControllerFactory;
import com.jfinal.template.Engine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * {@link SaTokenActionHandler} 请求分发测试
 */
@SaTokenTest
public class SaTokenActionHandlerTest {

    private static boolean renderReady;

    private SaTokenActionHandler handler;
    private ActionMapping mapping;
    private Map<String, Action> actions;
    private Constants constants;

    /** 整个类开始前先把 JFinal RenderManager 初始化好，后面 404/500 才渲染得出来 */
    @BeforeAll
    public static void initRenderManager() {
        try {
            ServletContext servletContext = mock(ServletContext.class);
            when(servletContext.getRealPath(anyString())).thenReturn(System.getProperty("java.io.tmpdir"));
            when(servletContext.getContextPath()).thenReturn("");
            when(servletContext.getClassLoader()).thenReturn(SaTokenActionHandlerTest.class.getClassLoader());
            com.jfinal.render.RenderManager.me().init(new Engine("sa-token-jfinal-test"), new Constants(), servletContext);
            renderReady = true;
        } catch (Throwable e) {
            renderReady = false;
        }
    }

    /** 每个用例开始前准备好 handler 和路由表 */
    @BeforeEach
    public void setUp() throws Exception {
        Assertions.assertTrue(renderReady, "RenderManager 初始化失败，没法测 ActionHandler");
        actions = new HashMap<String, Action>();
        register("index");
        register("login");
        register("fwd");
        register("fwdSelf");
        register("boom");
        register("notFound");
        register("notFoundNoMsg");
        register("badRequest");
        register("unauthorized");
        register("forbidden");
        register("otherErr");
        register("badRender");
        register("noView");

        mapping = mock(ActionMapping.class);
        when(mapping.getAction(anyString(), any())).thenAnswer(invocation -> actions.get(invocation.getArgument(0)));

        constants = new Constants();
        constants.setDevMode(false);
        constants.setControllerFactory(new ControllerFactory());
        constants.setActionReporter(new ActionReporter());

        handler = new SaTokenActionHandler();
        handler.init(mapping, constants);
        SaManager.setSaTokenContext(new SaTokenContextForJfinal());
    }

    /** 每个用例结束后把 Controller 线程绑定清掉 */
    @AfterEach
    public void tearDown() {
        SaControllerContext.release();
    }

    /** 带点的路径应该直接 return，别当 action 处理 */
    @Test
    public void handle_staticLikePath_shouldIgnore() {
        boolean[] isHandled = {false};
        handler.handle("/static/app.js", JfinalTestHelper.mockRequest("/static/app.js"),
                JfinalTestHelper.mockResponse(), isHandled);
        Assertions.assertFalse(isHandled[0]);
    }

    /** 找不到 action 时应该渲染 404，有 queryString 和没有都要能走完 */
    @Test
    public void handle_missingAction_shouldRender404() {
        boolean[] isHandled = {false};
        handler.handle("/missing", JfinalTestHelper.mockRequest("/missing"),
                JfinalTestHelper.mockResponse(), isHandled);
        Assertions.assertTrue(isHandled[0]);

        HttpServletRequest request = JfinalTestHelper.mockRequest("/missing");
        when(request.getQueryString()).thenReturn("x=1");
        handler.handle("/missing", request, JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** 正常 action 应该能 renderText，并且处理完后把 Controller 上下文释放掉 */
    @Test
    public void handle_index_shouldRenderText() {
        boolean[] isHandled = {false};
        handler.handle("/index", JfinalTestHelper.mockRequest("/index"),
                JfinalTestHelper.mockResponse(), isHandled);
        Assertions.assertTrue(isHandled[0]);
        Assertions.assertNull(SaControllerContext.get());
    }

    /** 登录 action 应该能通过 hold 住的上下文把 token 写出去 */
    @Test
    public void handle_login_shouldUseSaContext() {
        handler.handle("/login", JfinalTestHelper.mockRequest("/login"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
        Assertions.assertNull(SaControllerContext.get());
    }

    /** 转发到别的 action 时应该递归 handle */
    @Test
    public void handle_forwardAction() {
        handler.handle("/fwd", JfinalTestHelper.mockRequest("/fwd"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** 转发到自己时应该炸，最后走 500 */
    @Test
    public void handle_forwardSameUrl_shouldRender500() {
        handler.handle("/fwdSelf", JfinalTestHelper.mockRequest("/fwdSelf"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** 业务异常应该渲染 500，带 queryString 时日志分支也要走到 */
    @Test
    public void handle_boom_shouldRender500() {
        handler.handle("/boom", JfinalTestHelper.mockRequest("/boom"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});

        HttpServletRequest request = JfinalTestHelper.mockRequest("/boom");
        when(request.getQueryString()).thenReturn("q=1");
        handler.handle("/boom", request, JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** ActionException 404/400/401/403 都应该能渲染对应错误页 */
    @Test
    public void handle_actionExceptions_knownCodes() {
        handler.handle("/notFound", JfinalTestHelper.mockRequest("/notFound"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
        handler.handle("/notFoundNoMsg", JfinalTestHelper.mockRequest("/notFoundNoMsg"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
        HttpServletRequest notFoundQs = JfinalTestHelper.mockRequest("/notFound");
        when(notFoundQs.getQueryString()).thenReturn("x=1");
        handler.handle("/notFound", notFoundQs, JfinalTestHelper.mockResponse(), new boolean[] {false});
        handler.handle("/badRequest", JfinalTestHelper.mockRequest("/badRequest"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
        handler.handle("/unauthorized", JfinalTestHelper.mockRequest("/unauthorized"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
        handler.handle("/forbidden", JfinalTestHelper.mockRequest("/forbidden"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** 不认识的错误码应该走 error 日志那条分支 */
    @Test
    public void handle_actionException_otherCode() {
        handler.handle("/otherErr", JfinalTestHelper.mockRequest("/otherErr"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});

        HttpServletRequest request = JfinalTestHelper.mockRequest("/otherErr");
        when(request.getQueryString()).thenReturn("q=1");
        handler.handle("/otherErr", request, JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** RenderException 应该被吃掉，别再往外抛 */
    @Test
    public void handle_renderException_shouldSwallow() {
        handler.handle("/badRender", JfinalTestHelper.mockRequest("/badRender"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});

        HttpServletRequest request = JfinalTestHelper.mockRequest("/badRender");
        when(request.getQueryString()).thenReturn("q=1");
        handler.handle("/badRender", request, JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** 没手动 render 时应该走默认 view */
    @Test
    public void handle_noView_shouldUseDefaultRender() {
        handler.handle("/noView", JfinalTestHelper.mockRequest("/noView"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** devMode 下先 report 再 invoke */
    @Test
    public void handle_devMode_reportBeforeInvoke() {
        constants.setDevMode(true);
        ActionReporter.setReportAfterInvocation(false);
        handler.init(mapping, constants);
        handler.handle("/index", JfinalTestHelper.mockRequest("/index"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** devMode 下先 invoke 再 report */
    @Test
    public void handle_devMode_reportAfterInvoke() {
        constants.setDevMode(true);
        ActionReporter.setReportAfterInvocation(true);
        handler.init(mapping, constants);
        handler.handle("/index", JfinalTestHelper.mockRequest("/index"),
                JfinalTestHelper.mockResponse(), new boolean[] {false});
    }

    /** getAction 应该把查询交给 ActionMapping */
    @Test
    public void getAction_shouldDelegate() {
        Action action = handler.getAction("/index", new String[] {null});
        Assertions.assertNotNull(action);
        Assertions.assertNull(handler.getAction("/missing", new String[] {null}));
    }

    /** 把 Controller 方法登记到假路由表里 */
    private void register(String methodName) throws Exception {
        Method method = TestActionController.class.getMethod(methodName);
        actions.put("/" + methodName, new Action("/" + methodName, "/" + methodName,
                TestActionController.class, method, methodName, new Interceptor[0], "/"));
    }
}
