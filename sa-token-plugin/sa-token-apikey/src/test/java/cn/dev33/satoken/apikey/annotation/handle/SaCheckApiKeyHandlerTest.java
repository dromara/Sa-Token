/*
 * Copyright 2020-2099 sa-token.com
 *
 * Licensed under the Apache License, Version  2.0 (the "License");
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
package cn.dev33.satoken.apikey.annotation.handle;

import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.apikey.SaApiKeyManager;
import cn.dev33.satoken.apikey.annotation.SaCheckApiKey;
import cn.dev33.satoken.apikey.config.SaApiKeyConfig;
import cn.dev33.satoken.apikey.exception.ApiKeyException;
import cn.dev33.satoken.apikey.loader.SaApiKeyDataLoader;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.apikey.template.SaApiKeyTemplate;
import cn.dev33.satoken.context.mock.SaRequestForMock;
import cn.dev33.satoken.context.mock.SaTokenContextMockUtil;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link SaCheckApiKeyHandler} 注解处理器测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaCheckApiKeyHandlerTest {

    private SaApiKeyConfig backupConfig;
    private SaApiKeyDataLoader backupLoader;
    private SaApiKeyTemplate backupTemplate;

    @BeforeEach
    public void backup() {
        backupConfig = SaApiKeyManager.getConfig();
        backupLoader = SaApiKeyManager.getSaApiKeyDataLoader();
        backupTemplate = SaApiKeyManager.getSaApiKeyTemplate();
        SaApiKeyManager.setConfig(new SaApiKeyConfig().setIsRecordIndex(false));
        SaApiKeyManager.setSaApiKeyTemplate(new SaApiKeyTemplate());
    }

    @AfterEach
    public void restore() {
        SaApiKeyManager.setConfig(backupConfig);
        SaApiKeyManager.setSaApiKeyDataLoader(backupLoader);
        SaApiKeyManager.setSaApiKeyTemplate(backupTemplate);
    }

    /** 用于拿到 @SaCheckApiKey 注解的承载方法（AND 模式） */
    @SaCheckApiKey(scope = {"read"}, mode = SaMode.AND)
    public void annotatedAndMethod() {
    }

    /** 用于拿到 @SaCheckApiKey 注解的承载方法（OR 模式） */
    @SaCheckApiKey(scope = {"read", "write"}, mode = SaMode.OR)
    public void annotatedOrMethod() {
    }

    private ApiKeyModel buildAk(String apiKey, Object loginId, String... scopes) {
        ApiKeyModel ak = new ApiKeyModel()
                .setApiKey(apiKey).setLoginId(loginId).setCreateTime(System.currentTimeMillis())
                .setExpiresTime(SaTokenDao.NEVER_EXPIRE).setIsValid(true);
        for (String s : scopes) {
            ak.addScope(s);
        }
        return ak;
    }

    /** getHandlerAnnotationClass 应该返回 SaCheckApiKey.class */
    @Test
    public void getHandlerAnnotationClass() {
        Assertions.assertEquals(SaCheckApiKey.class, new SaCheckApiKeyHandler().getHandlerAnnotationClass());
    }

    /** checkMethod AND 模式：具备 scope 时应该通过 */
    @Test
    public void checkMethod_andMode_passes() throws NoSuchMethodException {
        Method method = SaCheckApiKeyHandlerTest.class.getMethod("annotatedAndMethod");
        SaCheckApiKey at = method.getAnnotation(SaCheckApiKey.class);
        SaApiKeyManager.getSaApiKeyTemplate().saveApiKey(buildAk("AK-hand", 1L, "read"));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("apikey", "AK-hand");
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = params;
        try {
            Assertions.assertDoesNotThrow(() -> new SaCheckApiKeyHandler().checkMethod(at, method));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** checkMethod AND 模式：不具备 scope 时必须抛异常 */
    @Test
    public void checkMethod_andMode_throws() throws NoSuchMethodException {
        Method method = SaCheckApiKeyHandlerTest.class.getMethod("annotatedAndMethod");
        SaCheckApiKey at = method.getAnnotation(SaCheckApiKey.class);
        SaApiKeyManager.getSaApiKeyTemplate().saveApiKey(buildAk("AK-hand2", 1L, "write"));

        Map<String, String> params = new LinkedHashMap<>();
        params.put("apikey", "AK-hand2");
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = params;
        try {
            Assertions.assertThrows(ApiKeyException.class, () -> new SaCheckApiKeyHandler().checkMethod(at, method));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** _checkMethod AND 模式：具备全部 scope 时应该通过 */
    @Test
    public void checkMethod_andMode_valid() {
        SaApiKeyManager.getSaApiKeyTemplate().saveApiKey(buildAk("AK-and", 1L, "read", "write"));
        Map<String, String> params = new LinkedHashMap<>();
        params.put("apikey", "AK-and");
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = params;
        try {
            Assertions.assertDoesNotThrow(() -> SaCheckApiKeyHandler._checkMethod(new String[]{"read", "write"}, SaMode.AND));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** _checkMethod OR 模式：具备其一时应该通过 */
    @Test
    public void checkMethod_orMode_valid() {
        SaApiKeyManager.getSaApiKeyTemplate().saveApiKey(buildAk("AK-or", 1L, "read"));
        Map<String, String> params = new LinkedHashMap<>();
        params.put("apikey", "AK-or");
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = params;
        try {
            Assertions.assertDoesNotThrow(() -> SaCheckApiKeyHandler._checkMethod(new String[]{"read", "write"}, SaMode.OR));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }

    /** _checkMethod 空 scope 时 AND/OR 都应该直接通过 */
    @Test
    public void checkMethod_emptyScope_passes() {
        SaApiKeyManager.getSaApiKeyTemplate().saveApiKey(buildAk("AK-empty", 1L));
        Map<String, String> params = new LinkedHashMap<>();
        params.put("apikey", "AK-empty");
        SaTokenContextMockUtil.setMockContext();
        ((SaRequestForMock) cn.dev33.satoken.context.SaHolder.getRequest()).parameterMap = params;
        try {
            Assertions.assertDoesNotThrow(() -> SaCheckApiKeyHandler._checkMethod(new String[]{}, SaMode.AND));
            Assertions.assertDoesNotThrow(() -> SaCheckApiKeyHandler._checkMethod(new String[]{}, SaMode.OR));
        } finally {
            SaTokenContextMockUtil.clearContext();
        }
    }
}
