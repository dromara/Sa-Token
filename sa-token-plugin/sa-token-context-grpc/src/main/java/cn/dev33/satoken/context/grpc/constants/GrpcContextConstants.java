package cn.dev33.satoken.context.grpc.constants;

import cn.dev33.satoken.id.SaIdUtil;
import cn.dev33.satoken.util.SaTokenConsts;
import io.grpc.Metadata;

/**
 * 常量 
 * 
 * @author lym
 * @since: 2022/8/26 14:27
 */
public class GrpcContextConstants {
    public static final Metadata.Key<String> SA_ID_TOKEN =
            Metadata.Key.of(SaIdUtil.ID_TOKEN, Metadata.ASCII_STRING_MARSHALLER);

    public static final Metadata.Key<String> SA_JUST_CREATED_NOT_PREFIX =
            Metadata.Key.of(SaTokenConsts.JUST_CREATED_NOT_PREFIX, Metadata.ASCII_STRING_MARSHALLER);

}
