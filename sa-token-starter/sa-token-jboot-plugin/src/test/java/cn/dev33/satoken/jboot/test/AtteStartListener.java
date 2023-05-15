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
package cn.dev33.satoken.jboot.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.jboot.SaAnnotationInterceptor;
import cn.dev33.satoken.jboot.SaTokenCacheDao;
import cn.dev33.satoken.jboot.SaTokenContextForJboot;
import cn.dev33.satoken.util.SaTokenConsts;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.core.listener.JbootAppListener;

public class AtteStartListener implements JbootAppListener {
    public void onInit() {
        SaTokenContext saTokenContext = new SaTokenContextForJboot();
        SaManager.setSaTokenContext(saTokenContext);
        SaManager.setStpInterface(new StpInterfaceImpl());
        SaTokenConfig saTokenConfig = new SaTokenConfig();
        saTokenConfig.setTokenStyle(SaTokenConsts.TOKEN_STYLE_SIMPLE_UUID);
        saTokenConfig.setTimeout(60*60*4);  //登录有效时间4小时
        saTokenConfig.setActivityTimeout(30*60); //半小时无操作过期处理
        saTokenConfig.setIsShare(false);
        saTokenConfig.setTokenName("token");    //更换satoken的名称
        saTokenConfig.setCookie(new SaCookieConfig().setHttpOnly(true));    //开启cookies的httponly属性
        SaManager.setConfig(saTokenConfig);
    }

    @Override
    public void onConstantConfig(Constants constants) {

    }

    @Override
    public void onRouteConfig(Routes routes) {

    }

    @Override
    public void onEngineConfig(Engine engine) {

    }

    @Override
    public void onPluginConfig(JfinalPlugins plugins) {

    }

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        //开启注解方式权限验证
        interceptors.add(new SaAnnotationInterceptor());
    }

    @Override
    public void onHandlerConfig(JfinalHandlers handlers) {

    }

    @Override
    public void onStartBefore() {

    }

    @Override
    public void onStart() {
        SaManager.setSaTokenDao(new SaTokenCacheDao("sa"));
    }

    @Override
    public void onStartFinish() {

    }

    @Override
    public void onStop() {

    }
}
