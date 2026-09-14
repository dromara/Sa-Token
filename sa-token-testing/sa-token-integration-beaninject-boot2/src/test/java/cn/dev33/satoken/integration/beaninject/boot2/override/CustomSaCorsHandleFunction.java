package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import org.springframework.stereotype.Component;

@Component
public class CustomSaCorsHandleFunction implements SaCorsHandleFunction {
    @Override public void execute(SaRequest req, SaResponse res, SaStorage sto) { }
}