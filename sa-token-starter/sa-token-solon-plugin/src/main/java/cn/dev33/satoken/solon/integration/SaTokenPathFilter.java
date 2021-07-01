package cn.dev33.satoken.solon.integration;


import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.router.SaRouter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author noear 2021/5/30 created
 */
public class SaTokenPathFilter implements Filter {
	
    // ------------------------ 设置此过滤器 拦截 & 放行 的路由

    /**
     * 拦截路由
     */
    private List<String> includeList = new ArrayList<>();

    /**
     * 放行路由
     */
    private List<String> excludeList = new ArrayList<>();

    /**
     * 添加 [拦截路由]
     * @param paths 路由
     * @return 对象自身
     */
    public SaTokenPathFilter addInclude(String... paths) {
        includeList.addAll(Arrays.asList(paths));
        return this;
    }

    /**
     * 添加 [放行路由]
     * @param paths 路由
     * @return 对象自身
     */
    public SaTokenPathFilter addExclude(String... paths) {
        excludeList.addAll(Arrays.asList(paths));
        return this;
    }

    /**
     * 写入 [拦截路由] 集合
     * @param pathList 路由集合
     * @return 对象自身
     */
    public SaTokenPathFilter setIncludeList(List<String> pathList) {
        includeList = pathList;
        return this;
    }

    /**
     * 写入 [放行路由] 集合
     * @param pathList 路由集合
     * @return 对象自身
     */
    public SaTokenPathFilter setExcludeList(List<String> pathList) {
        excludeList = pathList;
        return this;
    }

    /**
     * 获取 [拦截路由] 集合
     * @return see note
     */
    public List<String> getIncludeList() {
        return includeList;
    }

    /**
     * 获取 [放行路由] 集合
     * @return see note
     */
    public List<String> getExcludeList() {
        return excludeList;
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
     */
    public SaFilterAuthStrategy beforeAuth = r -> {};

    /**
     * 写入[认证函数]: 每次请求执行
     * @param auth see note
     * @return 对象自身
     */
    public SaTokenPathFilter setAuth(SaFilterAuthStrategy auth) {
        this.auth = auth;
        return this;
    }

    /**
     * 写入[异常处理函数]：每次[认证函数]发生异常时执行此函数
     * @param error see note
     * @return 对象自身
     */
    public SaTokenPathFilter setError(SaFilterErrorStrategy error) {
        this.error = error;
        return this;
    }

    /**
     * 写入[前置函数]：在每次[认证函数]之前执行
     * @param beforeAuth see note
     * @return 对象自身
     */
    public SaTokenPathFilter setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
        this.beforeAuth = beforeAuth;
        return this;
    }


    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        try {
            // 执行全局过滤器
            SaRouter.match(includeList, excludeList, () -> {
                beforeAuth.run(null);
                auth.run(null);
            });

        } catch (StopMatchException e) {
			
		} catch (Throwable e) {
			// 1. 获取异常处理策略结果 
			String result = (e instanceof BackResultException) ? e.getMessage() : String.valueOf(error.run(e));

            // 2. 写入输出流
            ctx.contentType("text/plain; charset=utf-8");
            ctx.output(result);
            return;
        }

        // 执行
        chain.doFilter(ctx);
    }
}
