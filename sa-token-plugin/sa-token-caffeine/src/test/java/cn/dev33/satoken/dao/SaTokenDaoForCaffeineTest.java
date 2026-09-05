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
package cn.dev33.satoken.dao;

import cn.dev33.satoken.test.SaTokenTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * {@link SaTokenDaoForCaffeine} 持久层测试
 *
 * @author click33
 * @since 1.46.0
 */
@SaTokenTest
public class SaTokenDaoForCaffeineTest {

    private SaTokenDaoForCaffeine dao;

    @BeforeEach
    public void beforeEach() {
        dao = new SaTokenDaoForCaffeine();
    }

    /** setObject + getObject 应该能正常读写，类型化读取应该返回同一个值 */
    @Test
    public void setObject_getObject_roundtrip() {
        dao.setObject("k1", "v1", 200);
        Assertions.assertEquals("v1", dao.getObject("k1"));
        Assertions.assertEquals("v1", dao.getObject("k1", String.class));
    }

    /** timeout=-1 时应该代表永不过期，getTimeout 应该返回 NEVER_EXPIRE */
    @Test
    public void setObject_neverExpire() {
        dao.setObject("k1", "v1", SaTokenDao.NEVER_EXPIRE);
        Assertions.assertEquals("v1", dao.getObject("k1"));
        Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, dao.getObjectTimeout("k1"));
    }

    /** timeout=0 或 timeout=-2 时应该不存储该值 */
    @Test
    public void setObject_invalidTimeout_ignored() {
        dao.setObject("k1", "v1", 0);
        Assertions.assertNull(dao.getObject("k1"));
        dao.setObject("k2", "v2", SaTokenDao.NOT_VALUE_EXPIRE);
        Assertions.assertNull(dao.getObject("k2"));
    }

    /** getTimeout 应该返回剩余秒数 */
    @Test
    public void getObjectTimeout_valid() {
        dao.setObject("k1", "v1", 200);
        long timeout = dao.getObjectTimeout("k1");
        Assertions.assertTrue(timeout > 195 && timeout <= 200);
    }

    /** 不存在的 key 调用 getTimeout 应该返回 NOT_VALUE_EXPIRE */
    @Test
    public void getObjectTimeout_missingKey() {
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, dao.getObjectTimeout("missing"));
    }

    /** 过期后的 key 应该取不到值，getTimeout 应该返回 NOT_VALUE_EXPIRE */
    @Test
    public void expiredKey() throws InterruptedException {
        dao.setObject("k1", "v1", 1);
        Thread.sleep(1100);
        Assertions.assertNull(dao.getObject("k1"));
        Assertions.assertEquals(SaTokenDao.NOT_VALUE_EXPIRE, dao.getObjectTimeout("k1"));
    }

    /** updateObject 应该更新值且保持有效期；对不存在的 key 应该不做任何操作 */
    @Test
    public void updateObject() {
        dao.setObject("k1", "v1", 200);
        dao.updateObject("k1", "v2");
        Assertions.assertEquals("v2", dao.getObject("k1"));
        Assertions.assertTrue(dao.getObjectTimeout("k1") > 195);

        dao.updateObject("missing", "v3");
        Assertions.assertNull(dao.getObject("missing"));
    }

    /** updateObjectTimeout 应该修改剩余有效期 */
    @Test
    public void updateObjectTimeout() {
        dao.setObject("k1", "v1", 200);
        dao.updateObjectTimeout("k1", 500);
        long timeout = dao.getObjectTimeout("k1");
        Assertions.assertTrue(timeout > 495 && timeout <= 500);
    }

    /** deleteObject 之后应该取不到值 */
    @Test
    public void deleteObject() {
        dao.setObject("k1", "v1", 200);
        dao.deleteObject("k1");
        Assertions.assertNull(dao.getObject("k1"));
    }

    /** searchData 应该能按前缀搜索到相关 key */
    @Test
    public void searchData() {
        dao.setObject("satoken:login:token:aaa", "v1", 200);
        dao.setObject("satoken:login:token:bbb", "v2", 200);
        List<String> list = dao.searchData("satoken:login:token:", "", 0, 10, false);
        Assertions.assertEquals(2, list.size());
    }

    /** init/destroy 应该能正常启停定时刷新线程 */
    @Test
    public void init_and_destroy() {
        Assertions.assertDoesNotThrow(dao::init);
        Assertions.assertDoesNotThrow(dao::destroy);
    }

}
