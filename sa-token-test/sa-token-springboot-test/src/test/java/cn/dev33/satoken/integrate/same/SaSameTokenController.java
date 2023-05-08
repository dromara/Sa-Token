package cn.dev33.satoken.integrate.same;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * same-token Controller 
 * 
 * @author click33
 *
 */
@RestController
@RequestMapping("/same/")
public class SaSameTokenController {

	// 获取信息 
	@RequestMapping("getInfo")
	public SaResult getInfo() {
		// 获取并校验same-token 
		String sameToken = SpringMVCUtil.getRequest().getHeader(SaSameUtil.SAME_TOKEN);
		SaSameUtil.checkToken(sameToken);
		// 返回信息 
		return SaResult.data("info=zhangsan");
	}

	// 获取信息2 
	@RequestMapping("getInfo2")
	public SaResult getInfo2() {
		// 获取并校验same-token 
		SaSameUtil.checkCurrentRequestToken();
		// 返回信息 
		return SaResult.data("info=zhangsan2");
	}
	
}
