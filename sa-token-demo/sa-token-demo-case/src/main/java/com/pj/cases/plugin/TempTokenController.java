package com.pj.cases.plugin;

import cn.dev33.satoken.temp.SaTempUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 测试专用 Controller 
 * @author click33
 *
 */
@RestController
@RequestMapping("/temp-token/")
public class TempTokenController {

	// 创建   浏览器访问： http://localhost:8081/temp-token/create
	@RequestMapping("create")
	public SaResult create() {
		String token = SaTempUtil.createToken(10001, 1200);
		System.out.println("创建成功：" + token);
		return SaResult.data(token);
	}

	// 创建，获取时裁剪前缀   浏览器访问： http://localhost:8081/temp-token/create2
	@RequestMapping("create2")
	public SaResult create2() {
		String token = SaTempUtil.createToken("shop_" + 1001, 1200);
		System.out.println("创建成功：" + token);
		System.out.println("获取对应值：" + SaTempUtil.parseToken(token));
		System.out.println("获取对应值，并裁剪前缀：" + SaTempUtil.parseToken(token, "shop_", Long.class));
		System.out.println("指定错误前缀来获取：" + SaTempUtil.parseToken(token, "art_", Long.class));
		return SaResult.data(token);
	}

	// 创建，回收   浏览器访问： http://localhost:8081/temp-token/create3
	@RequestMapping("create3")
	public SaResult create3() {
		String token = SaTempUtil.createToken(10003, 1200);
		System.out.println("创建成功：" + token);
		System.out.println("获取对应值：" + SaTempUtil.parseToken(token));
		SaTempUtil.deleteToken(token);
		System.out.println("回收后再获取：" + SaTempUtil.parseToken(token));
		return SaResult.data(token);
	}

	// 创建，记录索引   浏览器访问： http://localhost:8081/temp-token/create4
	@RequestMapping("create4")
	public SaResult create4() {
		String token1 = SaTempUtil.createToken(10004, 1200, true);
		String token2 = SaTempUtil.createToken(10004, 1300, true);
		String token3 = SaTempUtil.createToken(10004, -1, true);

		System.out.println("token1 剩余有效期：" + SaTempUtil.getTimeout(token1));
		System.out.println("token2 剩余有效期：" + SaTempUtil.getTimeout(token2));
		System.out.println("token3 剩余有效期：" + SaTempUtil.getTimeout(token3));

		SaTempUtil.deleteToken(token3);

		// 获取已创建的 token 列表
		System.out.println("获取已创建的 token 列表 ");
		List<String> tempTokenList = SaTempUtil.getTempTokenList(10004);
		System.out.println(tempTokenList);
		return SaResult.data(token1);
	}

}
