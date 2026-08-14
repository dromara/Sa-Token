package com.pj.poc;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.exception.SaJsonConvertException;
import cn.dev33.satoken.util.SaResult;

/**
 * Snack4 AutoType 本地验证：模拟 Redis 被写入带 {@code @type} 的恶意 JSON。
 *
 * <p>攻击链：</p>
 * <pre>
 * 1. 向 Redis 写入恶意 JSON
 * 2. 应用调用 SaTokenDao.getObject(key)
 * 3. Snack4 按 {@code @type} 实例化类
 * </pre>
 */
public class Snack4AutoTypeRcePoc {

	public static final String REDIS_KEY = "satoken:poc:snack4-autotype-rce";

	private static final String PAYLOAD =
			"{\"@type\":\"com.pj.poc.Snack4RceGadget\"}";

	/**
	 * 执行 PoC，控制台输出结果；供启动入口与 HTTP 接口复用。
	 *
	 * @return 拦截成功返回 ok，未拦截返回 error
	 */
	public static SaResult run() {
		System.out.println("\n========== Snack4 AutoType 本地验证 ==========");
		System.out.println("JSON 实现 = " + SaManager.getSaJsonTemplate().getClass().getName());
		System.out.println("模拟：Redis 中存在带 @type 的 JSON");
		System.out.println("payload = " + PAYLOAD);

		SaTokenDao dao = SaManager.getSaTokenDao();
		dao.set(REDIS_KEY, PAYLOAD, 100000);
		System.out.println("已写入 Redis key: " + REDIS_KEY);

		System.out.println("即将调用 getObject() …");
		try {
			Object obj = dao.getObject(REDIS_KEY);
			System.out.println("未拦截，getObject 返回: " + (obj == null ? "null" : obj.getClass().getName()));
			System.out.println("========== 验证失败：白名单未生效 ==========\n");
			return SaResult.error("未拦截未授权 @type 反序列化");
		} catch (SaJsonConvertException e) {
			System.out.println("已拦截未授权 @type 反序列化（SaJsonStrategy 白名单）");
			System.out.println("异常: " + e.getMessage());
			System.out.println("========== 验证通过 ==========\n");
			e.printStackTrace();
			return SaResult.ok("已拦截").set("message", e.getMessage());
		}
	}

}
