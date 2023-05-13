package cn.dev33.satoken.context.vertx.model;

import cn.dev33.satoken.context.model.SaResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Enaium
 */
public class SaResponseForVertx implements SaResponse {
  protected RoutingContext context;

  public SaResponseForVertx(RoutingContext context) {
    this.context = context;
  }

  @Override
  public Object getSource() {
    return context.response();
  }

  @Override
  public SaResponse setStatus(int sc) {
    context.response().setStatusCode(sc);
    return this;
  }

  @Override
  public SaResponse setHeader(String name, String value) {
    context.response().headers().set(name, value);
    return this;
  }

  @Override
  public SaResponse addHeader(String name, String value) {
    context.response().headers().add(name, value);
    return this;
  }

  @Override
  public Object redirect(String url) {
    context.redirect(url);
    return null;
  }
}
