package cn.dev33.satoken.jfinal.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.jfinal.*;
import com.jfinal.config.*;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.template.Engine;

public class Config extends JFinalConfig {

    public Config(){
        //注册权限验证功能，由saToken处理请求上下文
        SaTokenContext saTokenContext = new SaTokenContextForJfinal();
        SaManager.setSaTokenContext(saTokenContext);
        //加载权限角色设置数据接口
        SaManager.setStpInterface(new StpInterfaceImpl());

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
//        plugins.add(createRedisPlugin("satoken",10));
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
     * @return
     */
    private RedisPlugin createRedisPlugin(String name, Integer dbIndex) {
        RedisPlugin redisPlugin=new RedisPlugin(name, "redis-host", 6379, 3000,"pwd",dbIndex);
        redisPlugin.setSerializer(SaJdkSerializer.me);
        return redisPlugin;
    }
    @Override
    public void onStart(){
        //增加redis缓存,需要先配置redis地址
//        SaManager.setSaTokenDao(new SaTokenDaoRedis("satoken"));
    }
}
