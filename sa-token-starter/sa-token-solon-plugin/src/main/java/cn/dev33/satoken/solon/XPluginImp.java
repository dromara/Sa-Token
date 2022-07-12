package cn.dev33.satoken.solon;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.*;
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
import cn.dev33.satoken.solon.model.SaContextForSolon;
import cn.dev33.satoken.solon.integration.SaTokenAnnotationInterceptor;
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
    public void start(AopContext context) {
        context.beanAroundAdd(SaCheckPermission.class, SaTokenAnnotationInterceptor.INSTANCE);
        context.beanAroundAdd(SaCheckRole.class, SaTokenAnnotationInterceptor.INSTANCE);
        context.beanAroundAdd(SaCheckLogin.class, SaTokenAnnotationInterceptor.INSTANCE);
        context.beanAroundAdd(SaCheckSafe.class, SaTokenAnnotationInterceptor.INSTANCE);
        context.beanAroundAdd(SaCheckBasic.class, SaTokenAnnotationInterceptor.INSTANCE);

        //集成初始化

        // 注入上下文Bean
        SaManager.setSaTokenContext(new SaContextForSolon());

        //注入配置Bean
        SaTokenConfig saTokenConfig = Solon.cfg().getBean("sa-token", SaTokenConfig.class);
        SaManager.setConfig(saTokenConfig);

        context.getWrapAsyn(SaTokenConfig.class, bw -> {
            SaManager.setConfig(bw.raw());
        });


        // 注入Dao Bean
        context.getWrapAsyn(SaTokenDao.class, bw -> {
            SaManager.setSaTokenDao(bw.raw());
        });

        // 注入二级上下文 Bean
        context.getWrapAsyn(SaTokenSecondContextCreator.class, bw->{
            SaTokenSecondContextCreator raw = bw.raw();
            SaManager.setSaTokenSecondContext(raw.create());
        });

        // 注入侦听器 Bean
        context.getWrapAsyn(SaTokenListener.class, bw->{
            SaManager.setSaTokenListener(bw.raw());
        });

        // 注入权限认证 Bean
        context.getWrapAsyn(StpInterface.class, bw->{
            SaManager.setStpInterface(bw.raw());
        });

        // 注入持久化 Bean
        context.getWrapAsyn(SaTokenDao.class, bw->{
            SaManager.setSaTokenDao(bw.raw());
        });

        // 临时令牌验证模块 Bean
        context.getWrapAsyn(SaTempInterface.class, bw->{
            SaManager.setSaTemp(bw.raw());
        });

        // Sa-Token-Id 身份凭证模块 Bean
        context.getWrapAsyn(SaIdTemplate.class, bw->{
            SaIdUtil.saIdTemplate = bw.raw();
        });

        // Sa-Token Http Basic 认证模块 Bean
        context.getWrapAsyn(SaBasicTemplate.class, bw->{
            SaBasicUtil.saBasicTemplate = bw.raw();
        });

        // Sa-Token JSON 转换器 Bean
        context.getWrapAsyn(SaJsonTemplate.class, bw->{
            SaManager.setSaJsonTemplate(bw.raw());
        });

        // Sa-Token 参数签名算法 Bean
        context.getWrapAsyn(SaSignTemplate.class, bw->{
            SaManager.setSaSignTemplate(bw.raw());
        });

        // 自定义 StpLogic 对象
        context.getWrapAsyn(StpLogic.class, bw->{
            StpUtil.setStpLogic(bw.raw());
        });
    }
}