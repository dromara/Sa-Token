package com.pj.test;

import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.apikey.annotation.SaCheckApiKey;
import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.apikey.template.SaApiKeyUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API Key 资源 相关接口
 *
 * @author click33
 */
@RestController
public class ApiKeyResourcesController {

	// 必须携带有效的 ApiKey 才能访问
	@SaCheckApiKey
	@RequestMapping("/akRes1")
	public SaResult akRes1() {
		ApiKeyModel akModel = SaApiKeyUtil.currentApiKey();
		System.out.println("当前 ApiKey: " + akModel);
		return SaResult.ok("调用成功");
	}

	// 必须携带有效的 ApiKey ，且具有 userinfo 权限
	@SaCheckApiKey(scope = "userinfo")
	@RequestMapping("/akRes2")
	public SaResult akRes2() {
		ApiKeyModel akModel = SaApiKeyUtil.currentApiKey();
		System.out.println("当前 ApiKey: " + akModel);
		return SaResult.ok("调用成功");
	}

	// 必须携带有效的 ApiKey ，且同时具有 userinfo、chat 权限
	@SaCheckApiKey(scope = {"userinfo", "chat"})
	@RequestMapping("/akRes3")
	public SaResult akRes3() {
		ApiKeyModel akModel = SaApiKeyUtil.currentApiKey();
		System.out.println("当前 ApiKey: " + akModel);
		return SaResult.ok("调用成功");
	}

	// 必须携带有效的 ApiKey ，且具有 userinfo、chat 其中之一权限
	@SaCheckApiKey(scope = {"userinfo", "chat"}, mode = SaMode.OR)
	@RequestMapping("/akRes4")
	public SaResult akRes4() {
		ApiKeyModel akModel = SaApiKeyUtil.currentApiKey();
		System.out.println("当前 ApiKey: " + akModel);
		return SaResult.ok("调用成功");
	}

}
