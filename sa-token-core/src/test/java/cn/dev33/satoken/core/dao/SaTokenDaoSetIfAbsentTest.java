package cn.dev33.satoken.core.dao;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;

/**
 * SaTokenDao setIfAbsent / setObjectIfAbsent 原子写入测试
 *
 * @author konlue
 * @since 1.46.0
 */
public class SaTokenDaoSetIfAbsentTest {

	SaTokenDao dao = new SaTokenDaoDefaultImpl();

	/** key 不存在时应该写入成功，key 已存在时应该写入失败且不覆盖旧值 */
    @Test
    public void setIfAbsent_shouldSucceedOnlyOnce() {
    	Assertions.assertTrue(dao.setIfAbsent("lock", "a", 60));
    	Assertions.assertEquals("a", dao.get("lock"));

    	Assertions.assertFalse(dao.setIfAbsent("lock", "b", 60));
    	Assertions.assertEquals("a", dao.get("lock"));
    }

	/** timeout 语义应该与 set 保持一致：非法值不写入，-1 永久，正常值限时 */
    @Test
    public void setIfAbsent_shouldFollowTimeoutSemantics() {
    	// 非法 timeout 不写入，返回 false
    	Assertions.assertFalse(dao.setIfAbsent("k0", "v", 0));
    	Assertions.assertEquals(null, dao.get("k0"));
    	Assertions.assertFalse(dao.setIfAbsent("k-2", "v", SaTokenDao.NOT_VALUE_EXPIRE));
    	Assertions.assertEquals(null, dao.get("k-2"));

    	// -1 永久存储
    	Assertions.assertTrue(dao.setIfAbsent("kForever", "v", SaTokenDao.NEVER_EXPIRE));
    	Assertions.assertEquals(SaTokenDao.NEVER_EXPIRE, dao.getTimeout("kForever"));

    	// 正常值限时存储
    	Assertions.assertTrue(dao.setIfAbsent("kTimed", "v", 200));
    	long timeout = dao.getTimeout("kTimed");
    	Assertions.assertTrue(timeout > 195 && timeout <= 200);
    }

	/** Object 版本应该与 String 版本语义一致 */
    @Test
    public void setObjectIfAbsent_shouldSucceedOnlyOnce() {
    	Assertions.assertTrue(dao.setObjectIfAbsent("obj", "a", 60));
    	Assertions.assertEquals("a", dao.getObject("obj"));

    	Assertions.assertFalse(dao.setObjectIfAbsent("obj", "b", 60));
    	Assertions.assertEquals("a", dao.getObject("obj"));

    	// 非法 timeout 不写入
    	Assertions.assertFalse(dao.setObjectIfAbsent("objInvalid", "a", 0));
    	Assertions.assertEquals(null, dao.getObject("objInvalid"));
    }

	/** 并发场景下同一 key 只应该有一个线程写入成功 */
    @Test
    public void setIfAbsent_shouldBeAtomicUnderConcurrency() throws Exception {
    	for(int round = 0; round < 5; round++) {
    		String key = "atomic-" + round;
    		int threadCount = 16;
    		CountDownLatch beginLatch = new CountDownLatch(threadCount);
    		CountDownLatch doneLatch = new CountDownLatch(threadCount);
    		AtomicInteger successCount = new AtomicInteger(0);

    		for(int i = 0; i < threadCount; i++) {
    			new Thread(() -> {
    				try {
    					beginLatch.countDown();
    					beginLatch.await();
    					if(dao.setIfAbsent(key, "v", 60)) {
    						successCount.incrementAndGet();
    					}
    				} catch(InterruptedException e) {
    					// 忽略
    				} finally {
    					doneLatch.countDown();
    				}
    			}).start();
    		}
    		doneLatch.await();

    		Assertions.assertEquals(1, successCount.get(), "第 " + round + " 轮并发写入出现多个成功");
    	}
    }

}
