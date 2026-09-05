package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class CustomStpInterface implements StpInterface {
    @Override public List<String> getPermissionList(Object loginId, String loginType) { return Collections.emptyList(); }
    @Override public List<String> getRoleList(Object loginId, String loginType) { return Collections.emptyList(); }
}