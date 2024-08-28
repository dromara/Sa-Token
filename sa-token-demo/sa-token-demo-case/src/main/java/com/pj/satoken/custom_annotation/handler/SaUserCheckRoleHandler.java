package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.annotation.handler.SaCheckRoleHandler;
import com.pj.satoken.StpUserUtil;
import com.pj.satoken.custom_annotation.SaUserCheckRole;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 注解 SaUserCheckRole 的处理器
 *
 * @author click33
 */
@Component
public class SaUserCheckRoleHandler implements SaAnnotationHandlerInterface<SaUserCheckRole> {

    @Override
    public Class<SaUserCheckRole> getHandlerAnnotationClass() {
        return SaUserCheckRole.class;
    }

    @Override
    public void checkMethod(SaUserCheckRole at, Method method) {
        SaCheckRoleHandler._checkMethod(StpUserUtil.TYPE, at.value(), at.mode());
    }

}
