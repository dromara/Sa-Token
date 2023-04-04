package cn.dev33.satoken.context.vertx.context;

import cn.dev33.satoken.context.SaTokenContext;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.context.vertx.model.SaRequestForVertx;
import cn.dev33.satoken.context.vertx.model.SaResponseForVertx;
import cn.dev33.satoken.context.vertx.model.SaStorageForVertx;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Enaium
 */
public class SaContextForVertx implements SaTokenContext {

  protected RoutingContext context;

  public SaContextForVertx(RoutingContext context) {
    this.context = context;
  }

  @Override
  public SaRequest getRequest() {
    return new SaRequestForVertx(context);
  }

  @Override
  public SaResponse getResponse() {
    return new SaResponseForVertx(context);
  }

  @Override
  public SaStorage getStorage() {
    return new SaStorageForVertx(context);
  }

  @Override
  public boolean matchPath(String pattern, String path) {
    return path.matches(pattern);
  }
}
