package cn.dev33.satoken.solon.model;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;

import cn.dev33.satoken.context.model.SaResponse;

/**
 * @author noear
 * @since 1.4
 */
public class SaResponseForSolon implements SaResponse {
	
	protected Context ctx;

    public SaResponseForSolon() {
        ctx = Context.current();
    }

    @Override
    public Object getSource() {
        return ctx;
    }

    @Override
    public void deleteCookie(String s) {
        ctx.cookieRemove(s);
    }

    @Override
    public void addCookie(String name, String value, String path, String domain, int timeout) {
        if (Utils.isNotEmpty(path)) {
            path = "/";
        }

        ctx.cookieSet(name, value, domain, path, timeout);
    }

	@Override
	public SaResponse setStatus(int sc) {
		ctx.statusSet(sc);
		return this;
	}
	
    @Override
    public SaResponse setHeader(String name, String value) {
        ctx.headerSet(name, value);
        return this;
    }
    
	@Override
	public Object redirect(String url) {
		ctx.redirect(url);
		return null;
	}
}
