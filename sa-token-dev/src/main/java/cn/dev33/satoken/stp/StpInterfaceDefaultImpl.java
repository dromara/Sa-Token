package cn.dev33.satoken.stp;

import java.util.ArrayList;
import java.util.List;

/**
 *  权限验证接口 ，默认实现
 * @author kong
 */
public class StpInterfaceDefaultImpl implements StpInterface {

	@Override
	public List<Object> getPermissionCodeList(Object loginId, String loginKey) {
		return new ArrayList<Object>();
	}

}
