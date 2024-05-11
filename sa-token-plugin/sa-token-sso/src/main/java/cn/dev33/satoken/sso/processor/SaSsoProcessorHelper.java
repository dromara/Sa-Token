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
package cn.dev33.satoken.sso.processor;

import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.sso.util.SaSsoConsts;
import cn.dev33.satoken.sso.name.ParamName;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;

/**
 * SSO 请求处理器，辅助方法
 * 
 * @author click33
 * @since 1.38.0
 */
public class SaSsoProcessorHelper {

	/**
	 * 封装：单点注销成功后返回结果
	 * @param req SaRequest对象
	 * @param res SaResponse对象
	 * @return 返回结果
	 */
	public static Object ssoLogoutBack(SaRequest req, SaResponse res, ParamName paramName) {

		/*
		 * 三种情况：
		 * 	1. 有back参数，值为SELF -> 回退一级并刷新
		 * 	2. 有back参数，值为url -> 跳转到此url地址
		 * 	3. 无back参数 -> 返回json数据
		 */
		String back = req.getParam(paramName.back);
		if(SaFoxUtil.isNotEmpty(back)) {
			if(back.equals(SaSsoConsts.SELF)) {
				res.setHeader("Content-Type", "text/html; charset=utf-8");
				return "<script>if(document.referrer != location.href){ location.replace(document.referrer || '/'); }</script>";
			}
			return res.redirect(back);
		} else {
			return SaResult.ok("单点注销成功");
		}
	}

}
