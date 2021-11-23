package cn.dev33.satoken.jboot.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContext;
import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.template.Engine;
import cn.dev33.satoken.jboot.SaAnnotationInterceptor;
import cn.dev33.satoken.jboot.SaTokenContextForJboot;
import cn.dev33.satoken.jboot.SaTokenDaoRedis;
import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.core.listener.JbootAppListener;

public class AtteStartListener implements JbootAppListener {
    public void onInit() {
        //注册权限验证功能，由saToken处理请求上下文
        SaTokenContext saTokenContext = new SaTokenContextForJboot();
        SaManager.setSaTokenContext(saTokenContext);
        //加载权限角色设置数据接口
        SaManager.setStpInterface(new StpInterfaceImpl());
        //增加redis缓存,需要先配置redis地址
//        SaManager.setSaTokenDao(new SaTokenDaoRedis());
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
