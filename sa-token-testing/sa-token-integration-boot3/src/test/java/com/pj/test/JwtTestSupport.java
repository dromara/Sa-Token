package com.pj.test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.temp.jwt.SaTempTemplateForJwt;
import com.pj.test.satoken.StpInterfaceImpl;

/**
 * JWT 集成测试公共初始化
 */
public final class JwtTestSupport {

    static final String JWT_SECRET_KEY = "asdasdasifhueuiwyurfewbfjsdafjk";

    private JwtTestSupport() {
    }

    public static void ensureJwtSecretKey() {
        if (SaManager.getConfig().getJwtSecretKey() == null) {
            SaManager.getConfig().setJwtSecretKey(JWT_SECRET_KEY);
        }
    }

    public static void initJwt(StpLogic stpLogic) {
        ensureJwtSecretKey();
        SaManager.getConfig().setActiveTimeout(-1);
        SaManager.setStpInterface(new StpInterfaceImpl());
        StpUtil.setStpLogic(stpLogic);
    }

    /** 恢复 JWT 版临时 Token 模板（Spring 注入可能覆盖插件默认值） */
    public static void ensureJwtTempTemplate() {
        ensureJwtSecretKey();
        SaManager.setSaTempTemplate(new SaTempTemplateForJwt());
    }

    /** 恢复默认 StpLogic，避免污染其它集成测试 */
    public static void resetStpLogic() {
        StpUtil.setStpLogic(new StpLogic(StpUtil.TYPE));
    }

}
