package cn.dev33.satoken.aop;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.strategy.SaAnnotationStrategy;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token AOP 环绕切入 Bean 注册
 * <p>
 *    参考资料：<br>
 *    https://www.jb51.net/program/297714rev.htm    <br>
 *    https://www.bilibili.com/video/BV1WZ421W7Qx   <br>
 *    https://blog.csdn.net/Tomwildboar/article/details/139199801   <br>
 * </p>
 *
 * @author click33
 * @since 2024/8/3
 */
@Configuration
public class SaAopPointcutAdvisorBeanRegister {

    /**
     * Advisor 静态全局引用
     */
    public static SaAroundAnnotationPointcutAdvisor saAroundAnnoAdvisor;

    @Bean
    public SaAroundAnnotationPointcutAdvisor saAroundAnnotationHandlePointcutAdvisor (List<SaAnnotationHandlerInterface<?>> handlerList) {
        SaAroundAnnotationPointcutAdvisor advisor = new SaAroundAnnotationPointcutAdvisor();

        // 定义切入规则
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        String expression = calcExpression(handlerList);
        pointcut.setExpression(expression);
        advisor.setPointcut(pointcut);

        // 定义执行的方法 
        advisor.setAdvice(new SaAroundAnnotationMethodInterceptor());

        // 保存全局引用
        SaAopPointcutAdvisorBeanRegister.saAroundAnnoAdvisor = advisor;
        return advisor;
    }

    /**
     * 计算切入表达式
     * @param appendHandlerList 追加的 SaAnnotationAbstractHandler 处理器
     * @return /
     */
    public static String calcExpression(List<SaAnnotationHandlerInterface<?>> appendHandlerList) {

        // 框架内置的
        List<Class<?>> list = new ArrayList<>(SaAnnotationStrategy.instance.annotationHandlerMap.keySet());

        // 额外追加的
        if(appendHandlerList != null) {
            for (SaAnnotationHandlerInterface<?> handler : appendHandlerList) {
                Class<?> cls = handler.getHandlerAnnotationClass();
                if(!list.contains(cls)) {
                    list.add(handler.getHandlerAnnotationClass());
                }
            }
        }

        // 计算
        return calcClassListExpression(list);
    }

    /**
     * 计算 class 列表的切入表达式，
     * 最终样例形如：
     <pre>
         public static final String POINTCUT_SIGN =
         "@within(cn.dev33.satoken.annotation.SaCheckLogin) || @annotation(cn.dev33.satoken.annotation.SaCheckLogin) || "
         + "@within(cn.dev33.satoken.annotation.SaCheckRole) || @annotation(cn.dev33.satoken.annotation.SaCheckRole) || "
         + "@within(cn.dev33.satoken.annotation.SaCheckPermission) || @annotation(cn.dev33.satoken.annotation.SaCheckPermission)";
     </pre>
     * @param list /
     * @return /
     */
    public static String calcClassListExpression(List<Class<?>> list) {
        String pointcutExpression = "";
        for (Class<?> cls : list) {
            if(!pointcutExpression.isEmpty()) {
                pointcutExpression += " || ";
            }
            pointcutExpression += "@within(" + cls.getName() + ") || @annotation(" + cls.getName() + ")";
        }
        return pointcutExpression;
    }


}