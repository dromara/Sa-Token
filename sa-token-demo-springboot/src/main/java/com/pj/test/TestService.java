package com.pj.test;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import cn.dev33.satoken.annotation.SaCheckLogin;

/**
 * 用来测试AOP注解鉴权 
 * @author kong
 *
 */
@Service
//@SaCheckLogin
public class TestService {

	@SaCheckLogin
	public List<String> getList() {
		System.out.println("getList");
		return new ArrayList<String>();
	}
	
}
