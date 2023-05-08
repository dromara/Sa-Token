package cn.dev33.satoken.stp;

import java.util.ArrayList;
import java.util.List;

/**
 * 对 {@link StpInterface} 接口默认的实现类
 * <p>
 * 如果开发者没有实现 StpInterface 接口，则框架会使用此默认实现类，所有方法都返回空集合，即：用户不具有任何权限和角色。
 * 
 * @author click33
 * @since <= 1.34.0
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
