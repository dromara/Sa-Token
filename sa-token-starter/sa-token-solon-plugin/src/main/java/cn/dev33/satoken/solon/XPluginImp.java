package cn.dev33.satoken.solon;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.action.SaTokenAction;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.solon.integration.SaContextForSolon;
import cn.dev33.satoken.solon.integration.SaTokenMethodInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.temp.SaTempInterface;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {
    
	@Override
    public void start(SolonApp app) {
        Aop.context().beanAroundAdd(SaCheckPermission.class, SaTokenMethodInterceptor.INSTANCE);
        Aop.context().beanAroundAdd(SaCheckRole.class, SaTokenMethodInterceptor.INSTANCE);
        Aop.context().beanAroundAdd(SaCheckLogin.class, SaTokenMethodInterceptor.INSTANCE);
        Aop.context().beanAroundAdd(SaCheckSafe.class, SaTokenMethodInterceptor.INSTANCE);

        //集成初始化

        //注入配置Bean
        SaTokenConfig saTokenConfig = Solon.cfg().getBean("solon.sa-token", SaTokenConfig.class);
        SaManager.setConfig(saTokenConfig);


        //注入容器交互Bean
        SaManager.setSaTokenContext(new SaContextForSolon());

        // 注入侦听器 Bean
        Aop.getAsyn(SaTokenListener.class, bw->{
            SaManager.setSaTokenListener(bw.raw());
        });

        // 注入框架行为 Bean
        Aop.getAsyn(SaTokenAction.class, bw->{
            SaManager.setSaTokenAction(bw.raw());
        });

        // 注入权限认证 Bean
        Aop.getAsyn(StpInterface.class, bw->{
            SaManager.setStpInterface(bw.raw());
        });

        // 注入持久化 Bean
        Aop.getAsyn(SaTokenDao.class, bw->{
            SaManager.setSaTokenDao(bw.raw());
        });

        // 临时令牌验证模块 Bean
        Aop.getAsyn(SaTempInterface.class, bw->{
            SaManager.setSaTemp(bw.raw());
        });
        
    }
}
