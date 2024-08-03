package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationAbstractHandler;
import cn.dev33.satoken.annotation.handler.SaCheckRoleHandler;
import com.pj.satoken.StpUserUtil;
import com.pj.satoken.custom_annotation.SaUserCheckRole;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;

/**
 * 注解 SaUserCheckRole 的处理器
 *
 * @author click33
 */
@Component
public class SaUserCheckRoleHandler implements SaAnnotationAbstractHandler<SaUserCheckRole> {

    @Override
    public Class<SaUserCheckRole> getHandlerAnnotationClass() {
        return SaUserCheckRole.class;
    }

    @Override
    public void checkMethod(SaUserCheckRole at, AnnotatedElement element) {
        SaCheckRoleHandler._checkMethod(StpUserUtil.TYPE, at.value(), at.mode());
    }

}
