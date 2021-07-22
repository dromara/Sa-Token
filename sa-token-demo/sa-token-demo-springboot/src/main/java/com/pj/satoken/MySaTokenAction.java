package com.pj.satoken;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.action.SaTokenActionDefaultImpl;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 继承Sa-Token行为Bean默认实现, 重写部分逻辑 
 */
@Component
public class MySaTokenAction extends SaTokenActionDefaultImpl {
    // 重写token生成策略 
    @Override
    public String createToken(Object loginId, String loginType) {
        return SaFoxUtil.getRandomString(60);    // 随机60位字符串
    }
}
