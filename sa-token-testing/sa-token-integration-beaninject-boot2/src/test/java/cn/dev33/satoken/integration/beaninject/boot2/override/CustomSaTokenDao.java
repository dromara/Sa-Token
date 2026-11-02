package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import org.springframework.stereotype.Component;

@Component
public class CustomSaTokenDao extends SaTokenDaoDefaultImpl {
}