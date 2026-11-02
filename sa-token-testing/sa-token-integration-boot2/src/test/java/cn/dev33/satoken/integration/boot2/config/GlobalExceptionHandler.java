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
package cn.dev33.satoken.integration.boot2.config;

import cn.dev33.satoken.exception.DisableServiceException;
import cn.dev33.satoken.exception.NotHttpBasicAuthException;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.NotSafeException;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 集成测试全局异常处理：将 Sa-Token 异常映射为固定 HTTP 业务码，便于 MockMvc 断言。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 未登录 */
    @ExceptionHandler(NotLoginException.class)
    public SaResult handleNotLogin(NotLoginException e) {
        return SaResult.error().setCode(401);
    }

    /** 缺少角色 */
    @ExceptionHandler(NotRoleException.class)
    public SaResult handleNotRole(NotRoleException e) {
        return SaResult.error().setCode(402);
    }

    /** 缺少权限 */
    @ExceptionHandler(NotPermissionException.class)
    public SaResult handleNotPermission(NotPermissionException e) {
        return SaResult.error().setCode(403);
    }

    /** 二级认证失败 */
    @ExceptionHandler(NotSafeException.class)
    public SaResult handleNotSafe(NotSafeException e) {
        return SaResult.error().setCode(901);
    }

    /** Http Basic 认证失败 */
    @ExceptionHandler(NotHttpBasicAuthException.class)
    public SaResult handleNotBasicAuth(NotHttpBasicAuthException e) {
        return SaResult.error().setCode(903);
    }

    /** 服务被封禁 */
    @ExceptionHandler(DisableServiceException.class)
    public SaResult handleDisable(DisableServiceException e) {
        return SaResult.error().setCode(904);
    }

}
