package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.strategy.hooks.SaFirewallCheckHook;
import org.springframework.stereotype.Component;

@Component
public class CustomSaFirewallCheckHook implements SaFirewallCheckHook {
    @Override public void execute(SaRequest req, SaResponse res, Object extArg) { }
}