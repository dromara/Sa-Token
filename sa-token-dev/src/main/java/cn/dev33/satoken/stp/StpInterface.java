package cn.dev33.satoken.stp;

import java.util.List;

/**
 *  开放权限验证接口，方便重写    
 */
public interface StpInterface {
	
	/** 返回指定login_id所拥有的权限码集合   */
	public List<Object> getPermissionCodeList(Object login_id, String login_key);
	
	
}
