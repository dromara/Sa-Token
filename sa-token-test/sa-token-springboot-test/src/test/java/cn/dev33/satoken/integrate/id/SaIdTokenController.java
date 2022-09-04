package cn.dev33.satoken.integrate.id;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.id.SaIdUtil;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * id-token Controller 
 * 
 * @author kong
 *
 */
@RestController
@RequestMapping("/id/")
public class SaIdTokenController {

	// 获取信息 
	@RequestMapping("getInfo")
	public SaResult getInfo() {
		// 获取并校验id-token 
		String idToken = SpringMVCUtil.getRequest().getHeader(SaIdUtil.ID_TOKEN);
		SaIdUtil.checkToken(idToken);
		// 返回信息 
		return SaResult.data("info=zhangsan");
	}

	// 获取信息2 
	@RequestMapping("getInfo2")
	public SaResult getInfo2() {
		// 获取并校验id-token 
		SaIdUtil.checkCurrentRequestToken();
		// 返回信息 
		return SaResult.data("info=zhangsan2");
	}
	
}
