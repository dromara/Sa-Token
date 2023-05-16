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
package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.util.SaFoxUtil;
import org.noear.solon.core.handle.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author noear
 * @since 1.4
 */
public class SaRequestForSolon implements SaRequest {

    protected Context ctx;

    public SaRequestForSolon(){
        ctx = Context.current();
    }

    @Override
    public Object getSource() {
        return ctx;
    }

    @Override
    public String getParam(String s) {
        return ctx.param(s);
    }

    @Override
    public List<String> getParamNames(){
        Set<String> names = ctx.paramMap().keySet();
        return new ArrayList<>(names);
    }

    /**
     * 获取 [请求体] 里提交的所有参数
     * @return 参数列表
     */
    @Override
    public Map<String, String> getParamMap(){
        return ctx.paramMap();
    }

    @Override
    public String getHeader(String s) {
        return ctx.header(s);
    }

    @Override
    public String getCookieValue(String s) {
        return ctx.cookie(s);
    }

    @Override
    public String getRequestPath() {
        return ctx.pathNew();
    }

    @Override
    public String getUrl() {
        String currDomain = SaManager.getConfig().getCurrDomain();
        if( ! SaFoxUtil.isEmpty(currDomain)) {
            return currDomain + this.getRequestPath();
        }
        return ctx.url();
    }

    @Override
    public String getMethod() {
        return ctx.method();
    }

    @Override
    public Object forward(String path) {
        ctx.forward(path);
        return null;
    }

}
