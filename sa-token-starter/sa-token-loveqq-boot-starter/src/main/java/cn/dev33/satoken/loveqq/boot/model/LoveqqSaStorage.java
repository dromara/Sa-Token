package cn.dev33.satoken.loveqq.boot.model;

import cn.dev33.satoken.context.model.SaStorage;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;

/**
 * 对 SaStorage 包装类的实现
 *
 * @author kfyty725
 */
public class LoveqqSaStorage implements SaStorage {
    /**
     * loveqq-framework 包装请求
     */
    private final ServerRequest request;

    public LoveqqSaStorage(ServerRequest request) {
        this.request = request;
    }

    @Override
    public Object getSource() {
        return request;
    }

    @Override
    public Object get(String key) {
        return request.getAttribute(key);
    }

    @Override
    public SaStorage set(String key, Object value) {
        request.setAttribute(key, value);
        return this;
    }

    @Override
    public SaStorage delete(String key) {
        request.removeAttribute(key);
        return this;
    }
}
