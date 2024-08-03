package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationAbstractHandler;
import cn.dev33.satoken.annotation.handler.SaCheckPermissionHandler;
import com.pj.satoken.StpUserUtil;
import com.pj.satoken.custom_annotation.SaUserCheckPermission;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;

/**
 * 注解 SaUserCheckPermission 的处理器
 *
 * @author click33
 */
@Component
public class SaUserCheckPermissionHandler implements SaAnnotationAbstractHandler<SaUserCheckPermission> {

    @Override
    public Class<SaUserCheckPermission> getHandlerAnnotationClass() {
        return SaUserCheckPermission.class;
    }

    @Override
    public void checkMethod(SaUserCheckPermission at, AnnotatedElement element) {
        SaCheckPermissionHandler._checkMethod(StpUserUtil.TYPE, at.value(), at.mode(), at.orRole());
    }

}
