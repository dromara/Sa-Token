package com.pj.oauth2;

import cn.dev33.satoken.oauth2.dataloader.SaOAuth2DataLoader;
import cn.dev33.satoken.oauth2.model.SaClientModel;
import org.springframework.stereotype.Component;

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
					.setContractScope("userinfo")
					.setIsAutoMode(true);
		}
		return null;
	}
	
	// 根据ClientId 和 LoginId 获取openid 
	@Override
	public String getOpenid(String clientId, Object loginId) {
		// 此为模拟数据，真实环境需要从数据库查询 
		return "gr_SwoIN0MC1ewxHX_vfCW3BothWDZMMtx__";
	}

}
