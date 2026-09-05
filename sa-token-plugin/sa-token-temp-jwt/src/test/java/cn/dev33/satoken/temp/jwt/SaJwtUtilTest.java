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
package cn.dev33.satoken.temp.jwt;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.temp.jwt.error.SaTempJwtErrorCode;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

/**
 * {@link SaJwtUtil} 静态工具方法测试
 *
 * @author click33
 * @since 1.46.0
 */
public class SaJwtUtilTest {

    /** 测试秘钥 */
    public static final String JWT_SECRET_KEY = "SaJwtUtilTest-Secret-Key-0123456789";

    /** 按模块同款规则手工造一个 jwt，eff 传 null 代表不携带 eff 字段 */
    private static String buildJwt(Object value, Long eff, String keyt) {
        SecretKey key = Keys.hmacShaKeyFor(SaSecureUtil.md5(keyt).getBytes());
        JwtBuilder builder = Jwts.builder()
                .header().add("typ", "JWT").and()
                .claim(SaJwtUtil.KEY_VALUE, value);
        if(eff != null) {
            builder.claim(SaJwtUtil.KEY_EFF, eff);
        }
        return builder.signWith(key).compact();
    }

    /** createToken 生成的应该是一个标准三段式 jwt，载荷中携带 value 和 eff */
    @Test
    public void createToken_structure() {
        long before = System.currentTimeMillis();
        String token = SaJwtUtil.createToken("zhang-1", 200, JWT_SECRET_KEY);
        Assertions.assertEquals(2, token.chars().filter(ch -> ch == '.').count());

        Claims claims = SaJwtUtil.parseToken(token, JWT_SECRET_KEY);
        Assertions.assertEquals("zhang-1", claims.get(SaJwtUtil.KEY_VALUE));
        Long eff = claims.get(SaJwtUtil.KEY_EFF, Long.class);
        // eff 应该是 13 位毫秒时间戳，且等于创建时刻 + 200 秒
        Assertions.assertTrue(eff > before + 199 * 1000 && eff <= System.currentTimeMillis() + 200 * 1000);
    }

    /** timeout=-1 时 createToken 的 eff 应该被记为 -1（永不过期） */
    @Test
    public void createToken_neverExpire_effIsMinusOne() {
        String token = SaJwtUtil.createToken("zhang-1", SaJwtUtil.NEVER_EXPIRE, JWT_SECRET_KEY);
        Claims claims = SaJwtUtil.parseToken(token, JWT_SECRET_KEY);
        Assertions.assertEquals(SaJwtUtil.NEVER_EXPIRE, claims.get(SaJwtUtil.KEY_EFF, Long.class));
    }

    /** 用错误秘钥解析 token 时必须抛出 JwtException */
    @Test
    public void parseToken_wrongKey_throwJwtException() {
        String token = SaJwtUtil.createToken("zhang-1", 200, JWT_SECRET_KEY);
        Assertions.assertThrows(JwtException.class, () -> SaJwtUtil.parseToken(token, "wrong-key"));
    }

    /** 解析已超时的 token 时必须抛出 SaTokenException，且错误码为 30303 */
    @Test
    public void getValue_expiredToken_throw() {
        String token = buildJwt("zhang-1", System.currentTimeMillis() - 60 * 1000, JWT_SECRET_KEY);
        SaTokenException e = Assertions.assertThrows(SaTokenException.class,
                () -> SaJwtUtil.getValue(token, JWT_SECRET_KEY));
        Assertions.assertEquals(SaTempJwtErrorCode.CODE_30303, e.getCode());
    }

    /** token 不携带 eff 字段时 getValue 必须抛出 SaTokenException，且错误码为 30303 */
    @Test
    public void getValue_missingEffClaim_throw() {
        String token = buildJwt("zhang-1", null, JWT_SECRET_KEY);
        SaTokenException e = Assertions.assertThrows(SaTokenException.class,
                () -> SaJwtUtil.getValue(token, JWT_SECRET_KEY));
        Assertions.assertEquals(SaTempJwtErrorCode.CODE_30303, e.getCode());
    }

    /** eff=-1 的 token 即使时间戳已过期也应该能正常解析出 value */
    @Test
    public void getValue_neverExpireToken_pass() {
        String token = buildJwt("zhang-1", SaJwtUtil.NEVER_EXPIRE, JWT_SECRET_KEY);
        Assertions.assertEquals("zhang-1", SaJwtUtil.getValue(token, JWT_SECRET_KEY));
    }

    /** token 不携带 value 字段时 getValue 应该返回 null */
    @Test
    public void getValue_missingValueClaim_returnNull() {
        String token = buildJwt(null, System.currentTimeMillis() + 60 * 1000, JWT_SECRET_KEY);
        Assertions.assertNull(SaJwtUtil.getValue(token, JWT_SECRET_KEY));
    }

    /** eff=-1 时 getTimeout 应该返回 NEVER_EXPIRE */
    @Test
    public void getTimeout_neverExpireToken() {
        String token = buildJwt("zhang-1", SaJwtUtil.NEVER_EXPIRE, JWT_SECRET_KEY);
        Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, SaJwtUtil.getTimeout(token, JWT_SECRET_KEY));
    }

    /** eff 已过期时 getTimeout 应该返回 NOT_VALUE_EXPIRE */
    @Test
    public void getTimeout_expiredToken() {
        String token = buildJwt("zhang-1", System.currentTimeMillis() - 60 * 1000, JWT_SECRET_KEY);
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, SaJwtUtil.getTimeout(token, JWT_SECRET_KEY));
    }

    /** 默认构造函数应该能 new 出来 */
    @Test
    public void constructor_instantiable() {
        Assertions.assertNotNull(new SaJwtUtil());
    }

    /** eff 未过期时 getTimeout 应该返回剩余秒数 */
    @Test
    public void getTimeout_validToken() {
        String token = buildJwt("zhang-1", System.currentTimeMillis() + 200 * 1000, JWT_SECRET_KEY);
        long timeout = SaJwtUtil.getTimeout(token, JWT_SECRET_KEY);
        Assertions.assertTrue(timeout >= 199 && timeout <= 200);
    }

}
