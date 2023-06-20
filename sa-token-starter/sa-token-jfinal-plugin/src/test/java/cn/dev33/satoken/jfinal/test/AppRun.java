/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
