package cn.dev33.satoken.context.vertx.model;

import cn.dev33.satoken.context.model.SaRequest;
import io.vertx.core.http.Cookie;
import io.vertx.ext.web.RoutingContext;

import java.util.Set;

/**
 * @author Enaium
 */
public class SaRequestForVertx implements SaRequest {

  protected RoutingContext context;

  public SaRequestForVertx(RoutingContext context) {
    this.context = context;
  }

  @Override
  public Object getSource() {
    return context.request();
  }

  @Override
  public String getParam(String name) {
    return context.request().getParam(name);
  }

  @Override
  public String getHeader(String name) {
    return context.request().getHeader(name);
  }

  @Override
  public String getCookieValue(String name) {
    final Set<Cookie> cookies = context.request().cookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if (cookie != null && name.equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

  @Override
  public String getRequestPath() {
    return context.request().path();
  }

  @Override
  public String getUrl() {
    return context.request().uri();
  }

  @Override
  public String getMethod() {
    return context.request().method().name();
  }

  @Override
  public Object forward(String path) {
    context.reroute(path);
    return null;
  }
}
