/*
 * Copyright 2020-2099 sa-token.cc
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

import com.jfinal.aop.Invocation;
import com.jfinal.config.Constants;
import com.jfinal.core.*;
import com.jfinal.kit.ReflectKit;
import com.jfinal.log.Log;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SaTokenActionHandler extends ActionHandler {
    protected boolean devMode;
    protected ActionMapping actionMapping;
    protected ControllerFactory controllerFactory;
    protected ActionReporter actionReporter;
    protected static final RenderManager renderManager = RenderManager.me();
    private static final Log log = Log.getLog(ActionHandler.class);

    protected void init(ActionMapping actionMapping, Constants constants) {
        this.actionMapping = actionMapping;
        this.devMode = constants.getDevMode();
        this.controllerFactory = constants.getControllerFactory();
        this.actionReporter = constants.getActionReporter();
    }

    /**
     * 子类覆盖 getAction 方法可以定制路由功能
     */
    protected Action getAction(String target, String[] urlPara) {
        return actionMapping.getAction(target, urlPara);
    }

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.indexOf('.') != -1) {
            return ;
        }

        isHandled[0] = true;
        String[] urlPara = {null};
        Action action = getAction(target, urlPara);

        if (action == null) {
            if (log.isWarnEnabled()) {
                String qs = request.getQueryString();
                log.warn("404 Action Not Found: " + (qs == null ? target : target + "?" + qs));
            }
            renderManager.getRenderFactory().getErrorRender(404).setContext(request, response).render();
            return ;
        }

        Controller controller = null;
        try {
            // Controller controller = action.getControllerClass().newInstance();
            controller = controllerFactory.getController(action.getControllerClass());
            CPI._init_(controller, action, request, response, urlPara[0]);
            if (resolveJson && controller.isJsonRequest()) {
                // 注入 JsonRequest 包装对象接管 request
                controller.setHttpServletRequest(jsonRequestFactory.apply(controller.getRawData(), controller.getRequest()));
            }
             //加入SaToken上下文处理
            SaControllerContext.hold(controller);
            if (devMode) {
                if (actionReporter.isReportAfterInvocation(request)) {
                    new Invocation(action, controller).invoke();
                    actionReporter.report(target, controller, action);
                } else {
                    actionReporter.report(target, controller, action);
                    new Invocation(action, controller).invoke();
                }
            }
            else {
                new Invocation(action, controller).invoke();
            }

            Render render = controller.getRender();
            if (render instanceof ForwardActionRender) {
                String actionUrl = ((ForwardActionRender)render).getActionUrl();
                if (target.equals(actionUrl)) {
                    throw new RuntimeException("The forward action url is the same as before.");
                } else {
                    handle(actionUrl, request, response, isHandled);
                }
                return ;
            }

            if (render == null) {
                render = renderManager.getRenderFactory().getDefaultRender(action.getViewPath() + action.getMethodName());
            }
            render.setContext(request, response, action.getViewPath()).render();
        }
        catch (RenderException e) {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(qs == null ? target : target + "?" + qs, e);
            }
        }
        catch (ActionException e) {
            handleActionException(target, request, response, action, e);
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                String targetInfo = (qs == null ? target : target + "?" + qs);
                String sign = ReflectKit.getMethodSignature(action.getMethod());
                log.error(sign + " : " + targetInfo, e);
            }
            renderManager.getRenderFactory().getErrorRender(500).setContext(request, response, action.getViewPath()).render();
        } finally {
            SaControllerContext.release();
            controllerFactory.recycle(controller);
        }
    }

    /**
     * 抽取出该方法是为了缩短 handle 方法中的代码量，确保获得 JIT 优化，
     * 方法长度超过 8000 个字节码时，将不会被 JIT 编译成二进制码
     * <p>
     * 通过开启 java 的 -XX:+PrintCompilation 启动参数得知，handle(...)
     * 方法(73 行代码)已被 JIT 优化，优化后的字节码长度为 593 个字节，相当于
     * 每行代码产生 8.123 个字节
     */
    private void handleActionException(String target, HttpServletRequest request, HttpServletResponse response, Action action, ActionException e) {
        int errorCode = e.getErrorCode();
        String msg = null;
        if (errorCode == 404) {
            msg = "404 Not Found: ";
        } else if (errorCode == 400) {
            msg = "400 Bad Request: ";
        } else if (errorCode == 401) {
            msg = "401 Unauthorized: ";
        } else if (errorCode == 403) {
            msg = "403 Forbidden: ";
        }

        if (msg != null) {
            if (log.isWarnEnabled()) {
                String qs = request.getQueryString();
                msg = msg + (qs == null ? target : target + "?" + qs);
                if (e.getMessage() != null) {
                    msg = msg + "\n" + e.getMessage();
                }
                log.warn(msg);
            }
        } else {
            if (log.isErrorEnabled()) {
                String qs = request.getQueryString();
                log.error(errorCode + " Error: " + (qs == null ? target : target + "?" + qs), e);
            }
        }

        e.getErrorRender().setContext(request, response, action.getViewPath()).render();
    }
}
