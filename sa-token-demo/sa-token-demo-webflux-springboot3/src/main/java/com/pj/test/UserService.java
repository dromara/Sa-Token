package com.pj.test;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 模拟 Service 方法
 * @author click33
 * @since 2025/4/6
 */
@Service
public class UserService {

    public boolean isLogin() {
        System.out.println("UserService 里调用 API 测试，是否登录：" + StpUtil.isLogin());
        return StpUtil.isLogin();
    }

    public Mono<Long> findUserIdByNamePwd(String name, String pwd) {
        // ...
        return Mono.just(10001L);
    }

}