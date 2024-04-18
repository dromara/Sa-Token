/*
 * Copyright 2020-2099 sa-token.cc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dev33.satoken.integrate.more;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.dev33.satoken.httpauth.basic.SaHttpBasicUtil;
import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * 其它测试 
 * 
 * @author click33
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

    	SaHolder.getResponse().setServer("sa-server");
    	return SaResult.data(flag);
    }

	// Http Basic 认证 
    @RequestMapping("basicAuth")
    public SaResult basicAuth() {
    	SaHttpBasicUtil.check("sa:123456");
    	return SaResult.ok();
    }
    
}
