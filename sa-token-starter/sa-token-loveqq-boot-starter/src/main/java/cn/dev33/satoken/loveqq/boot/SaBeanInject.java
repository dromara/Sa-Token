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
package cn.dev33.satoken.loveqq.boot;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.http.SaHttpTemplate;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicTemplate;
import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestTemplate;
import cn.dev33.satoken.httpauth.digest.SaHttpDigestUtil;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.loveqq.boot.support.SaPathMatcherHolder;
import cn.dev33.satoken.plugin.SaTokenPlugin;
import cn.dev33.satoken.plugin.SaTokenPluginHolder;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.secure.totp.SaTotpTemplate;
import cn.dev33.satoken.serializer.SaSerializerTemplate;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import cn.dev33.satoken.strategy.SaFirewallStrategy;
import cn.dev33.satoken.strategy.SaStrategy;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHook;
import cn.dev33.satoken.temp.SaTempTemplate;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Component;
import com.kfyty.loveqq.framework.core.support.PatternMatcher;

import java.util.List;

/**
 * 注入 Sa-Token 所需要的 Bean
 *
 * @author click33
 * @since 1.34.0
 */
@Component
public class SaBeanInject {

    /**
     * 组件注入
     * <p> 为确保 Log 组件正常打印，必须将 SaLog 和 SaTokenConfig 率先初始化 </p>
     *
     * @param log           log 对象
     * @param saTokenConfig 配置对象
     */
    public SaBeanInject(@Autowired(required = false) SaLog log,
                        @Autowired(required = false) SaTokenConfig saTokenConfig,
                        @Autowired(required = false) SaTokenPluginHolder pluginHolder) {
        if (log != null) {
            SaManager.setLog(log);
        }

        if (saTokenConfig != null) {
            SaManager.setConfig(saTokenConfig);
        }

        // 初始化 Sa-Token SPI 插件
        if (pluginHolder == null) {
            pluginHolder = SaTokenPluginHolder.instance;
        }

        pluginHolder.init();

        SaTokenPluginHolder.instance = pluginHolder;
    }

    /**
     * 注入持久化Bean
     *
     * @param saTokenDao SaTokenDao对象
     */
    @Autowired(required = false)
    public void setSaTokenDao(SaTokenDao saTokenDao) {
        SaManager.setSaTokenDao(saTokenDao);
    }

    /**
     * 注入权限认证Bean
     *
     * @param stpInterface StpInterface对象
     */
    @Autowired(required = false)
    public void setStpInterface(StpInterface stpInterface) {
        SaManager.setStpInterface(stpInterface);
    }

    /**
     * 注入上下文Bean
     *
     * @param saTokenContext SaTokenContext对象
     */
    @Autowired(required = false)
    public void setSaTokenContext(SaTokenContext saTokenContext) {
        SaManager.setSaTokenContext(saTokenContext);
    }

    /**
     * 注入侦听器Bean
     *
     * @param listenerList 侦听器集合
     */
    @Autowired(required = false)
    public void setSaTokenListener(List<SaTokenListener> listenerList) {
        SaTokenEventCenter.registerListenerList(listenerList);
    }

    /**
     * 注入自定义注解处理器
     *
     * @param handlerList 自定义注解处理器集合
     */
    @Autowired(required = false)
    public void setSaAnnotationHandler(List<SaAnnotationHandlerInterface<?>> handlerList) {
        for (SaAnnotationHandlerInterface<?> handler : handlerList) {
            SaAnnotationStrategy.instance.registerAnnotationHandler(handler);
        }
    }

    /**
     * 注入临时令牌验证模块 Bean
     *
     * @param saTempTemplate /
     */
    @Autowired(required = false)
    public void setSaTempTemplate(SaTempTemplate saTempTemplate) {
        SaManager.setSaTempTemplate(saTempTemplate);
    }

    /**
     * 注入 Same-Token 模块 Bean
     *
     * @param saSameTemplate saSameTemplate对象
     */
    @Autowired(required = false)
    public void setSaIdTemplate(SaSameTemplate saSameTemplate) {
        SaManager.setSaSameTemplate(saSameTemplate);
    }

    /**
     * 注入 Sa-Token Http Basic 认证模块
     *
     * @param saBasicTemplate saBasicTemplate对象
     */
    @Autowired(required = false)
    public void setSaHttpBasicTemplate(SaHttpBasicTemplate saBasicTemplate) {
        SaHttpBasicUtil.saHttpBasicTemplate = saBasicTemplate;
    }

    /**
     * 注入 Sa-Token Http Digest 认证模块
     *
     * @param saHttpDigestTemplate saHttpDigestTemplate 对象
     */
    @Autowired(required = false)
    public void setSaHttpDigestTemplate(SaHttpDigestTemplate saHttpDigestTemplate) {
        SaHttpDigestUtil.saHttpDigestTemplate = saHttpDigestTemplate;
    }

    /**
     * 注入自定义的 JSON 转换器 Bean
     *
     * @param saJsonTemplate JSON 转换器
     */
    @Autowired(required = false)
    public void setSaJsonTemplate(SaJsonTemplate saJsonTemplate) {
        SaManager.setSaJsonTemplate(saJsonTemplate);
    }

    /**
     * 注入自定义的 Http 转换器 Bean
     *
     * @param saHttpTemplate /
     */
    @Autowired(required = false)
    public void setSaHttpTemplate(SaHttpTemplate saHttpTemplate) {
        SaManager.setSaHttpTemplate(saHttpTemplate);
    }

    /**
     * 注入自定义的序列化器 Bean
     *
     * @param saSerializerTemplate 序列化器
     */
    @Autowired(required = false)
    public void setSaSerializerTemplate(SaSerializerTemplate saSerializerTemplate) {
        SaManager.setSaSerializerTemplate(saSerializerTemplate);
    }

    /**
     * 注入自定义的 TOTP 算法 Bean
     *
     * @param totpTemplate TOTP 算法类
     */
    @Autowired(required = false)
    public void setSaTotpTemplate(SaTotpTemplate totpTemplate) {
        SaManager.setSaTotpTemplate(totpTemplate);
    }

    /**
     * 注入自定义的 StpLogic
     *
     * @param stpLogic /
     */
    @Autowired(required = false)
    public void setStpLogic(StpLogic stpLogic) {
        StpUtil.setStpLogic(stpLogic);
    }

    /**
     * 利用自动注入特性，获取Spring框架内部使用的路由匹配器
     *
     * @param pathMatcher 要设置的 pathMatcher
     */
    @Autowired(required = false)
    public void setPathMatcher(PatternMatcher pathMatcher) {
        SaPathMatcherHolder.setPathMatcher(pathMatcher);
    }

    /**
     * 注入自定义防火墙校验 hook 集合
     *
     * @param hooks /
     */
    @Autowired(required = false)
    public void setSaFirewallCheckHooks(List<SaFirewallCheckHook> hooks) {
        for (SaFirewallCheckHook hook : hooks) {
            SaFirewallStrategy.instance.registerHook(hook);
        }
    }

    /**
     * 注入CORS 策略处理函数
     *
     * @param corsHandle /
     */
    @Autowired(required = false)
    public void setCorsHandle(SaCorsHandleFunction corsHandle) {
        SaStrategy.instance.corsHandle = corsHandle;
    }

    /**
     * 注入自定义插件集合
     *
     * @param plugins /
     */
    @Autowired(required = false)
    public void setSaTokenPluginList(List<SaTokenPlugin> plugins) {
        for (SaTokenPlugin plugin : plugins) {
            SaTokenPluginHolder.instance.installPlugin(plugin);
        }
    }
}
