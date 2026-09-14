package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.oauth2.dao.SaOAuth2Dao;
import org.springframework.stereotype.Component;

@Component
public class CustomSaOAuth2Dao extends SaOAuth2Dao {
}