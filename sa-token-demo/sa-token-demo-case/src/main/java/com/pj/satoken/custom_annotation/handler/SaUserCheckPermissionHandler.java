package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.annotation.handler.SaCheckPermissionHandler;
import com.pj.satoken.StpUserUtil;
import com.pj.satoken.custom_annotation.SaUserCheckPermission;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 注解 SaUserCheckPermission 的处理器
 *
 * @author click33
 */
@Component
public class SaUserCheckPermissionHandler implements SaAnnotationHandlerInterface<SaUserCheckPermission> {

    @Override
    public Class<SaUserCheckPermission> getHandlerAnnotationClass() {
        return SaUserCheckPermission.class;
    }

    @Override
    public void checkMethod(SaUserCheckPermission at, Method method) {
        SaCheckPermissionHandler._checkMethod(StpUserUtil.TYPE, at.value(), at.mode(), at.orRole());
    }

}
