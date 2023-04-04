package cn.dev33.satoken.context.vertx.handler;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.vertx.context.SaContextForVertx;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Enaium
 */
public class SaContext implements Handler<RoutingContext> {
  @Override
  public void handle(RoutingContext context) {
    SaManager.setSaTokenContext(new SaContextForVertx(context));
    context.next();
  }
}
