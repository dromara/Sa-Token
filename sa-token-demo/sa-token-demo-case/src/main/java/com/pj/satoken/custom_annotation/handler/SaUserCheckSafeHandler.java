package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.annotation.handler.SaCheckSafeHandler;
import com.pj.satoken.StpUserUtil;
import com.pj.satoken.custom_annotation.SaUserCheckSafe;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;

/**
 * 注解 SaUserCheckPermission 的处理器
 *
 * @author click33
 */
@Component
public class SaUserCheckSafeHandler implements SaAnnotationHandlerInterface<SaUserCheckSafe> {

    @Override
    public Class<SaUserCheckSafe> getHandlerAnnotationClass() {
        return SaUserCheckSafe.class;
    }

    @Override
    public void checkMethod(SaUserCheckSafe at, AnnotatedElement element) {
        SaCheckSafeHandler._checkMethod(StpUserUtil.TYPE, at.value());
    }

}
