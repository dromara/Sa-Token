package cn.dev33.satoken.fun;

import java.lang.reflect.Method;

/**
 * 函数式接口:检查父类方法的注解
 * 
 * @author ShiYi
 */
@FunctionalInterface
public interface SaCheckParentMethodAnnotationFunction {

    /**
     * 检查父类方法的注解
     * @param method 当前方法
     */
    void accept(Method method);
    
}
