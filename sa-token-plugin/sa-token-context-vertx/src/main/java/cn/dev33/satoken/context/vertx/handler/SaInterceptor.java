package cn.dev33.satoken.context.vertx.handler;

import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.filter.SaFilterAuthStrategy;
import cn.dev33.satoken.filter.SaFilterErrorStrategy;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.context.vertx.error.ErrorCode;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Enaium
 */
public class SaInterceptor implements Handler<RoutingContext> {

  // ------------------------ 设置此处理器 拦截 & 放行 的路由

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
   *
   * @param paths 路由
   * @return 对象自身
   */
  public SaInterceptor addInclude(String... paths) {
    includeList.addAll(Arrays.asList(paths));
    return this;
  }

  /**
   * 添加 [放行路由]
   *
   * @param paths 路由
   * @return 对象自身
   */
  public SaInterceptor addExclude(String... paths) {
    excludeList.addAll(Arrays.asList(paths));
    return this;
  }

  /**
   * 写入 [拦截路由] 集合
   *
   * @param pathList 路由集合
   * @return 对象自身
   */
  public SaInterceptor setIncludeList(List<String> pathList) {
    includeList = pathList;
    return this;
  }

  /**
   * 写入 [放行路由] 集合
   *
   * @param pathList 路由集合
   * @return 对象自身
   */
  public SaInterceptor setExcludeList(List<String> pathList) {
    excludeList = pathList;
    return this;
  }

  /**
   * 获取 [拦截路由] 集合
   *
   * @return see note
   */
  public List<String> getIncludeList() {
    return includeList;
  }

  /**
   * 获取 [放行路由] 集合
   *
   * @return see note
   */
  public List<String> getExcludeList() {
    return excludeList;
  }


  // ------------------------ 钩子函数

  /**
   * 认证函数：每次请求执行
   */
  public SaFilterAuthStrategy auth = r -> {
  };

  /**
   * 异常处理函数：每次[认证函数]发生异常时执行此函数
   */
  public SaFilterErrorStrategy error = e -> {
    throw new SaTokenException(e).setCode(ErrorCode.CODE_20205);
  };

  /**
   * 前置函数：在每次[认证函数]之前执行
   */
  public SaFilterAuthStrategy beforeAuth = r -> {
  };

  /**
   * 写入[认证函数]: 每次请求执行
   *
   * @param auth see note
   * @return 对象自身
   */
  public SaInterceptor setAuth(SaFilterAuthStrategy auth) {
    this.auth = auth;
    return this;
  }

  /**
   * 写入[异常处理函数]：每次[认证函数]发生异常时执行此函数
   *
   * @param error see note
   * @return 对象自身
   */
  public SaInterceptor setError(SaFilterErrorStrategy error) {
    this.error = error;
    return this;
  }

  /**
   * 写入[前置函数]：在每次[认证函数]之前执行
   *
   * @param beforeAuth see note
   * @return 对象自身
   */
  public SaInterceptor setBeforeAuth(SaFilterAuthStrategy beforeAuth) {
    this.beforeAuth = beforeAuth;
    return this;
  }


  // ------------------------ handle
  @Override
  public void handle(RoutingContext context) {
    try {
      SaRouter.match(includeList).notMatch(excludeList).check(r -> {
        beforeAuth.run(context);
        auth.run(context);
      });
    } catch (StopMatchException ignored) {

    } catch (Throwable e) {
      String result = (e instanceof BackResultException) ? e.getMessage() : String.valueOf(error.run(e));

      // 2. 写入输出流
      if (context.response().headers().get("Content-Type") == null) {
        context.response().headers().set("Content-Type", "text/plain; charset=utf-8");
      }
      context.response().end(result);
      return;
    }
    context.next();
  }
}
