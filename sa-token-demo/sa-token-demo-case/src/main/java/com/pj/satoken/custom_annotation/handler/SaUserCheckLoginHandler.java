package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationAbstractHandler;
import cn.dev33.satoken.annotation.handler.SaCheckLoginHandler;
import com.pj.satoken.StpUserUtil;
import com.pj.satoken.custom_annotation.SaUserCheckLogin;
import org.springframework.stereotype.Component;

import java.lang.reflect.AnnotatedElement;

/**
 * 注解 SaUserCheckLogin 的处理器
 *
 * @author click33
 */
@Component
public class SaUserCheckLoginHandler implements SaAnnotationAbstractHandler<SaUserCheckLogin> {

    @Override
    public Class<SaUserCheckLogin> getHandlerAnnotationClass() {
        return SaUserCheckLogin.class;
    }

    @Override
    public void checkMethod(SaUserCheckLogin at, AnnotatedElement element) {
        SaCheckLoginHandler._checkMethod(StpUserUtil.TYPE);
    }

}
