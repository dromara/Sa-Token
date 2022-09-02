package cn.dev33.satoken.springboot.satoken;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 自定义权限验证接口扩展 
 * 
 * @author Auster
 *
 */
@Component
public class StpInterfaceImpl implements StpInterface {

	/**
	 * 返回一个账号所拥有的权限码集合 
	 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		int id = SaFoxUtil.getValueByType(loginId, int.class);
		if(id == 10001) {
			return Arrays.asList("user*", "art-add", "art-delete", "art-update", "art-get");
		} else {
			return null;
		}
	}

	/**
	 * 返回一个账号所拥有的角色标识集合 
	 */
	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		int id = SaFoxUtil.getValueByType(loginId, int.class);
		if(id == 10001) {
			return Arrays.asList("admin", "super-admin");
		} else {
			return null;
		}
	}

}
