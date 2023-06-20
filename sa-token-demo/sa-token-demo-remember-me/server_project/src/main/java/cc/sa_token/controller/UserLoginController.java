package cc.sa_token.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/back/user")
public class UserLoginController {

    @RequestMapping("/login")
    public SaResult doLogin(String name, String pwd, Boolean remember) {
        if("zhang".equals(name) && "123456".equals(pwd)) {
            StpUtil.login(10001, remember);
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return SaResult.ok()
                    .set("tokenName", tokenInfo.getTokenName())
                    .set("tokenValue", tokenInfo.getTokenValue());
        } else {
            return SaResult.error("登录失败");
        }
    }

    @RequestMapping("/state")
    public SaResult checkNowLoginState() {
        return SaResult.ok().setData(StpUtil.isLogin());
    }

    @RequestMapping("/logout")
    public SaResult doLogout() {
        StpUtil.logout();
        return SaResult.ok().setData(StpUtil.isLogin());
    }

}
