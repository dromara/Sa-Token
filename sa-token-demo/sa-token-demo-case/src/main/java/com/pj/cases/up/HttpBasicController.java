package com.pj.cases.up;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * Sa-Token Http Basic 认证 
 * 
 * @author kong
 * @since 2022-10-17 
 */
@RestController
@RequestMapping("/basic/")
public class HttpBasicController {

	/*
	 * 测试步骤：
	 	1、访问资源接口，被拦截，无法返回数据信息    ---- http://localhost:8081/basic/getInfo
	 	2、浏览器弹出窗口，要求输入账号密码，输入：账号=sa，密码=123456，确认
	 	3、后端返回数据信息
	 	4、后续再次访问接口时，无需重复输入账号密码 
	 */

	// 资源接口  ---- http://localhost:8081/basic/getInfo
	@RequestMapping("getInfo")
	public SaResult login() {
		// 1、Http Basic 认证校验，账号=sa，密码=123456
		SaBasicUtil.check("sa:123456");
		
		// 2、返回数据 
		String data = "这是通过 Http Basic 校验后才返回的数据";
		return SaResult.data(data);
	}

}
