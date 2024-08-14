package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationAbstractHandler;
import cn.dev33.satoken.annotation.handler.SaCheckSafeHandler;
import com.pj.satoken.StpUserUtil;
import com.pj.satoken.custom_annotation.SaUserCheckSafe;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 注解 SaUserCheckPermission 的处理器
 *
 * @author click33
 */
@Component
public class SaUserCheckSafeHandler implements SaAnnotationAbstractHandler<SaUserCheckSafe> {

    @Override
    public Class<SaUserCheckSafe> getHandlerAnnotationClass() {
        return SaUserCheckSafe.class;
    }

    @Override
    public void checkMethod(SaUserCheckSafe at, Method method) {
        SaCheckSafeHandler._checkMethod(StpUserUtil.TYPE, at.value());
    }

}
