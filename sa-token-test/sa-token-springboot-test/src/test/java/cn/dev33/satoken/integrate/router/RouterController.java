package cn.dev33.satoken.integrate.router;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 路由鉴权测试 
 * 
 * @author kong
 *
 */
@RestController
@RequestMapping("/rt/")
public class RouterController {

    @RequestMapping("getInfo")
    public SaResult getInfo() {
    	return SaResult.ok();
    }

    @RequestMapping("getInfo*")
    public SaResult getInfo2() {
    	return SaResult.ok();
    }

    // 读url 
    @RequestMapping("getInfo_101")
    public SaResult getInfo_101() {
    	return SaResult.data(SaHolder.getRequest().getUrl());
    }

    // 读Cookie 
    @RequestMapping("getInfo_102")
    public SaResult getInfo_102() {
    	return SaResult.data(SaHolder.getRequest().getCookieValue("x-token"));
    }

    // 测试转发 
    @RequestMapping("getInfo_103")
    public SaResult getInfo_103() {
    	SaHolder.getRequest().forward("/rt/getInfo_102");
    	return SaResult.ok();
    }

    // 空接口 
    @RequestMapping("getInfo_200")
    public SaResult getInfo_200() {
    	return SaResult.ok();
    }
    @RequestMapping("getInfo_201")
    public SaResult getInfo_201() {
    	return SaResult.ok();
    }
    @RequestMapping("getInfo_202")
    public SaResult getInfo_202() {
    	return SaResult.ok();
    }
	@RequestMapping("login")
	public SaResult login(long id) {
		StpUtil.login(id);
		return SaResult.ok().set("token", StpUtil.getTokenValue());
	}

}
