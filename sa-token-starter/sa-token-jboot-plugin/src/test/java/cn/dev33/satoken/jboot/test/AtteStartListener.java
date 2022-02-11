package cn.dev33.satoken.jboot.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.config.SaCookieConfig;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.jboot.SaAnnotationInterceptor;
import cn.dev33.satoken.jboot.SaTokenContextForJboot;
import cn.dev33.satoken.jboot.SaTokenDaoRedis;
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
        SaManager.setSaTokenDao(new SaTokenDaoRedis());
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

    }

    @Override
    public void onStartFinish() {

    }

    @Override
    public void onStop() {

    }
}
