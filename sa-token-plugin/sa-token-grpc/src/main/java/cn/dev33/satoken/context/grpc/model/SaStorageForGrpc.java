package cn.dev33.satoken.context.grpc.model;

import cn.dev33.satoken.context.grpc.context.SaTokenGrpcContext;
import cn.dev33.satoken.context.model.SaStorage;

/**
 * Storage for grpc
 *
 * @author lym
 * @since <= 1.34.0
 */
public class SaStorageForGrpc implements SaStorage {

    /**
     * 获取底层源对象
     */
    @Override
    public Object getSource() {
        return SaTokenGrpcContext.getContext();
    }

    /**
     * 在 [Request作用域] 里写入一个值
     */
    @Override
    public SaStorage set(String key, Object value) {
        SaTokenGrpcContext.set(key, value);
        return this;
    }

    /**
     * 在 [Request作用域] 里获取一个值
     */
    @Override
    public Object get(String key) {
        return SaTokenGrpcContext.get(key);
    }

    /**
     * 在 [Request作用域] 里删除一个值
     */
    @Override
    public SaStorage delete(String key) {
        SaTokenGrpcContext.removeKey(key);
        return this;
    }

}
