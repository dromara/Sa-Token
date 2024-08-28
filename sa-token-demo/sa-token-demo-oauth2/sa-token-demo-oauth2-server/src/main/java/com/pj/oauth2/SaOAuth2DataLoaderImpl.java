package com.pj.oauth2;

import cn.dev33.satoken.oauth2.consts.GrantType;
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
					.addAllowRedirectUris("*")    // 所有允许授权的 url
					.addContractScopes("openid", "userid", "userinfo", "oidc")    // 所有签约的权限
					.addAllowGrantTypes(	 // 所有允许的授权模式
							GrantType.authorization_code, // 授权码式
							GrantType.implicit,  // 隐式式
							GrantType.refresh_token,  // 刷新令牌
							GrantType.password,  // 密码式
							GrantType.client_credentials,  // 客户端模式
							"phone_code"  // 自定义授权模式 手机号验证码登录
					)
			;
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
