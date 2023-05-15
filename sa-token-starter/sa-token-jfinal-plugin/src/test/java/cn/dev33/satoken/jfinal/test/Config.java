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
package cn.dev33.satoken.jfinal.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.jfinal.*;
import cn.dev33.satoken.util.SaTokenConsts;
import com.jfinal.config.*;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.ISerializer;
import com.jfinal.template.Engine;

public class Config extends JFinalConfig {

    public Config(){
        //注册权限验证功能，由saToken处理请求上下文
        SaTokenContext saTokenContext = new SaTokenContextForJfinal();
        SaManager.setSaTokenContext(saTokenContext);
        //加载权限角色设置数据接口
        SaManager.setStpInterface(new StpInterfaceImpl());
        //设置token生成类型
        SaTokenConfig saTokenConfig = new SaTokenConfig();
        saTokenConfig.setTokenStyle(SaTokenConsts.TOKEN_STYLE_SIMPLE_UUID);
        saTokenConfig.setTimeout(60*60*4);  //登录有效时间4小时
        saTokenConfig.setActivityTimeout(30*60); //半小时无操作过期处理
        saTokenConfig.setIsShare(false);
        saTokenConfig.setTokenName("token");    //更改satoken的cookies名称
        SaCookieConfig saCookieConfig = new SaCookieConfig();
        saCookieConfig.setHttpOnly(true);   //开启cookies 的httponly属性
        saTokenConfig.setCookie(saCookieConfig);
        SaManager.setConfig(saTokenConfig);
    }

    @Override
    public void configConstant(Constants constants) {

    }

    @Override
    public void configRoute(Routes routes) {
        //路由扫描
        routes.scan("cn.dev33.satoken.jfinal");
    }

    @Override
    public void configEngine(Engine engine) {

    }

    @Override
    public void configPlugin(Plugins plugins) {
        //添加redis扩展
        plugins.add(createRedisPlugin("satoken",1, SaJdkSerializer.me));
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        //开启注解方式权限验证
        interceptors.add(new SaAnnotationInterceptor());
    }

    @Override
    public void configHandler(Handlers handlers) {
        //将上下文交给satoken处理
        handlers.setActionHandler(new SaTokenActionHandler());
    }

    /**
     * 创建Redis插件
     * @param name 名称
     * @param dbIndex 使用的库ID
     * @param serializer 自定义序列化方法
     * @return
     */
    private RedisPlugin createRedisPlugin(String name, Integer dbIndex, ISerializer serializer) {
        RedisPlugin redisPlugin = new RedisPlugin(name, "redis-host", 6379, 3000,"pwd",dbIndex);
        redisPlugin.setSerializer(serializer);
        return redisPlugin;
    }
    @Override
    public void onStart(){
        //增加redis缓存,需要先配置redis地址
        SaManager.setSaTokenDao(new SaTokenDaoRedis("satoken"));
    }
}
