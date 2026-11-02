package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.data.model.ClientTokenModel;
import cn.dev33.satoken.oauth2.scope.handler.SaOAuth2ScopeHandlerInterface;
import org.springframework.stereotype.Component;

@Component
public class CustomSaOAuth2ScopeHandler implements SaOAuth2ScopeHandlerInterface {
    public static final String SCOPE = "integration-test-scope";
    @Override public String getHandlerScope() { return SCOPE; }
    @Override public void workAccessToken(AccessTokenModel at) { }
    @Override public void workClientToken(ClientTokenModel ct) { }
}