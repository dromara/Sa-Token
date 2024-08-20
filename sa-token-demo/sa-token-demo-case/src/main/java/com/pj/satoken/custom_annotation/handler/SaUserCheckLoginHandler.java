package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationHandlerInterface;
import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import com.pj.satoken.StpUserUtil;
import com.pj.satoken.custom_annotation.SaUserCheckLogin;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 注解 SaUserCheckLogin 的处理器
 *
 * @author click33
 */
@Component
public class SaUserCheckLoginHandler implements SaAnnotationHandlerInterface<SaUserCheckLogin> {

    @Override
    public Class<SaUserCheckLogin> getHandlerAnnotationClass() {
        return SaUserCheckLogin.class;
    }

    @Override
    public void checkMethod(SaUserCheckLogin at, Method method) {
        SaCheckLoginHandler._checkMethod(StpUserUtil.TYPE);
    }

}
