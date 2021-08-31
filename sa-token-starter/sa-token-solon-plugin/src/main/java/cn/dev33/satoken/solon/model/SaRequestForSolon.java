package cn.dev33.satoken.solon.model;

import org.noear.solon.core.handle.Context;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * @author noear
 * @since 1.4
 */
public class SaRequestForSolon implements SaRequest {
    
	protected Context ctx;
    
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
		String currDomain = SaManager.getConfig().getCurrDomain();
		if(SaFoxUtil.isEmpty(currDomain) == false) {
			return currDomain + this.getRequestPath();
		}
		return ctx.url();
	}
    
    @Override
    public String getMethod() {
        return ctx.method();
    }

    @Override
	public Object forward(String path) {
    	ctx.forward(path);
    	return null;
	}
	
}
