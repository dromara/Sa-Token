package cn.dev33.satoken.loveqq.boot.model;

import cn.dev33.satoken.context.model.SaResponse;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

/**
 * 对 SaResponse 包装类的实现
 *
 * @author kfyty725
 */
public class LoveqqSaResponse implements SaResponse {
    /**
     * loveqq-framework 包装响应
     */
    private final ServerResponse response;

    public LoveqqSaResponse(ServerResponse response) {
        this.response = response;
    }

    @Override
    public Object getSource() {
        return response;
    }

    @Override
    public SaResponse setStatus(int sc) {
        response.setStatus(sc);
        return this;
    }

    @Override
    public SaResponse setHeader(String name, String value) {
        response.setHeader(name, value);
        return this;
    }

    @Override
    public SaResponse addHeader(String name, String value) {
        response.addHeader(name, value);
        return this;
    }

    @Override
    public Object redirect(String url) {
        return response.sendRedirect(url);
    }
}
