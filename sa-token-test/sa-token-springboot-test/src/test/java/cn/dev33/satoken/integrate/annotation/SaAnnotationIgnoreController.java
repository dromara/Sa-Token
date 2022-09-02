package cn.dev33.satoken.integrate.annotation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.util.SaResult;

/**
 * 测试注解用的Controller 
 * 
 * @author kong
 * @since: 2022-9-2
 */
@SaCheckLogin
@RestController
@RequestMapping("/ig/")
public class SaAnnotationIgnoreController {

	// 需要登录后访问  
	@RequestMapping("show1")
	public SaResult show1() {
		return SaResult.ok();
	}

	// 不登录也可访问  
	@SaIgnore
	@RequestMapping("show2")
	public SaResult show2() {
		return SaResult.ok();
	}

}
