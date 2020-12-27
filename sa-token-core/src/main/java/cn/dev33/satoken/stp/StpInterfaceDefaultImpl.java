package cn.dev33.satoken.stp;

import java.util.ArrayList;
import java.util.List;

/**
 * 对StpInterface接口默认的实现类 
 * @author kong
 */
public class StpInterfaceDefaultImpl implements StpInterface {

	@Override
	public List<String> getPermissionList(Object loginId, String loginKey) {
		return new ArrayList<String>();
	}

	@Override
	public List<String> getRoleList(Object loginId, String loginKey) {
		return new ArrayList<String>();
	}

}
