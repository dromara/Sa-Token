package cn.dev33.satoken.stp;

import java.util.ArrayList;
import java.util.List;

/**
 * 对StpInterface接口默认的实现类 
 * @author kong
 */
public class StpInterfaceDefaultImpl implements StpInterface {

	@Override
	public List<Object> getPermissionCodeList(Object loginId, String loginKey) {
		return new ArrayList<Object>();
	}

}
