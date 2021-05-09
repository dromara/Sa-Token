package cn.dev33.satoken.quick.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.dev33.satoken.quick.SaQuickManager;
import cn.dev33.satoken.quick.config.SaQuickConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;

/**
 * 登录Controller
 * @author kong
 *
 */
@Controller
public class SaQuickController {

	/**
	 * 进入登录页面
	 * @param request see note
	 * @return see note
	 */
	@GetMapping("/saLogin")
	public String saLogin(HttpServletRequest request) {
		request.setAttribute("cfg", SaQuickManager.getConfig());
		return "sa-login.html";
	}

	
	/**
	 * 登录接口
	 * @param name 账号
	 * @param pwd 密码
	 * @return 是否登录成功 
	 */
	@PostMapping("/doLogin")
	@ResponseBody
	public Map<String, Object> doLogin(String name, String pwd) {
		
		// 参数完整性校验
		if(SaFoxUtil.isEmpty(name) || SaFoxUtil.isEmpty(pwd)) {
			return getResult(500, "请输入账号和密码", null);
		}
		
		// 密码校验 
		SaQuickConfig config = SaQuickManager.getConfig();
		if(name.equals(config.getName()) && pwd.equals(config.getPwd())) {
			StpUtil.setLoginId(config.getName());
			return getResult(200, "ok", StpUtil.getTokenInfo());
		} else {
			// 校验失败 
			return getResult(500, "账号或密码输入错误", null);
		}
	}
	
	
	private Map<String, Object> getResult(int code, String msg, Object data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code);
		map.put("msg", msg);
		map.put("data", data);
		return map;
	}
	
}
