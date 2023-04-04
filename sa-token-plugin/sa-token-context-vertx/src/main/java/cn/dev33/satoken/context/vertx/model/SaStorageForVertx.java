package cn.dev33.satoken.context.vertx.model;

import cn.dev33.satoken.context.model.SaStorage;
import io.vertx.ext.web.RoutingContext;

/**
 * @author Enaium
 */
public class SaStorageForVertx implements SaStorage {

  protected RoutingContext context;

  public SaStorageForVertx(RoutingContext context) {
    this.context = context;
  }

  @Override
  public Object getSource() {
    return context.request();
  }

  @Override
  public Object get(String key) {
    return context.request().formAttributes().get(key);
  }

  @Override
  public SaStorageForVertx set(String key, Object value) {
    context.request().formAttributes().set(key, value.toString());
    return this;
  }

  @Override
  public SaStorageForVertx delete(String key) {
    context.request().formAttributes().remove(key);
    return this;
  }
}
