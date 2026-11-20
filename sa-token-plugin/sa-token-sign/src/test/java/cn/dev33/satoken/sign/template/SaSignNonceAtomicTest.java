package cn.dev33.satoken.sign.template;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDaoDefaultImpl;
import cn.dev33.satoken.sign.config.SaSignConfig;
import cn.dev33.satoken.sign.exception.SaSignException;
import cn.dev33.satoken.test.SaTokenTest;

/**
 * checkNonce 并发防重放测试：同一 nonce 在并发场景下也只能通过一次校验。
 *
 * <p>
 * 		DAO 的 set / setIfAbsent 上注入同步栅栏，把所有线程聚合到写入步之后再放行，
 * 		确定性地放大「先查后写」实现的竞态窗口：
 * 		旧实现（get + set）下两个线程都会先查到 null 再各自写入，双双通过；
 * 		新实现（原子 setIfAbsent）下只有一个线程写入成功。
 * </p>
 *
 * @author konlue
 * @since 1.46.0
 */
@SaTokenTest
public class SaSignNonceAtomicTest {

    private static final String KEY = "SwqFmsKxcbq23";

    /** 造一个配好秘钥的签名模板实例，别污染全局 SaSignManager */
    private static SaSignTemplate template() {
        return new SaSignTemplate(new SaSignConfig().setSecretKey(KEY));
    }

    /** 在写入步注入同步栅栏的内存 DAO */
    @SuppressWarnings("serial")
    private static SaTokenDaoDefaultImpl barrierDao(int parties) {
        return new SaTokenDaoDefaultImpl() {
            final CountDownLatch arriveLatch = new CountDownLatch(parties);

            void arriveAndWait() {
                arriveLatch.countDown();
                try {
                    arriveLatch.await(2, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                }
            }

            @Override
            public void set(String key, String value, long timeout) {
                arriveAndWait();
                super.set(key, value, timeout);
            }

            @Override
            public boolean setIfAbsent(String key, String value, long timeout) {
                arriveAndWait();
                return super.setIfAbsent(key, value, timeout);
            }
        };
    }

    /** 并发重放同一 nonce，应该只有一个线程通过 checkNonce */
    @Test
    public void checkNonce_shouldOnlyAllowOnePassUnderConcurrency() throws Exception {
        int threadCount = 2;
        SaManager.setSaTokenDao(barrierDao(threadCount));

        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger passCount = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                template().checkNonce("replay-nonce-001");
                passCount.incrementAndGet();
            } catch (SaSignException ignored) {
                // 另一个线程应抛出「此 nonce 已被使用过」
            } finally {
                doneLatch.countDown();
            }
        };
        new Thread(task).start();
        new Thread(task).start();
        Assertions.assertTrue(doneLatch.await(5, TimeUnit.SECONDS), "测试线程未在限定时间内结束");

        Assertions.assertEquals(1, passCount.get(), "并发场景下同一 nonce 被多次通过校验");
    }

    /** 并发重放多个不同 nonce，每个 nonce 也只能被一个线程消费 */
    @Test
    public void checkNonce_shouldConsumeEachNonceOnlyOnce() throws Exception {
        int threadCount = 2;
        SaManager.setSaTokenDao(barrierDao(threadCount));

        for (int i = 0; i < 5; i++) {
            String nonce = "replay-nonce-round-" + i;
            CountDownLatch doneLatch = new CountDownLatch(threadCount);
            AtomicInteger passCount = new AtomicInteger(0);

            Runnable task = () -> {
                try {
                    template().checkNonce(nonce);
                    passCount.incrementAndGet();
                } catch (SaSignException ignored) {
                } finally {
                    doneLatch.countDown();
                }
            };
            new Thread(task).start();
            new Thread(task).start();
            Assertions.assertTrue(doneLatch.await(5, TimeUnit.SECONDS), "测试线程未在限定时间内结束");

            Assertions.assertEquals(1, passCount.get(), "nonce " + nonce + " 被多次通过校验");
        }
    }

}
