package cn.dev33.satoken.solon;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.Aop;
import org.noear.solon.core.Plugin;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckBasic;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaCheckSafe;
import cn.dev33.satoken.basic.SaBasicTemplate;
import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.id.SaIdTemplate;
import cn.dev33.satoken.id.SaIdUtil;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.solon.integration.SaContextForSolon;
import cn.dev33.satoken.solon.integration.SaTokenMethodInterceptor;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
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
        Aop.context().beanAroundAdd(SaCheckBasic.class, SaTokenMethodInterceptor.INSTANCE);

        //集成初始化

        // 注入上下文Bean
        SaManager.setSaTokenContext(new SaContextForSolon());

        //注入配置Bean
        SaTokenConfig saTokenConfig = Solon.cfg().getBean("sa-token", SaTokenConfig.class);
        SaManager.setConfig(saTokenConfig);

        Aop.getAsyn(SaTokenConfig.class, bw -> {
            SaManager.setConfig(bw.raw());
        });


        // 注入Dao Bean
        Aop.getAsyn(SaTokenDao.class, bw -> {
            SaManager.setSaTokenDao(bw.raw());
        });

        // 注入二级上下文 Bean 
        Aop.getAsyn(SaTokenSecondContextCreator.class, bw->{
        	SaTokenSecondContextCreator raw = bw.raw();
            SaManager.setSaTokenSecondContext(raw.create());
        });
        
        // 注入侦听器 Bean
        Aop.getAsyn(SaTokenListener.class, bw->{
            SaManager.setSaTokenListener(bw.raw());
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

        // Sa-Token-Id 身份凭证模块 Bean
        Aop.getAsyn(SaIdTemplate.class, bw->{
        	SaIdUtil.saIdTemplate = bw.raw();
        });

        // Sa-Token Http Basic 认证模块 Bean 
        Aop.getAsyn(SaBasicTemplate.class, bw->{
        	SaBasicUtil.saBasicTemplate = bw.raw();
        });

        // Sa-Token JSON 转换器 Bean 
        Aop.getAsyn(SaJsonTemplate.class, bw->{
        	SaManager.setSaJsonTemplate(bw.raw());
        });

        // Sa-Token 参数签名算法 Bean 
        Aop.getAsyn(SaSignTemplate.class, bw->{
        	SaManager.setSaSignTemplate(bw.raw());
        });

        // 自定义 StpLogic 对象 
        Aop.getAsyn(StpLogic.class, bw->{
        	StpUtil.setStpLogic(bw.raw());
        });
        
    }
	
}
