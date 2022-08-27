package cn.dev33.satoken.context.grpc.context;

import io.grpc.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lym
 * @date 2022/8/25 11:02
 **/
public class SaTokenGrpcContext {
    /**
     * grpc请求上下文。请求完成后会由grpc自动清空
     *
     * @see Contexts#interceptCall(Context, ServerCall, Metadata, ServerCallHandler)
     */
    private static final Context.Key<Map<String, Object>> SA_TOKEN_CONTEXT_KEY =
            Context.key("sa-token-context");

    public static Object get(String key) {
        return SA_TOKEN_CONTEXT_KEY.get().get(key);
    }

    public static void set(String key, Object value) {
        SA_TOKEN_CONTEXT_KEY.get().put(key, value);
    }

    public static void removeKey(String key) {
        SA_TOKEN_CONTEXT_KEY.get().remove(key);
    }

    public static Map<String, Object> getContext() {
        return SA_TOKEN_CONTEXT_KEY.get();
    }

    public static boolean isNotNull() {
        return SA_TOKEN_CONTEXT_KEY.get() != null;
    }

    public static Context create() {
        return Context.current().withValue(SaTokenGrpcContext.SA_TOKEN_CONTEXT_KEY, new HashMap<>());
    }
}
