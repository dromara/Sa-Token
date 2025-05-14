package com.pj.test;

import cn.dev33.satoken.apikey.model.ApiKeyModel;
import cn.dev33.satoken.apikey.template.SaApiKeyUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * API Key 相关接口
 *
 * @author click33
 */
@RestController
public class ApiKeyController {

	// 返回当前登录用户拥有的 ApiKey 列表
	@RequestMapping("/myApiKeyList")
	public SaResult myApiKeyList() {
		List<ApiKeyModel> apiKeyList = SaApiKeyUtil.getApiKeyList(StpUtil.getLoginId());
		return SaResult.data(apiKeyList);
	}

	// 创建一个新的 ApiKey，并返回
	@RequestMapping("/createApiKey")
	public SaResult createApiKey() {
		ApiKeyModel akModel = SaApiKeyUtil.createApiKeyModel(StpUtil.getLoginId()).setTitle("test");
		SaApiKeyUtil.saveApiKey(akModel);
		return SaResult.data(akModel);
	}

	// 修改 ApiKey
	@RequestMapping("/updateApiKey")
	public SaResult updateApiKey(ApiKeyModel akModel) {
		// 先验证一下是否为本人的 ApiKey
		SaApiKeyUtil.checkApiKeyLoginId(akModel.getApiKey(), StpUtil.getLoginId());
		// 修改
		ApiKeyModel akModel2 = SaApiKeyUtil.getApiKey(akModel.getApiKey());
		akModel2.setTitle(akModel.getTitle());
		akModel2.setExpiresTime(akModel.getExpiresTime());
		akModel2.setIsValid(akModel.getIsValid());
		akModel2.setScopes(akModel.getScopes());
		SaApiKeyUtil.saveApiKey(akModel2);
		return SaResult.ok();
	}

	// 删除 ApiKey
	@RequestMapping("/deleteApiKey")
	public SaResult deleteApiKey(String apiKey) {
		SaApiKeyUtil.checkApiKeyLoginId(apiKey, StpUtil.getLoginId());
		SaApiKeyUtil.deleteApiKey(apiKey);
		return SaResult.ok();
	}

	// 删除当前用户所有 ApiKey
	@RequestMapping("/deleteMyAllApiKey")
	public SaResult deleteMyAllApiKey() {
		SaApiKeyUtil.deleteApiKeyByLoginId(StpUtil.getLoginId());
		return SaResult.ok();
	}

}
