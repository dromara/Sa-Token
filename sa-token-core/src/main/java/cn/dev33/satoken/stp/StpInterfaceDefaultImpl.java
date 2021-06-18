package cn.dev33.satoken.stp;

import java.util.ArrayList;
import java.util.List;

/**
 * 对 {@link StpInterface} 接口默认的实现类
 * <p>
 * 如果开发者没有实现StpInterface接口，则使用此默认实现
 * 
 * @author kong
 */
public class StpInterfaceDefaultImpl implements StpInterface {

	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		return new ArrayList<String>();
	}

	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		return new ArrayList<String>();
	}

}
