package cn.dev33.satoken.solon;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.basic.SaBasicTemplate;
import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.json.SaJsonTemplate;
import cn.dev33.satoken.listener.SaTokenEventCenter;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.log.SaLog;
import cn.dev33.satoken.same.SaSameTemplate;
import cn.dev33.satoken.sign.SaSignTemplate;
import cn.dev33.satoken.solon.model.SaContextForSolon;
import cn.dev33.satoken.solon.oauth2.SaOAuth2AutoConfigure;
import cn.dev33.satoken.solon.sso.SaSsoAutoConfigure;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.SaTempInterface;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;

/**
 * @author noear
 * @since 1.4
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) {
        // Sa-Token 日志输出 Bean
        context.getBeanAsync(SaLog.class, bean -> {
            SaManager.setLog(bean);
        });


        //注入其它 Bean
        context.lifecycle(-99, () -> {
            beanInitDo(context);
            context.beanMake(SaSsoAutoConfigure.class);
            context.beanMake(SaOAuth2AutoConfigure.class);
        });
    }

    private void beanInitDo(AopContext context) {
        // 注入上下文Bean
        SaManager.setSaTokenContext(new SaContextForSolon());

        //注入配置Bean
        SaTokenConfig saTokenConfig = Solon.cfg().getBean("sa-token", SaTokenConfig.class);
        if (saTokenConfig != null) {
            SaManager.setConfig(saTokenConfig);
        }

        context.getBeanAsync(SaTokenConfig.class, bean -> {
            SaManager.setConfig(bean);
        });

        // 注入Dao Bean
        context.getBeanAsync(SaTokenDao.class, bean -> {
            SaManager.setSaTokenDao(bean);
        });

        // 注入二级上下文 Bean
        context.getBeanAsync(SaTokenSecondContextCreator.class, bean -> {
            SaManager.setSaTokenSecondContext(bean.create());
        });

        // 注入侦听器 Bean （可以有多个）
        context.subBeansOfType(SaTokenListener.class, sl -> {
            SaTokenEventCenter.registerListener(sl);
        });


        // 注入权限认证 Bean
        context.getBeanAsync(StpInterface.class, bean -> {
            SaManager.setStpInterface(bean);
        });

        // 注入持久化 Bean
        context.getBeanAsync(SaTokenDao.class, bean -> {
            SaManager.setSaTokenDao(bean);
        });

        // 临时令牌验证模块 Bean
        context.getBeanAsync(SaTempInterface.class, bean -> {
            SaManager.setSaTemp(bean);
        });

        // Sa-Token Same-Token 模块 Bean
        context.getBeanAsync(SaSameTemplate.class, bean -> {
            SaManager.setSaSameTemplate(bean);
        });

        // Sa-Token Http Basic 认证模块 Bean
        context.getBeanAsync(SaBasicTemplate.class, bean -> {
            SaBasicUtil.saBasicTemplate = bean;
        });

        // Sa-Token JSON 转换器 Bean
        context.getBeanAsync(SaJsonTemplate.class, bean -> {
            SaManager.setSaJsonTemplate(bean);
        });

        // Sa-Token 参数签名算法 Bean
        context.getBeanAsync(SaSignTemplate.class, bean -> {
            SaManager.setSaSignTemplate(bean);
        });

        // 自定义 StpLogic 对象 //容器层面只能有一个；要多个得自己在Util上处理
        context.getBeanAsync(StpLogic.class, bean -> {
            StpUtil.setStpLogic(bean);
        });
    }
}