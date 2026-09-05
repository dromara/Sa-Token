/*
 * Copyright 2020-2099 sa-token.com
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
package cn.dev33.satoken.integration.boot2.support;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.util.SaFoxUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 集成测试用权限数据源：账号 10001 拥有固定角色与权限，其它账号返回 null。
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    /** 返回账号拥有的权限码集合 */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        int id = SaFoxUtil.getValueByType(loginId, int.class);
        if (id == 10001) {
            return Arrays.asList("user*", "art-add", "art-delete", "art-update", "art-get");
        }
        return null;
    }

    /** 返回账号拥有的角色标识集合 */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        int id = SaFoxUtil.getValueByType(loginId, int.class);
        if (id == 10001) {
            return Arrays.asList("admin", "super-admin");
        }
        return null;
    }

}
