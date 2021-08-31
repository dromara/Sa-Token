package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.context.model.SaStorage;
import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.4
 */
public class SaStorageForSolon implements SaStorage {
   
	protected Context ctx;

    public SaStorageForSolon() {
        ctx = Context.current();
    }

    @Override
    public Object getSource() {
        return ctx;
    }

    @Override
    public void set(String key, Object value) {
        ctx.attrSet(key, value);
    }

    @Override
    public Object get(String key) {
        return ctx.attr(key);
    }

    @Override
    public void delete(String key) {
        ctx.attrMap().remove(key);
    }
}
