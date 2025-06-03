package cn.dev33.satoken.loveqq.boot.model;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.application.ApplicationInfo;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.util.SaFoxUtil;
import com.kfyty.loveqq.framework.web.core.http.ServerRequest;
import com.kfyty.loveqq.framework.web.core.http.ServerResponse;

import java.net.HttpCookie;
import java.util.Collection;
import java.util.Map;

/**
 * 对 SaRequest 包装类的实现
 *
 * @author kfyty725
 */
public class LoveqqSaRequest implements SaRequest {
    /**
     * loveqq-framework 包装请求
     */
    private final ServerRequest request;

    public LoveqqSaRequest(ServerRequest request) {
        this.request = request;
    }

    @Override
    public Object getSource() {
        return request;
    }

    @Override
    public String getParam(String name) {
        return request.getParameter(name);
    }

    @Override
    public Collection<String> getParamNames() {
        return request.getParameterNames();
    }

    @Override
    public Map<String, String> getParamMap() {
        return request.getParameterMap();
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public String getCookieValue(String name) {
        HttpCookie cookie = request.getCookie(name);
        return cookie == null ? null : cookie.getValue();
    }

    @Override
    public String getCookieFirstValue(String name) {
        HttpCookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (HttpCookie cookie : cookies) {
                if (cookie != null && name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String getCookieLastValue(String name) {
        String value = null;
        HttpCookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (HttpCookie cookie : cookies) {
                if (cookie != null && name.equals(cookie.getName())) {
                    value = cookie.getValue();
                }
            }
        }
        return value;
    }

    @Override
    public String getRequestPath() {
        return ApplicationInfo.cutPathPrefix(request.getRequestURI());
    }

    @Override
    public String getUrl() {
        String currDomain = SaManager.getConfig().getCurrDomain();
        if (!SaFoxUtil.isEmpty(currDomain)) {
            return currDomain + this.getRequestPath();
        }
        return request.getRequestURL();
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getHost() {
        return request.getHost();
    }

    @Override
    public Object forward(String path) {
        ServerResponse response = (ServerResponse) SaManager.getSaTokenContext().getResponse().getSource();
        return response.sendRedirect(path);
    }
}
