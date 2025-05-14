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
package cn.dev33.satoken.annotation.handler;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.util.SaFoxUtil;

import java.lang.reflect.AnnotatedElement;

/**
 * 注解 SaCheckPermission 的处理器
 *
 * @author click33
 * @since 2024/8/2
 */
public class SaCheckPermissionHandler implements SaAnnotationHandlerInterface<SaCheckPermission> {

    @Override
    public Class<SaCheckPermission> getHandlerAnnotationClass() {
        return SaCheckPermission.class;
    }

    @Override
    public void checkMethod(SaCheckPermission at, AnnotatedElement element) {
        _checkMethod(at.type(), at.value(), at.mode(), at.orRole());
    }

    public static void _checkMethod(String type, String[] value, SaMode mode, String[] orRole) {
        StpLogic stpLogic = SaManager.getStpLogic(type, false);

        String[] permissionArray = value;
        try {
            if(mode == SaMode.AND) {
                stpLogic.checkPermissionAnd(permissionArray);
            } else {
                stpLogic.checkPermissionOr(permissionArray);
            }
        } catch (NotPermissionException e) {
            // 权限认证校验未通过，再开始角色认证校验
            for (String role : orRole) {
                String[] rArr = SaFoxUtil.convertStringToArray(role);
                // 某一项 role 认证通过，则可以提前退出了，代表通过
                if (stpLogic.hasRoleAnd(rArr)) {
                    return;
                }
            }
            throw e;
        }
    }

}