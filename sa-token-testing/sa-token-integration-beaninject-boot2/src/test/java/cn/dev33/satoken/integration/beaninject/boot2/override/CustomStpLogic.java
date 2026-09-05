package cn.dev33.satoken.integration.beaninject.boot2.override;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

@Component
public class CustomStpLogic extends StpLogic {
    public CustomStpLogic() { super(StpUtil.TYPE); }
}