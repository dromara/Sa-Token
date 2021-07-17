package cn.dev33.satoken.solon.model;

import cn.dev33.satoken.context.model.SaRequest;
import org.noear.solon.core.handle.Context;

/**
 * @author noear
 * @since 1.4
 */
public class SaRequestForSolon implements SaRequest {
    
	Context ctx;
    
	public SaRequestForSolon(){
        ctx = Context.current();
    }

    @Override
    public Object getSource() {
        return ctx;
    }

    @Override
    public String getParam(String s) {
        return ctx.param(s);
    }

    @Override
    public String getHeader(String s) {
        return ctx.header(s);
    }

    @Override
    public String getCookieValue(String s) {
        return ctx.cookie(s);
    }

    @Override
    public String getRequestPath() {
        return ctx.pathNew();
    }

    @Override
	public String getUrl() {
		return ctx.url();
	}
    
    @Override
    public String getMethod() {
        return ctx.method();
    }
}
