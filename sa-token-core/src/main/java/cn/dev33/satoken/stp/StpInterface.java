package cn.dev33.satoken.stp;

import java.util.List;

/**
 * 权限认证接口，实现此接口即可集成权限认证功能
 * 
 * @author kong
 */
public interface StpInterface {

	/**
	 * 返回指定账号id所拥有的权限码集合 
	 * 
	 * @param loginId  账号id
	 * @param loginType 账号类型
	 * @return 该账号id具有的权限码集合
	 */
	public List<String> getPermissionList(Object loginId, String loginType);

	/**
	 * 返回指定账号id所拥有的角色标识集合 
	 * 
	 * @param loginId  账号id
	 * @param loginType 账号类型
	 * @return 该账号id具有的角色标识集合
	 */
	public List<String> getRoleList(Object loginId, String loginType);

}
