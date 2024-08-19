package com.pj.oauth2;

import cn.dev33.satoken.oauth2.data.loader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import org.springframework.stereotype.Component;

/**
 * Sa-Token OAuth2：自定义数据加载器
 *
 * @author click33
 */
@Component
public class SaOAuth2DataLoaderImpl implements SaOAuth2DataLoader {
	
	// 根据 clientId 获取 Client 信息
	@Override
	public SaClientModel getClientModel(String clientId) {
		// 此为模拟数据，真实环境需要从数据库查询 
		if("1001".equals(clientId)) {
			return new SaClientModel()
					.setClientId("1001")    // client id
					.setClientSecret("aaaa-bbbb-cccc-dddd-eeee")    // client 秘钥
					.addAllowUrls("*")    // 所有允许授权的 url
					.addContractScopes("openid", "userid", "userinfo")    // 所有签约的权限
					.setIsAutoMode(true);    // 是否自动判断开放的授权模式
		}
		return null;
	}
	
	// 根据 clientId 和 loginId 获取 openid
	@Override
	public String getOpenid(String clientId, Object loginId) {
		// 此处使用框架默认算法生成 openid，真实环境建议改为从数据库查询
		return SaOAuth2DataLoader.super.getOpenid(clientId, loginId);
	}

}
