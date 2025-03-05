package com.pj.test;

import org.springframework.stereotype.Service;

/**
 * 模拟数据库操作类
 *
 * @author click33
 * @since 2025/3/5
 */
@Service
public class SysUserMockDao {

    // 返回指定 userId 绑定的手机号
    public String getPhoneByUserId(long userId) {
        return "13112341234";
    }

    // 返回指定用户名对应的 userId
    public long getUserIdByName(String name) {
        return 10001;
    }

}