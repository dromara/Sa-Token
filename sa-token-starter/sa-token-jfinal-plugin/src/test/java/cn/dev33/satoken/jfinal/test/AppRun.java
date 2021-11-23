package cn.dev33.satoken.jfinal.test;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.jfinal.core.Controller;
import com.jfinal.core.Path;
import com.jfinal.server.undertow.UndertowServer;

@Path("/")
public class AppRun extends Controller {
    public static void main(String[] args) {
        UndertowServer.create(Config.class)
                .addHotSwapClassPrefix("cn.dev33.satoken.jfinal.")
                .start();
    }

    public void index(){
        renderText("index");
    }

    public void doLogin(){
        StpUtil.logout();
        StpUtil.login(10002);
        //赋值角色
        renderText("登录成功");
    }

    public void getLoginInfo(){
        System.out.println("是否登录："+StpUtil.isLogin());
        System.out.println("登录信息"+StpUtil.getTokenInfo());
        renderJson(StpUtil.getTokenInfo());
    }

    @SaCheckRole("super-admin")
    public void add(){
        renderText("超级管理员方法！");
    }
}
