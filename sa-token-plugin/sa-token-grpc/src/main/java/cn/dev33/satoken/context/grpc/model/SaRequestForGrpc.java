package cn.dev33.satoken.context.grpc.model;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.model.SaRequest;

import java.util.List;
import java.util.Map;

/**
 * Request for grpc
 *
 * @author lym
 * @since <= 1.34.0
 */
public class SaRequestForGrpc implements SaRequest {

    /**
     * 获取底层源对象
     */
    @Override
    public Object getSource() {
        return SaTokenGrpcContext.getContext();
    }

    /**
     * 在 [请求体] 里获取一个值
     */
    @Override
    public String getParam(String name) {
        // 不传播 url 参数
        return null;
    }

    /**
     * 获取 [请求体] 里提交的所有参数名称
     * @return 参数名称列表
     */
    @Override
    public List<String> getParamNames(){
        return null;
    }

    /**
     * 获取 [请求体] 里提交的所有参数
     * @return 参数列表
     */
    @Override
    public Map<String, String> getParamMap(){
        return null;
    }

    /**
     * 在 [请求头] 里获取一个值
     */
    @Override
    public String getHeader(String name) {
        // 不传播 header 参数
        return null;
    }

    /**
     * 在 [Cookie作用域] 里获取一个值
     */
    @Override
    public String getCookieValue(String name) {
        // 不传播 cookie 参数
        return null;
    }

    /**
     * 返回当前请求path (不包括上下文名称)
     */
    @Override
    public String getRequestPath() {
        // 不传播 requestPath
        return null;
    }

    /**
     * 返回当前请求的url，例：http://xxx.com/test
     *
     * @return see note
     */
    public String getUrl() {
        // 不传播 url
        return null;
    }

    /**
     * 返回当前请求的类型
     */
    @Override
    public String getMethod() {
        // 不传播 method
        return null;
    }

    /**
     * 转发请求
     */
    @Override
    public Object forward(String path) {
        // 不传播 forward 动作
        return null;
    }

}
