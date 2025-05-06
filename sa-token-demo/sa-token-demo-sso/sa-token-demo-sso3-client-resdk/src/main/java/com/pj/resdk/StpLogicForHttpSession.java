package com.pj.resdk;

import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.parameter.SaLogoutParameter;

import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 会话对象 - httpSession 版
 *
 * @author click33
 * @since 2025/5/6
 */
public class StpLogicForHttpSession extends StpLogic {

    /**
     * 初始化 StpLogic, 并指定账号类型
     *
     * @param type /
     *
     */
    public StpLogicForHttpSession(String type) {
        super(type);
    }

    // 判断当前会话是否已登录
    @Override
    public boolean isLogin() {

        return SpringMVCUtil.getRequest().getSession().getAttribute("userId") != null;
    }

    // 获取当前会话的登录ID
    @Override
    public Object getLoginId() {
        Object userId = SpringMVCUtil.getRequest().getSession().getAttribute("userId");
        if(userId == null) {
            throw new RuntimeException("当前会话未登录");
        }
        return userId;
    }

    // 获取当前登录设备 id
    @Override
    public String getLoginDeviceId() {
        return null;
    }

    // 当前会话注销
    @Override
    public void logout(SaLogoutParameter logoutParameter) {
        SpringMVCUtil.getRequest().getSession().removeAttribute("userId");
    }

    // 当前账号id注销
    @Override
    public void _logout(Object loginId, SaLogoutParameter logoutParameter) {
        System.out.println("--- 注销账号id：" + loginId);
        for (HttpSession session: MyHttpSessionHolder.sessionList) {
            Object userId = session.getAttribute("userId");
            if(Objects.equals(String.valueOf(userId), String.valueOf(loginId))) {
                session.removeAttribute("userId");
            }
        }
    }

}