package com.pj.oauth2;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Sa-Token OAuth2：自定义数据加载器
 *
 * @author click33
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	
	// 根据 client_id 获取 Client 信息
	@Override
	public SaClientModel getClientModel(String clientId) {
		// 此为模拟数据，真实环境需要从数据库查询 
		if("1001".equals(clientId)) {
			return new SaClientModel()
					.setClientId("1001")
					.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")
					.setAllowUrl("*")
					.setContractScopes(Arrays.asList("openid", "userid", "userinfo"))
					.setIsAutoMode(true);
		}
		return null;
	}
	
	// 根据ClientId 和 LoginId 获取openid 
	@Override
	public String getOpenid(String clientId, Object loginId) {
		// 此处使用框架默认算法生成 openid，真实环境建议改为从数据库查询
		return SaOAuth2DataLoader.super.getOpenid(clientId, loginId);
	}

}
