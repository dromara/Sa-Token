package cn.dev33.satoken.jboot.test;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import io.jboot.Jboot;
import io.jboot.app.JbootApplication;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;

@RequestMapping("/")
public class AppRun extends JbootController {
    public static void main(String[] args) {
        JbootApplication.run(args);
    }

    public void index() {
        renderText("index");
    }

    public void doLogin() {
        StpUtil.login(10001);
        //赋值角色
        renderText("登录成功");
    }

    public void getLoginInfo() {
        System.out.println("是否登录：" + StpUtil.isLogin());
        System.out.println("登录信息" + StpUtil.getTokenInfo());
        renderJson(StpUtil.getTokenInfo());
    }

    @SaCheckRole("super-admin")
    public void add() {
        renderText("超级管理员方法！");
    }

    @SuppressWarnings("unused")
    public void token(String token) {
		Object t = Jboot.getRedis().get("xxxxx"); //默认redis库
        SaSession saSession = StpUtil.getSessionByLoginId(StpUtil.getLoginIdByToken(token), false); //satoken redis库
        renderJson(saSession);
    }
}
