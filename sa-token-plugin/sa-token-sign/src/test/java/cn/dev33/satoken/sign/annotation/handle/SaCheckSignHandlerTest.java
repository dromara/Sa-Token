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
package cn.dev33.satoken.sign.annotation.handle;

import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.sign.SaSignManager;
import cn.dev33.satoken.sign.annotation.SaCheckSign;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.exception.SaSignException;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaCheckSignHandler} 注解处理器行为测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaCheckSignHandlerTest {

    private Map<String, SaSignConfig> backupSignMany;

    @BeforeEach
    public void backup() {
        backupSignMany = SaSignManager.getSignMany();
    }

    @AfterEach
    public void restore() {
        SaSignManager.setSignMany(backupSignMany);
    }

    /** 用于获取 @SaCheckSign 注解的承载方法 */
    @SaCheckSign(appid = "app1", verifyParams = {"data"})
    public void annotatedMethod() {
    }

    /** getHandlerAnnotationClass 返回 SaCheckSign.class */
    @Test
    public void getHandlerAnnotationClass() {
        Assertions.assertEquals(SaCheckSign.class, new SaCheckSignHandler().getHandlerAnnotationClass());
    }

    /** checkMethod 把注解的 appid 与 verifyParams 透传给 _checkMethod */
    @Test
    public void checkMethod_delegatesToCheckMethod() throws NoSuchMethodException {
        Method method = SaCheckSignHandlerTest.class.getMethod("annotatedMethod");
        SaCheckSign at = method.getAnnotation(SaCheckSign.class);

        Map<String, SaSignConfig> map = new LinkedHashMap<>();
        map.put("app1", new SaSignConfig().setSecretKey("k1"));
        SaSignManager.setSignMany(map);

        SaRequestForMock request = new SaRequestForMock();
        request.parameterMap = new LinkedHashMap<>();
        SaTokenContextMockUtil.setMockContext();
        try {
            // 缺少 timestamp，checkParamMap 会抛异常，证明走到了 checkRequest
            try {
                new SaCheckSignHandler().checkMethod(at, method);
                Assertions.fail("应抛出 SaSignException");
            } catch (SaSignException ex) {
                Assertions.assertTrue(ex.getMessage().contains("timestamp"));
            }
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** _checkMethod 固定 appid 时按该 appid 查找签名配置 */
    @Test
    public void checkMethod_fixedAppid() {
        Map<String, SaSignConfig> map = new LinkedHashMap<>();
        map.put("app1", new SaSignConfig().setSecretKey("k1"));
        SaSignManager.setSignMany(map);

        SaRequestForMock request = new SaRequestForMock();
        request.parameterMap = new LinkedHashMap<>();
        SaTokenContextMockUtil.setMockContext();
        try {
            try {
                SaCheckSignHandler._checkMethod("app1", new String[]{});
                Assertions.fail("应抛出 SaSignException");
            } catch (SaSignException ex) {
                Assertions.assertTrue(ex.getMessage().contains("timestamp"));
            }
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** _checkMethod 成功路径：合法请求参数时 checkRequest 正常返回，覆盖方法末尾调用 */
    @Test
    public void checkMethod_fixedAppid_validRequest_passes() {
        Map<String, SaSignConfig> map = new LinkedHashMap<>();
        SaSignConfig config = new SaSignConfig().setSecretKey("k1");
        map.put("app1", config);
        SaSignManager.setSignMany(map);

        // 用 app1 对应模板生成合法的 timestamp/nonce/sign
        cn.dev33.satoken.sign.template.SaSignTemplate template =
                new cn.dev33.satoken.sign.template.SaSignTemplate(config);
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, Object> signed = template.addSignParams(params);
        Map<String, String> paramMap = new LinkedHashMap<>();
        signed.forEach((k, v) -> paramMap.put(k, String.valueOf(v)));

        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = paramMap;
        try {
            Assertions.assertDoesNotThrow(() -> SaCheckSignHandler._checkMethod("app1", new String[]{}));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** checkMethod 成功路径：合法请求时注解校验通过 */
    @Test
    public void checkMethod_delegatesToCheckMethod_validRequest_passes() throws NoSuchMethodException {
        Method method = SaCheckSignHandlerTest.class.getMethod("annotatedMethod");
        SaCheckSign at = method.getAnnotation(SaCheckSign.class);

        Map<String, SaSignConfig> map = new LinkedHashMap<>();
        SaSignConfig config = new SaSignConfig().setSecretKey("app1-key");
        map.put("app1", config);
        SaSignManager.setSignMany(map);

        // app1 模板生成合法参数（annotatedMethod 的 verifyParams={"data"}，不指定时校验全部参数）
        cn.dev33.satoken.sign.template.SaSignTemplate template =
                new cn.dev33.satoken.sign.template.SaSignTemplate(config);
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, Object> signed = template.addSignParams(params);
        Map<String, String> paramMap = new LinkedHashMap<>();
        signed.forEach((k, v) -> paramMap.put(k, String.valueOf(v)));

        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = paramMap;
        try {
            Assertions.assertDoesNotThrow(() -> new SaCheckSignHandler().checkMethod(at, method));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** _checkMethod appid 为 #{paramName} 时从请求参数解析实际 appid */
    @Test
    public void checkMethod_templatedAppid_resolvedFromRequest() {
        Map<String, SaSignConfig> map = new LinkedHashMap<>();
        map.put("app-resolved", new SaSignConfig().setSecretKey("k1"));
        SaSignManager.setSignMany(map);

        SaRequestForMock request = new SaRequestForMock();
        request.parameterMap = new LinkedHashMap<>();
        request.parameterMap.put("appid", "app-resolved");
        SaTokenContextMockUtil.setMockContext();
        try {
            // 解析出 appid=app-resolved 后走到 checkRequest，缺 timestamp 抛异常
            try {
                SaCheckSignHandler._checkMethod("#{appid}", new String[]{});
                Assertions.fail("应抛出 SaSignException");
            } catch (SaSignException ex) {
                Assertions.assertTrue(ex.getMessage().contains("timestamp"));
            }
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** _checkMethod appid 以 #{ 开头但不以 } 结尾时不进入解析分支，按原 appid 查找（未命中抛 CODE_12211） */
    @Test
    public void checkMethod_templatedAppid_notClosedBrace_throws() {
        SaSignManager.setSignMany(new LinkedHashMap<>());

        SaRequestForMock request = new SaRequestForMock();
        request.parameterMap = new LinkedHashMap<>();
        SaTokenContextMockUtil.setMockContext();
        try {
            cn.dev33.satoken.exception.SaTokenException ex = Assertions.assertThrows(
                    cn.dev33.satoken.exception.SaTokenException.class,
                    () -> SaCheckSignHandler._checkMethod("#{appid", new String[]{}));
            Assertions.assertEquals(cn.dev33.satoken.sign.error.SaSignErrorCode.CODE_12211, ex.getCode());
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }
}
