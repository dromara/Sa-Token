package cn.dev33.satoken.stp;

import java.util.List;

/**
 *  开放权限验证接口，方便重写    
 */
public interface StpInterface {
	
	/**
	 * 返回指定login_id所拥有的权限码集合
	 * @param login_id 账号id 
	 * @param login_key 具体的stp标识 
	 * @return
	 */
	public List<Object> getPermissionCodeList(Object login_id, String login_key);
	
	
}
