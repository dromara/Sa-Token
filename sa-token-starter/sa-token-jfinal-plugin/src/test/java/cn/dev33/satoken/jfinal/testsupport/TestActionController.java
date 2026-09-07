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
package cn.dev33.satoken.jfinal.testsupport;

import cn.dev33.satoken.stp.StpUtil;
import com.jfinal.core.ActionException;
import com.jfinal.core.Controller;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

/**
 * ActionHandler 单测用的 JFinal Controller，覆盖正向、转发、异常几条路径。
 */
public class TestActionController extends Controller {

    /** 正常输出一段文本 */
    public void index() {
        renderText("index-ok");
    }

    /** 走一遍 StpUtil.login，用来确认上下文 hold 住了 */
    public void login() {
        StpUtil.login(10001);
        renderText("login-ok");
    }

    /** 转发到 /index */
    public void fwd() {
        forwardAction("/index");
    }

    /** 转发到自己，按实现会抛 RuntimeException */
    public void fwdSelf() {
        forwardAction("/fwdSelf");
    }

    /** 业务里直接炸，走 500 分支 */
    public void boom() {
        throw new RuntimeException("boom");
    }

    /** 抛 404 ActionException */
    public void notFound() {
        throw new ActionException(404, noopRender(), "missing");
    }

    /** 抛 404 但不带 message，打日志时不该再拼异常文案 */
    public void notFoundNoMsg() {
        throw new ActionException(404, noopRender());
    }

    /** 抛 400 ActionException */
    public void badRequest() {
        throw new ActionException(400, noopRender(), "bad");
    }

    /** 抛 401 ActionException */
    public void unauthorized() {
        throw new ActionException(401, noopRender(), "unauth");
    }

    /** 抛 403 ActionException */
    public void forbidden() {
        throw new ActionException(403, noopRender(), "forbid");
    }

    /** 抛个不在 404/400/401/403 里的错误码，走 error 日志分支 */
    public void otherErr() {
        throw new ActionException(418, noopRender(), "teapot");
    }

    /** render 阶段炸 RenderException */
    public void badRender() {
        render(new Render() {
            @Override
            public void render() {
                throw new RenderException("bad-render");
            }
        });
    }

    /** 不调 render，让 Handler 走默认 view */
    public void noView() {
    }

    /** ActionException 只要错误码，不走模板文件 */
    private Render noopRender() {
        return new Render() {
            @Override
            public void render() {
            }
        };
    }
}
