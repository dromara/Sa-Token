package cn.dev33.satoken.jboot.test;

import cn.dev33.satoken.stp.StpInterface;
import io.jboot.aop.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@Bean
public class StpInterfaceImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object o, String s) {
        return null;
    }

    @Override
    public List<String> getRoleList(Object o, String s) {
        List<String> list = new ArrayList<String>();
        list.add("admin");
        list.add("super-admin");
        return list;
    }
}
