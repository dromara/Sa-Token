package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.data.model.AccessTokenModel;
import cn.dev33.satoken.oauth2.granttype.handler.SaOAuth2GrantTypeHandlerInterface;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class CustomSaOAuth2GrantTypeHandler implements SaOAuth2GrantTypeHandlerInterface {
    public static final String GRANT_TYPE = "integration_test_grant";
    @Override public String getHandlerGrantType() { return GRANT_TYPE; }
    @Override public AccessTokenModel getAccessToken(SaRequest req, String clientId, List<String> scopes) { return null; }
}