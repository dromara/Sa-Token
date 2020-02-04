package com.pj.satoken;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpInterface;

/**
 *    自定义权限验证接口扩展 
 */
@Component	// 打开此注解，保证此类被springboot扫描，即可完成sa-token的自定义权限验证扩展 
public class StpCustom implements StpInterface {

	@Override
	public List<Object> getPermissionCodeList(Object login_id, String login_key) {
		List<Object> list = new ArrayList<Object>();
		list.add("101");
		list.add("user-add");
		list.add("user-delete");
		list.add("user-update");
		list.add("user-get");
		list.add("article-get");
		return list;
	}

}
