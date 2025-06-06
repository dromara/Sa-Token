package com.pj.test;

import cn.dev33.satoken.loveqq.boot.context.SaReactorHolder;
import cn.dev33.satoken.loveqq.boot.utils.SaTokenContextUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.kfyty.loveqq.framework.core.autoconfig.annotation.Autowired;
import com.kfyty.loveqq.framework.web.core.annotation.GetMapping;
import com.kfyty.loveqq.framework.web.core.annotation.RequestMapping;
import com.kfyty.loveqq.framework.web.core.annotation.RestController;
import com.kfyty.loveqq.framework.web.core.annotation.bind.CookieValue;
import com.kfyty.loveqq.framework.web.core.annotation.bind.RequestParam;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 测试专用 Controller
 * 本示例是基于 reactor 编写，如果是 servlet，去除 SaReactorHolder/SaTokenContextUtil 包装，直接调用 sa-token api 即可
 *
 * @author click33
 */
@RestController
@RequestMapping("/test/")
public class TestController {

    @Autowired
    UserService userService;

    // 登录测试：Controller 里调用 Sa-Token API   --- http://localhost:8081/test/login
    @GetMapping("login")
    public Mono<SaResult> login(@RequestParam(defaultValue = "10001") String id) {
        return SaReactorHolder.sync(() -> {
            StpUtil.login(id);
            return SaResult.ok("登录成功");
        });
    }

    // API测试：手动设置上下文、try-finally 形式     	--- http://localhost:8081/test/isLogin
    @GetMapping("isLogin")
    public SaResult isLogin(ServerRequest request, ServerResponse response) {
        try {
            SaTokenContextUtil.setContext(request, response);
            System.out.println("是否登录：" + StpUtil.isLogin());
            return SaResult.data(StpUtil.getTokenInfo());
        } finally {
            SaTokenContextUtil.clearContext(null);
        }
    }

    // API测试：手动设置上下文、lambda 表达式形式    	--- http://localhost:8081/test/isLogin2
    @GetMapping("isLogin2")
    public SaResult isLogin2(ServerRequest request, ServerResponse response) {
        SaResult res = SaTokenContextUtil.setContext(request, response, () -> {
            System.out.println("是否登录：" + StpUtil.isLogin());
            return SaResult.data(StpUtil.getTokenInfo());
        });
        return SaResult.data(res);
    }

    // API测试：自动设置上下文、lambda 表达式形式    	--- http://localhost:8081/test/isLogin3
    @GetMapping("isLogin3")
    public Mono<SaResult> isLogin3() {
        return SaReactorHolder.sync(() -> {
            System.out.println("是否登录：" + StpUtil.isLogin());
            userService.isLogin();
            return SaResult.data(StpUtil.getTokenInfo());
        });
    }

    // API测试：自动设置上下文、调用 userService Mono 方法     	--- http://localhost:8081/test/isLogin4
    @GetMapping("isLogin4")
    public Mono<SaResult> isLogin4() {
        return userService.findUserIdByNamePwd("ZhangSan", "123456")
                .flatMap(userId -> SaReactorHolder.sync(() -> {
                    StpUtil.login(userId);
                    return SaResult.data(StpUtil.getTokenInfo());
                }));
    }

    // API测试：切换线程、复杂嵌套调用 	--- http://localhost:8081/test/isLogin5
    @GetMapping("isLogin5")
    public Mono<SaResult> isLogin5() {
        System.out.println("线程id-----" + Thread.currentThread().getId());
        // 要点：在流里调用 Sa-Token API 之前，必须用 SaReactorHolder.sync( () -> {} ) 进行包裹
        return Mono.delay(Duration.ofSeconds(1))
                .doOnNext(r -> System.out.println("线程id-----" + Thread.currentThread().getId()))
                .map(r -> SaReactorHolder.sync(() -> userService.isLogin()))
                .map(r -> userService.findUserIdByNamePwd("ZhangSan", "123456"))
                .map(r -> SaReactorHolder.sync(() -> userService.isLogin()))
                .flatMap(isLogin -> {
                    System.out.println("是否登录 " + isLogin);
                    return SaReactorHolder.sync(() -> {
                        System.out.println("是否登录 " + StpUtil.isLogin());
                        return SaResult.data(StpUtil.getTokenInfo());
                    });
                });
    }

    // API测试：使用上下文无关的API 	--- http://localhost:8081/test/isLogin6
    @GetMapping("isLogin6")
    public SaResult isLogin6(@CookieValue("satoken") String satoken) {
        System.out.println("token 为：" + satoken);
        System.out.println("登录人：" + StpUtil.getLoginIdByToken(satoken));
        return SaResult.ok("登录人：" + StpUtil.getLoginIdByToken(satoken));
    }

    // 测试   浏览器访问： http://localhost:8081/test/test
    @GetMapping("test")
    public SaResult test() {
        System.out.println("线程id------- " + Thread.currentThread().getId());
        return SaResult.ok();
    }
}
