package cn.dev33.satoken.jfinal.test;

import cn.dev33.satoken.stp.StpInterface;

import java.util.ArrayList;
import java.util.List;

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
