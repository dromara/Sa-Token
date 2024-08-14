package com.pj.satoken.custom_annotation.handler;

import cn.dev33.satoken.annotation.handler.SaAnnotationAbstractHandler;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.SaTokenException;
import com.pj.satoken.custom_annotation.CheckAccount;
import org.noear.solon.annotation.Component;

import java.lang.reflect.Method;

/**
 * 注解 CheckAccount 的处理器
 *
 * @author click33
 *
 */
@Component
public class CheckAccountHandler implements SaAnnotationAbstractHandler<CheckAccount> {

    // 指定这个处理器要处理哪个注解
    @Override
    public Class<CheckAccount> getHandlerAnnotationClass() {
        return CheckAccount.class;
    }

    // 每次请求校验注解时，会执行的方法
    @Override
    public void checkMethod(CheckAccount at, Method method) {
        // 获取前端请求提交的参数
        String name = SaHolder.getRequest().getParamNotNull("name");
        String pwd = SaHolder.getRequest().getParamNotNull("pwd");

        // 与注解中指定的值相比较
        if(name.equals(at.name()) && pwd.equals(at.pwd()) ) {
            // 校验通过，什么也不做
        } else {
            // 校验不通过，则抛出异常
            throw new SaTokenException("账号或密码错误，未通过校验");
        }
    }

}
