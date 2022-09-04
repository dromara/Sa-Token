package cn.dev33.satoken.integrate.router;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
