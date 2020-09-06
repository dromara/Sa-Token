package cn.dev33.satoken.stp;

import java.util.List;

/**
 * 开放权限验证接口，方便重写   
 * @author kong
 */
public interface StpInterface {
	
	/**
	 * 返回指定login_id所拥有的权限码集合
	 * @param loginId 账号id 
	 * @param loginKey 具体的stp标识 
	 * @return
	 */
	public List<Object> getPermissionCodeList(Object loginId, String loginKey);
	
	
}
