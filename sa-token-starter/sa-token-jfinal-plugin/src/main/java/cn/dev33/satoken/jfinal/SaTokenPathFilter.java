package cn.dev33.satoken.jfinal;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.filter.SaFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SaTokenPathFilter implements SaFilter {

    // ------------------------ 设置此过滤器 拦截 & 放行 的路由

    /**
     * 拦截路由
     */
    public List<String> includeList = new ArrayList<>();

    /**
     * 放行路由
     */
    public List<String> excludeList = new ArrayList<>();

    @Override
    public SaTokenPathFilter addInclude(String... paths) {
        includeList.addAll(Arrays.asList(paths));
        return this;
    }

    @Override
    public SaTokenPathFilter addExclude(String... paths) {
        excludeList.addAll(Arrays.asList(paths));
        return this;
    }

    @Override
    public SaTokenPathFilter setIncludeList(List<String> pathList) {
        includeList = pathList;
        return this;
    }

    @Override
    public SaTokenPathFilter setExcludeList(List<String> pathList) {
        excludeList = pathList;
        return this;
    }


    // ------------------------ 钩子函数

    /**
     * 认证函数：每次请求执行
     */
    public SaFilterAuthStrategy auth = r -> {};

    /**
     * 异常处理函数：每次[认证函数]发生异常时执行此函数
     */
    public SaFilterErrorStrategy error = e -> {
        throw new SaTokenException(e);
    };

    /**
     * 前置函数：在每次[认证函数]之前执行
     *      <b>注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth</b>
     */
    public SaFilterAuthStrategy beforeAuth = r -> {};

    @Override
    public SaTokenPathFilter setAuth(SaFilterAuthStrategy auth) {
        this.auth = auth;
        return this;
    }

    @Override
    public SaTokenPathFilter setError(SaFilterErrorStrategy error) {
        this.error = error;
        return this;
    }

    @Override
    public SaTokenPathFilter setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
        this.beforeAuth = beforeAuth;
        return this;
    }


    /*@Override
    public void doFilter(Controller ctx, FilterChain chain) throws Throwable {
        try {
            // 执行全局过滤器
            SaRouter.match(includeList).notMatch(excludeList).check(r -> {
                beforeAuth.run(null);
                auth.run(null);
            });

        } catch (StopMatchException e) {

        } catch (Throwable e) {
            // 1. 获取异常处理策略结果
            String result = (e instanceof BackResultException) ? e.getMessage() : String.valueOf(error.run(e));
            // 2. 写入输出流
            ctx.renderText(result);
            return;
        }

        // 执行
        chain.doFilter(ctx);
    }*/
}
