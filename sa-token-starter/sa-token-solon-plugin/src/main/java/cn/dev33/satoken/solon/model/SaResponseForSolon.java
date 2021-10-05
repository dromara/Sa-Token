package cn.dev33.satoken.solon.model;

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
	public SaResponse setStatus(int sc) {
		ctx.status(sc);
		return this;
	}
	
    @Override
    public SaResponse setHeader(String name, String value) {
        ctx.headerSet(name, value);
        return this;
    }

	/**
	 * 在响应头里添加一个值 
	 * @param name 名字
	 * @param value 值 
	 * @return 对象自身 
	 */
	public SaResponse addHeader(String name, String value) {
		ctx.headerAdd(name, value);
		return this;
	}
    
	@Override
	public Object redirect(String url) {
		ctx.redirect(url);
		return null;
	}
}
