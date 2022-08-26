package cn.dev33.satoken.context.grpc;

import cn.dev33.satoken.context.second.SaTokenSecondContext;
import cn.dev33.satoken.context.second.SaTokenSecondContextCreator;
import org.springframework.stereotype.Component;

/**
 * Sa-Token 二级Context - 创建器 [Grpc版]
 *
 * @author lym
 */
@Component
public class SaTokenSecondContextCreatorForGrpc implements SaTokenSecondContextCreator {

    @Override
    public SaTokenSecondContext create() {
        return new SaTokenSecondContextForGrpc();
    }

}
