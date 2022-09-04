package cn.dev33.satoken.integrate.more;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 其它测试 
 * 
 * @author kong
 *
 */
@RestController
@RequestMapping("/more/")
public class MoreController {

	// 一些基本的测试 
    @RequestMapping("getInfo")
    public SaResult getInfo() {
    	SaRequest req = SaHolder.getRequest();
    	boolean flag = 
    			SaFoxUtil.equals(req.getParam("name"), "zhang")
    			&& SaFoxUtil.equals(req.getParam("name2", "li"), "li")
    			&& SaFoxUtil.equals(req.getParamNotNull("name"), "zhang")
    			&& req.isParam("name", "zhang")
    			&& req.isPath("/more/getInfo")
    			&& req.hasParam("name")
    			&& SaFoxUtil.equals(req.getHeader("div"), "val")
    			&& SaFoxUtil.equals(req.getHeader("div", "zhang"), "val")
    			&& SaFoxUtil.equals(req.getHeader("div2", "zhang"), "zhang")
    			;
    	
    	return SaResult.data(flag);
    }

}
