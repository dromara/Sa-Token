package com.pj.satoken;

import cn.dev33.satoken.temp.SaTempTemplate;

/**
 * 自定义临时 token 认证组件子类
 *
 * @author click33
 * @since 2025/4/9
 */
//@Component
public class MySaTempTemplate extends SaTempTemplate {

    @Override
    public String createToken(Object value, long timeout, boolean isRecordIndex) {
        System.out.println("------- 自定义一些逻辑 createToken ");
        return super.createToken(value, timeout, isRecordIndex);
    }

    @Override
    public Object parseToken(String token) {
        System.out.println("------- 自定义一些逻辑 parseToken ");
        return super.parseToken(token);
    }

}